package fld002_forkjoin_sum;

import java.util.concurrent.*;

public class Main {
	public static ForkJoinPool pool = new ForkJoinPool();
	
	public Main(){
		/*creaimo e inizializziamo un array*/
		int[] arr = new int[100];
		for(int i = 0; i < 100; i++) arr[i] = i;
		
		/*creiamo il task iniziale che passeremo al pool*/
		SumArray sum = new SumArray(arr, 0, 100); 
		int ris = pool.invoke(sum);
		
		System.out.println("Risultato: " + ris);
	}
}
