package fld004_prefix_sum1;

import java.util.concurrent.ForkJoinPool;

public class Main {

	public static void startWork(){
		
		//PRECONDIZIONE: N deve essere una potenza di 2
		ForkJoinPool pool = new ForkJoinPool();
		final int CUTOFF = 2;
		final int N = 128;
		int[] input = new int[N];
		int[] output = new int[N];
		
		System.out.print("Input: ");
		
		//inizializziamo l'array di input: numeri da 1 a N
		for(int i = 0; i < N; i++){
			input[i] = i + 1;
			System.out.print(input[i] + ", ");
		}
		System.out.println(" ");
		
		//int MAX = 0;
		//Numero di passate da effettuare sull'array		
		int MAX = (int)(Math.log(N) / Math.log(2));
		
		System.out.println("Passate: " + MAX);
		
		for(int i = 0; i <= MAX; i++){
			SumThread sum = new SumThread(input, output, 0, N, (int) Math.pow(2, i), CUTOFF);
			pool.invoke(sum);
			
			//scambiamo array di input con output
			int[] temp = input;
			input = output;
			output = temp;
			
			System.out.print("Output (d = " + i + "): ");
			for(int j = 0; j < N; j++) System.out.print(input[j] + ", ");
			System.out.println(" ");
		}
		
		System.out.println(output[N-1]);
	}
}
