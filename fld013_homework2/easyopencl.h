/****************EasyOpenCl**************************

Autore:           Cristian Di Pietrantonio
Versione:         1.0
Ultima modifica:  6/01/2016

Descrizione:
Questa libreria e' stata pensata per facilitare la scrittura di programmi OpenCl che usano molto spesso
la stessa sequenza di operazioni. Questo file di header contiene, oltre che ai prototipi, anche le
implementazioni. In questo modo basta semplicemente includere questo file per utilizzarle.
Ogni nome di fuzione e struttura ha il prefisso "eoc" che segnala la sua appartenenza a questa libreria.
(EasyOpenCL).

License: This library is released under the GNU License 3.0. It has been developed during the course
"Multicore Systems Programming", Department of Computer Science at La Sapienza.
*****************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>

#ifdef __APPLE__
   #include <OpenCL/opencl.h>
#else
   #include <CL/cl.h>
#endif

//le seguenti definizioni sono utilizzate quando viene fatto il parsing del programma kernel
#define MIN_KERSOURCE_LEN 2048
#define INC_KERSOURCE_LEN 1024


/*Utilizzare le seguenti definizioni come parametro per eocCreateBuffer*/
#define EOC_RDONLY 0
#define EOC_WRONLY 1
#define EOC_RDWR 2

/*definisce una struttura che contiene tutto il necessario per effettuare computazioni su una GPU*/
typedef struct eoc_gpu_device {
   cl_platform_id platform;
   cl_device_id device;
   cl_command_queue commands;
   cl_kernel kernel;
   cl_context context;
   cl_program program;
} eoc_gpu_device;

/*prototipi di funzioni pubbliche*/
eoc_gpu_device eocCreateGpuDevice();
void eocLoadAndBuildKernel(char *filename, char *kernelName, eoc_gpu_device *dev);
void eocFreeGpuDevice(eoc_gpu_device *dev);
cl_mem eocCreateBuffer(cl_context context, int flag, cl_uint size);
void eocEnqReadBuffer(cl_command_queue commands, cl_mem memObj, cl_uint size, void *data);
void eocEnqWriteBuffer(cl_command_queue commands, cl_mem memObj, cl_uint size, void *data);
void eocEnqDataParallelKernel(eoc_gpu_device *dev, int *sizes, int *offsets, unsigned int dim);

// ------------------------------------------------------------------
// __ck_err
// funzione che controlla i valori di ritorno di una funzione openCl
// ed in caso di errore stampa il messaggio e termina il progrmma.
// params:
// 1) err: valore di ritorno
// 2) msg: messaggio da mostrare in caso di errore
//
// ------------------------------------------------------------------
void __ck_err(cl_uint err, char *msg){
   if(err == CL_SUCCESS) return;
   printf("%s. Codice errore: %d\n", msg, err);
   exit(err);
}

// ------------------------------------------------------------------
//
// __ck_mem_allocation
// controlla se un'allocazione di memoria attraverso malloc o realloc e' andata a buon fine.
// Se ci sono stati problemi stampa un messaggio di errore e termina il programma.
//
// ------------------------------------------------------------------
void __ck_mem_allocation(void *pointer, char *msg){
   if(!pointer){
      printf("Errore nell'allocare memoria. %s\n", msg);
      exit(1);
   }
}
// ------------------------------------------------------------------
//
// eocFreeGpuDevice
// rilascia tutte le risorse in eoc_gpu_device*/
// params:
// 1) *dev: puntatore ad una struttura eoc_gpu_device.
//
// ------------------------------------------------------------------
void eocFreeGpuDevice(eoc_gpu_device *dev){
   clReleaseProgram(dev->program);
   clReleaseKernel(dev->kernel);
   clReleaseCommandQueue(dev->commands);
   clReleaseContext(dev->context);
   clReleaseDevice(dev->device);
}

// -----------------------------------------------------------------
//
// eocCreateGpuDevice
// ritorna una eoc_gpu_device. Questa struttura rappresenta una GPU con quasi
// tutto il necessario per eseguire una computazione; e' presa la prima gpu disponibile.
//
// ------------------------------------------------------------------
eoc_gpu_device eocCreateGpuDevice(){
   eoc_gpu_device dev;
   /*variabili temporanee*/
   cl_uint err;
   cl_int serr;
   cl_uint num_platforms;
   //vediamo quante piattaforme ci sono
   err = clGetPlatformIDs(0, NULL, &num_platforms);
   __ck_err(err, "Errore nell'ottenere il numero di piattaforme disponibili.\n");
   //richiediamo esattamente quel numero di piattaforme
   cl_platform_id *platforms = malloc(sizeof(cl_platform_id) * num_platforms);
   __ck_mem_allocation(platforms, "Punto di origine: eocCreateGpuDevice.");
   err = clGetPlatformIDs(num_platforms, platforms, NULL);
   __ck_err(err, "Errore nell'ottenere le piattaforme.");
   //cicliamo tra tutte le piattaforme finché non troviamo un device disponibile
   int i; //counter
   int found = 0; //indica se abbiamo trovato un dispositivo
   for(i = 0; i < num_platforms; i++){
      err = clGetDeviceIDs(platforms[i], CL_DEVICE_TYPE_GPU, 1, &dev.device, NULL);
      if(err == CL_SUCCESS){
         dev.platform = platforms[i];
         found = 1;
         break;
      }
   }
   free(platforms);
   if(!found){
      printf("Nessuna GPU disponibile!\n");
      exit(1);
   }
   dev.context = clCreateContext(NULL, 1, &dev.device, NULL, NULL, &serr);
   __ck_err(serr, "Error while creating the context.");
   dev.commands = clCreateCommandQueue(dev.context, dev.device, 0, &serr);
   __ck_err(serr, "Error while creating the command queue.");
   dev.context = clCreateContext(NULL, 1, &dev.device, NULL, NULL, &serr);
   __ck_err(serr, "Error while creating the context.");
   dev.commands = clCreateCommandQueue(dev.context, dev.device, 0, &serr);
   __ck_err(serr, "Error while creating the command queue.");
   dev.program = NULL;
   dev.kernel = NULL;
   return dev;
}

// ------------------------------------------------------------------
//
// __load_program_from_file
// la seguente funzione legge un programma destinato all'esecuzione su GPU da un file
// di testo e lo ritorna in un array di caratteri.
//
// ------------------------------------------------------------------
char *__load_program_from_file(char *filename){
   char *program = (char *) malloc(sizeof(char) * MIN_KERSOURCE_LEN);
   __ck_mem_allocation(program, "Punto di origine: load program from file.");
   //apriamo il file
   FILE *source = fopen(filename, "r");
   if(!source){
      printf("Error while opening the source program file.\n");
      exit(1);
   }
   if(setvbuf(source, NULL, _IOFBF, 1024)){ //settiamo il buffer
      printf("Buffering del file non riuscito. Potrebbero verificarsi problemi.\n");
   }
   char ch = fgetc(source);
   int chars_read = 1;
   int source_alloc_bytes = MIN_KERSOURCE_LEN;
   /*iniziamo a leggere il file*/
   while(ch != EOF){
      if(chars_read + 1 >= source_alloc_bytes){ //lasciamo sempre lo spazio per il terminatore di stringa
         source_alloc_bytes += INC_KERSOURCE_LEN;
         program = (char*) realloc(program, source_alloc_bytes);
         __ck_mem_allocation(program, "Generato in load program from file dalla realloc.");
      }
      program[chars_read - 1] = ch;
      ch = fgetc(source); //leggiamo un altro carattere
      chars_read++;
   }
   program[chars_read - 1] = '\0';
   fclose(source);
   source = NULL;
   return program;
}

// ------------------------------------------------------------------
//
// eocLoadAndBuildKernel
// La seguente funzione carica il codice sorgente di un programma kernel da un file ed esegue la build
// del kernel specificato come secondo parametro.
//
// ------------------------------------------------------------------

void eocLoadAndBuildKernel(char *filename, char *kernelName, eoc_gpu_device *dev){
   cl_uint err = 0;
   char *programSource = __load_program_from_file(filename);
   dev->program = clCreateProgramWithSource(dev->context, 1, (const char **) &programSource,
                                             NULL, &err);
   __ck_err(err, "Errore mentre si creava il programma da codice sorgente.");
   err = clBuildProgram(dev->program, 0, NULL, NULL, NULL, NULL);
   free(programSource);
   if(err != CL_SUCCESS){ //stampa un log della build per localizzare l'errore
     size_t len;
     char buffer[4096];
     printf("Errore nel fare la build del programma!\n");
     clGetProgramBuildInfo(dev->program, dev->device, CL_PROGRAM_BUILD_LOG, sizeof(buffer), buffer, &len);
     printf("%s\n", buffer);
     exit(1);
   }
   dev->kernel = clCreateKernel(dev->program, kernelName, &err);
   __ck_err(err, "Errore nel creare il kernel.");
}

// -------------------------------------------------------------------
//
// eocCreateBuffer
// e' un semplice wrapper intorno a clCreateBuffer che nasconde parametri non utilizzati nel corso
//
// -------------------------------------------------------------------
cl_mem eocCreateBuffer(cl_context context, int flag, cl_uint size){
   cl_mem_flags flg;
   switch (flag){
      case 0:{
         flg = CL_MEM_READ_ONLY;
         break;
         }
      case 1:{
         flg = CL_MEM_WRITE_ONLY;
         break;
         }
      default: {
         flg = CL_MEM_READ_WRITE;
      }
   }
   cl_mem temp = clCreateBuffer(context, flg, size, NULL, NULL);
   if(!temp){
      printf("Errore nel creare un buffer!\n");
      exit(1);
   }
   return temp;
}

// -------------------------------------------------------------------
//
// eocEnqWriteBuffer
// wrapper intorno a clEnqueueWriteBuffer.
//
// -------------------------------------------------------------------
void eocEnqWriteBuffer(cl_command_queue commands, cl_mem memObj, cl_uint size, void *data){
   cl_uint err;
   err = clEnqueueWriteBuffer(commands, memObj, CL_TRUE, 0, size, data, 0, NULL, NULL);
   if(err != CL_SUCCESS){
      printf("Errore nello scrivere un buffer! Numero errore: %d\n", err);
      exit(1);
   }
}

// -------------------------------------------------------------------
//
// eocEnqReadBuffer
// wrapper intorno a clEnqueueReadBuffer.
//
// -------------------------------------------------------------------
void eocEnqReadBuffer(cl_command_queue commands, cl_mem memObj, cl_uint size, void *data){
   cl_uint err;
   err = clEnqueueReadBuffer(commands, memObj, CL_TRUE, 0, size, data, 0, NULL, NULL);
   if(err != CL_SUCCESS){
      printf("Errore nel leggere un buffer dal dispositivo! Numero errore: %d\n", err);
      exit(1);
   }
}

// -------------------------------------------------------------------
//
// eocTransferToDevice
// Questa funzione e' una scorciatoia per passare dati in sola lettura al dispositivo
//
// --------------------------------------------------------------------

cl_mem eocTransferToDevice(void *data, eoc_gpu_device *dev, int size){
   cl_uint err;
   cl_mem memIn = clCreateBuffer(dev->context, CL_MEM_READ_ONLY, size, NULL, NULL);
   if(!memIn){
      printf("Errore nel creare un buffer in eocTransferToDevice.\n");
      exit(1);
   }
   err = clEnqueueWriteBuffer(dev->commands, memIn, CL_TRUE, 0, size, data, 0, NULL, NULL);
   __ck_err(err, "Errore nella scrittura del buffer in eocTransferToDevice.");
   return memIn;
}

// -------------------------------------------------------------------
//
// __eoc_sqrt
// Descrizione: funzione ausiliaria che serve a calcolare la radice
// quadrata o cubica di un numero. Tipicamente utilizzata per calcolare
// la dimensione di un work group "quadrato" o "cubico"
//
// -------------------------------------------------------------------
int __eoc_sqrt(size_t num, int dim){
   if(num == 0) return 0;
   if(dim == 1) return num;
   else if(dim == 2){
      int i = 1;
      while(i * i <= num) i++;
      return i - 1;
   }else if(dim == 3){
      int i = 1;
      while((i * i * i) <= num) i++;
      return i - 1;
   }
   return -1;
}

// -------------------------------------------------------------------
//
// eocGetWorkGroupSize
// Descrizione: Ritorna la dimensione del work group size di un dato
// dispositivo
// PUBLIC FUNCTION
//
// -------------------------------------------------------------------
size_t eocGetWorkGroupSize(eoc_gpu_device *dev){
   size_t local;
   cl_uint err;
   err = clGetKernelWorkGroupInfo(dev->kernel, dev->device, CL_KERNEL_WORK_GROUP_SIZE, sizeof(size_t),
    &local, NULL);
   __ck_err(err, "Errore nel recuperare le informazioni del workgroup.");
   return local;
}

// -------------------------------------------------------------------
//
// eocEnqDataParallelKernel
// Descrizione: mette in atto una computazione data parallel utilizzando
// kernel e dispositivo contenuti nella struttura eoc_gpu_device passata
// come primo argomento.
// PUBLIC FUNCTION
//
// -------------------------------------------------------------------
void eocEnqDataParallelKernel(eoc_gpu_device *dev, int *sizes, int *offsets, unsigned int dim){
   if(dim > 3){
      printf("Dimensione non supportata.\n");
      exit(1);
   }
   cl_uint err;
   size_t wgEdgeSize = __eoc_sqrt(eocGetWorkGroupSize(dev), dim);
   size_t *global = malloc(dim * sizeof(size_t));
   __ck_mem_allocation(global, "Generato in eocEnqDataParallelKernel.");
   size_t *local = malloc(dim * sizeof(size_t));
   __ck_mem_allocation(local, "Generato in eocEnqDataParallelKernel.");
   int i;
   size_t *stoffset = malloc(dim * sizeof(size_t));
   __ck_mem_allocation(stoffset, "Generato in eocEnqDataParallelKernel.");

   for(i = 0; i < dim; i++){
      stoffset[i] = offsets[i];
      local[i] = wgEdgeSize;
      global[i]  = ((sizes[i] + wgEdgeSize - 1) / wgEdgeSize) * wgEdgeSize;
   }
   err = clEnqueueNDRangeKernel(dev->commands, dev->kernel, dim, stoffset, global, local, 0, NULL, NULL);
   free(local);
   free(global);
   free(stoffset);
   __ck_err(err, "Errore nell'eseguire il kernel!");
}
