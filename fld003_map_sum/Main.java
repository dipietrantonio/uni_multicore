package fld003_map_sum;

import java.util.concurrent.ForkJoinPool;

public class Main {
	
	public int[] sum(int[] arr1, int[] arr2){
		ForkJoinPool fjp = new ForkJoinPool();
		int[] res = new int[arr1.length];
		VectAdd v = new VectAdd(0, arr1.length, arr1, arr2, res);
		fjp.invoke(v);
		return res;
	}
}
