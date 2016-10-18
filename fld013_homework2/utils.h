/****************************************************************************
Title: utils
Descrizione: alcune funzioni utili nell Homework 2 di Multicore
Autore: Cristian Di Pietrantonio.
****************************************************************************/
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>

#define LEXEME_LEN 30
#define LEXEME_LEN_INC 10

/*function prototypes*/
unsigned char *load_pgm(char *filename, int *rows, int *cols);
int save_pgm (unsigned char* img, int rows, int cols, char* filename);
unsigned char *create_mask(int size);

// --------------------------------------------------------------------------
//
// char* create_mask(int size)
// Descrizione: crea una matrice quadrata di lato size che rappresenta una
// maschera di blurring. Size deve essere dispari.
// Ritorna un puntatore alla matrice o NULL se si e' verificato qualche errore
//
// --------------------------------------------------------------------------
unsigned char *create_filter(int size){
   if(size % 2 == 0){
      fprintf(stderr, "Il valore in input deve essere dispari\n");
      return NULL;
   }
   unsigned char *array = calloc(size*size, sizeof(char));
   if(!array){
      fprintf(stderr, "blurmask: errore nell'allocare memoria con malloc.\n");
      return NULL;
   }
   int offset = 0;
   int row = 0;
   int currentColLeft = size / 2;
   int currentColRight = size / 2;
   while(currentColLeft >= 0){
      int i; //counter
      for(i = 0 + offset; i < size - offset; i++){
         array[row * size + currentColLeft] = 1;
         array[row * size + currentColRight] = 1;
         row++;
      }
      offset++;
      row = offset;
      currentColLeft--;
      currentColRight++;
   }
   return array;
}

// --------------------------------------------------------------------------
//
// char *__read_next_lexeme(FILE *pFile)
// Descrizione: Legge la successiva parola nel file in input.
// se incontra il carattere # ignora tutte le successive parole fino a fine riga
//
// --------------------------------------------------------------------------
unsigned char *__read_next_lexeme(FILE *pFile){
   int lex_len = LEXEME_LEN;
   unsigned char *buffer = malloc(sizeof(unsigned char) * LEXEME_LEN);
   int charsWritten = 0;
   char ch = fgetc(pFile);
   while(ch != EOF){
      if(ch == '#'){ //commento
         while((ch = fgetc(pFile)) != '\n'); //ignora tutta la linea
      }else if(isspace(ch)){ //whitespace
         if(charsWritten > 0){ //abbiamo letto un lessema
            buffer[charsWritten++] = '\0';
            return buffer;
         }
      }else{
         //controlliamo di avere abbastanza memoria
         if(charsWritten + 1 >= lex_len){
            lex_len += LEXEME_LEN_INC;
            buffer = realloc(buffer, lex_len);
            if(!buffer){
               fprintf(stderr, "Errore nell'eseguire la realloc.\n");
               return NULL;
            }
         }
         //aggiungiamo il carattere letto nel buffer
         buffer[charsWritten++] = (unsigned char) ch;
      }
      ch = fgetc(pFile); //leggi il prossimo carattere
   }
   if(charsWritten > 0) {
      buffer[charsWritten++] = '\0';
      return buffer;
   }else{
      return NULL;
   }
}

// --------------------------------------------------------------------------
//
// char *load_pgm(char *filename, int *rows, int *cols)
// carica un'immagine pgm in memoria, restituendo un puntatore all'array
// contentente l'immagine e settando i valori di rows e cols con l'altezza
// e la larghezza dell'immagine in pixel rispettivamente.
// Ritorna NULL in caso di errore.
//
// --------------------------------------------------------------------------
unsigned char *load_pgm(char *filename, int *rows, int *cols){
   //apriamo il file
   FILE *pFile = fopen(filename, "r");
   if(!pFile){
      fprintf(stderr, "Errore nell'aprire l'immagine in input.\n");
      return NULL;
   }
   //leggiamo il primo lessema. Deve essere P2, altrimenti torniamo errore
   char *lexeme = __read_next_lexeme(pFile);
   if(!lexeme){
      fprintf(stderr, "Si e' verificato un errore nel leggere il lessema.\n");
      fclose(pFile);
      return NULL;
   }
   if(strcmp(lexeme, "P2") != 0){
      fprintf(stderr, "Errore: formato file non riconosciuto (manca P2)\n");
      fclose(pFile);
      return NULL;
   }
   free(lexeme);
   //leggiamo il prossimo valore: numero di colonne
   lexeme = __read_next_lexeme(pFile);
   if(!lexeme){
      fprintf(stderr, "Si e' verificato un errore nel leggere il lessema.\n");
      fclose(pFile);
      return NULL;
   }
   *cols = atoi(lexeme);
   free(lexeme);
   //leggiamo il numero di righe
   lexeme = __read_next_lexeme(pFile);
   if(!lexeme){
      fprintf(stderr, "Si e' verificato un errore nel leggere il lessema.\n");
      fclose(pFile);
      return NULL;
   }
   *rows = atoi(lexeme);
   free(lexeme);
   //il prossimo valore e' il massimo valore che puo presentarsi nei pixel. Lo ignoriamo
   lexeme = __read_next_lexeme(pFile);
   if(!lexeme){
      fprintf(stderr, "Si e' verificato un errore nel leggere il lessema.\n");
      fclose(pFile);
      return NULL;
   }
   free(lexeme);
   //iniziamo a leggere i pixel
   int totPixels = *cols * *rows;
   int width = *cols;
   unsigned char *image = malloc(totPixels * sizeof(unsigned char));
   int i, row;
   for(i = 0, row = 0; i < totPixels; i++){
      lexeme = __read_next_lexeme(pFile);
      if(!lexeme){
            fprintf(stderr, "Errore: il numero di pixel non corrisponde.\n");
            fclose(pFile);
            return NULL;
      }
      image[i] = (unsigned char) atoi(lexeme);
      free(lexeme);
   }
   fclose(pFile);
   return image;
}

// ---------------------------------------------------------------------
//
// save_pgm(unsigned char* img, int rows, int cols, char* filename)
// Salva un immagine pgm
//
// ---------------------------------------------------------------------
int save_pgm (unsigned char* img, int rows, int cols, char* filename) {

    FILE *pFile = fopen(filename, "w");
    if(!pFile) {
      fprintf(stderr, "Errore nell'aprire l'immagine in scrittura.\n");
      return 1;
   }
    fprintf(pFile, "P2\n #Creato con la funzione di Di Pietrantonio Cristian.\n");
    fprintf(pFile, "%d %d\n", cols, rows);
    fprintf(pFile, "255\n");
    int i; //contatore
    for(i = 0; i < rows * cols; i++) fprintf(pFile, "%u\n", img[i]);
    fclose(pFile);
    return 0;
}
