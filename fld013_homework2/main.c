/***********************************************************
                     Homework 2
Studente: Cristian Di Pietrantonio

Descrizione librerie usate:
   - easyopencl.h: Ho raccolto in questa libreria una serie di funzioni che velocizza
                  l'utilizzo di opencl quando si utilizzano pattern comuni e ripetitivi.
                  In questo modo si evidenziano le operazioni significative in questo programma.
                  Ogni errore che si potrebbe verificare nelle funzioni OpenCL e' catturato da questa
                  libreria la quale mostra il messaggio di errore e termina il programma.
   - utils.h:     Contiene le funzioni per caricare e salvare immagini pgm, e per creare filtri
                  di blurring.

IMPORTANTE!!: bisogna mettere il file "kernel.c" nella stessa cartella dell'eseguibile generato
dalla compilazione di questo sorgente affiché il programma funzioni.
***********************************************************/
#include <time.h>
#include <stdio.h>
#include <stdlib.h>
#include "easyopencl.h"
#include "utils.h"
// ------------------------------------------------------------------
//
// main: punto di entrata del programma.
// Parametri:
//   - argv[1]: nome del file in input
//   - argv[2]: nome del file in output
//   - argv[3]: lunghezza del lato della matrice filtro
//
// ------------------------------------------------------------------
int main(int argc, char **argv){
   /*verifichiamo che il numero di parametri sia corretto.*/
   if(argc != 4){
      printf("Errore: Numero di argomenti insufficiente.\n\nUtilizzo: \"input file\" \"output file\" " \
      "\"lunghezza_lato_matrice_filtro\"\n");
      exit(1);
   }
   /*carichiamo l'immagine in input e creiamo la matrice filtro.*/
   int cols, rows, blurSize;
   unsigned char *image = load_pgm(argv[1], &rows, &cols);

   if(!image){
      printf("Errore nel caricare l'immagine.\n");
      exit(1);
   }
   blurSize = atoi(argv[3]);
   if(blurSize % 2 == 0){
      printf("Errore: parametro che indica la lunghezza del lato della matrice filtro non valido.\n");
      exit(1);
   }
   unsigned char *filter = create_filter(blurSize);
   int i, matrixSum;
   //valore utilizzato per normalizzazione
   for(i = 0, matrixSum = 0; i < blurSize * blurSize; i++) matrixSum += filter[i];

   /*creiamo ed inizializziamo un eoc_gpu_device che contiene tutto l'occorrente
   per una computazione su GPU*/
   eoc_gpu_device dev = eocCreateGpuDevice();
   eocLoadAndBuildKernel("kernel.txt", "blur", &dev);

   //calcoliamo le dimensioni con le quali andremo a lavorare
   int inputSize = cols * rows * sizeof(unsigned char); //dimensione buffer di input
   /*calcoliamo gli offset per le due dimensioni di un NDRange. In questo modo non consideriamo
   pixel dei quali non possiamo calcolare il valore di output*/
   int offsets[2] = {blurSize / 2, blurSize / 2};
   int outputSize = (cols - blurSize + 1) * (rows - blurSize + 1);
   //spazio richiesto in memoria dalla matrice del filtro
   int filterMatrixSize = blurSize * blurSize * sizeof(unsigned char);
   //allochiamo spazio per memorizzare i risultati
   unsigned char *out = malloc(sizeof(unsigned char) * outputSize);

   cl_mem input = eocCreateBuffer(dev.context, EOC_RDONLY, inputSize);
   cl_mem output = eocCreateBuffer(dev.context, EOC_WRONLY, outputSize);
   cl_mem memFilter = eocCreateBuffer(dev.context, EOC_RDONLY, filterMatrixSize);

   //calcoliamo quanto spazio bisogna allocare in ogni workgroup per lavorare localmente
   int workgroupSize = eocGetWorkGroupSize(&dev);
   int localStorageEdgeLen =  __eoc_sqrt(workgroupSize, 2) + offsets[0] * 2;
   int localStorageSize = localStorageEdgeLen * localStorageEdgeLen;

   //impostiamo i parametri del kernel
   clSetKernelArg(dev.kernel, 0, sizeof(int), &rows);
   clSetKernelArg(dev.kernel, 1, sizeof(int), &cols);
   clSetKernelArg(dev.kernel, 2, sizeof(int), &offsets);
   clSetKernelArg(dev.kernel, 3, sizeof(cl_mem), &input);
   clSetKernelArg(dev.kernel, 4, sizeof(cl_mem), &output);
   clSetKernelArg(dev.kernel, 5, sizeof(cl_mem), &memFilter);
   /*il seguente parametro e' rappresenta l'array per salvare in memoria locale parte dell'input.
   solo in questo modo e' possibile allocare dinamicamente memoria locale in una gpu.*/
   clSetKernelArg(dev.kernel, 6, filterMatrixSize, NULL);
   clSetKernelArg(dev.kernel, 7, sizeof(int), &matrixSum);
   clSetKernelArg(dev.kernel, 8, localStorageSize, NULL);
   //passiamo l'input al device
   eocEnqWriteBuffer(dev.commands, memFilter, filterMatrixSize, filter);
   eocEnqWriteBuffer(dev.commands, input, inputSize, image);
   //offset per l'NDRange
   int sizes[2] = {rows, cols};
   //la seguente funzone crea workgroup "quadrati".
   eocEnqDataParallelKernel(&dev, sizes, offsets, 2);
   //leggiamo il risultato della computazione
   eocEnqReadBuffer(dev.commands, output, outputSize, out);
   //salviamo l'immagine con le nuove dimensioni ridotte
	 save_pgm(out, rows - offsets[0] * 2, cols - offsets[0] * 2, argv[2]);
   printf("Computazione terminata con successo! L'immagine \"%s\" e' stata creata.\n", argv[2]);
   //liberiamo le risorse
   eocFreeGpuDevice(&dev);
   clReleaseMemObject(input);
   clReleaseMemObject(output);
   clReleaseMemObject(memFilter);
   free(image);
   free(out);
   free(filter);
   return 0;
}
