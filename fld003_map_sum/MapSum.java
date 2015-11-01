package fld003_map_sum;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class MapSum {
	
	public int[] sum(int[] arr1, int[] arr2){
		ForkJoinPool fjp = new ForkJoinPool();
		int[] res = new int[arr1.length];
		VectorAdd v = new VectorAdd(0, arr1.length, arr1, arr2, res);
		fjp.invoke(v);
		return res;
	}
}

class VectorAdd extends RecursiveAction{

	private static final long serialVersionUID = 1L;
	int lo, hi;
	int[] arr1, arr2, res;
	
	public VectorAdd(int lo, int hi, int[] a1, int[] a2, int[] r){
		this.lo = lo;
		this.hi = hi;
		this. arr1 = a1;
		this.arr2 = a2;
		this.res = r;
	}
	
	protected void compute() {
		if(hi - lo < 10){
			for(int i = lo; i < hi; i++) res[i] = arr1[i] + arr2[i];
		}else{
			int m = (lo + hi )/2;
			VectorAdd left = new VectorAdd(lo, m, arr1, arr2, res);
			VectorAdd right = new VectorAdd(m, hi, arr1, arr2, res);
			left.fork();
			right.compute();
			left.join();
		}
	}

}
