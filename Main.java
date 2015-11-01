import java.util.concurrent.ForkJoinPool;

import fld005_prefix_sum2.PrefixSum;

public class Main {
	public static void main(String[] args){
		ForkJoinPool p = new ForkJoinPool();
		int[] in = new int[100];
		for(int i = 0; i < 100; i++) in[i] = i + 1;		
		PrefixSum ds = new PrefixSum(p, 5);
		int[] out = ds.execute(in);
		for(int i = 0; i < 100; i++) System.out.print(out[i] + ", ");
	}
}
