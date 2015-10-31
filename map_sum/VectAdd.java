package map_sum;
import java.util.concurrent.*;
public class VectAdd extends RecursiveAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int lo, hi;
	int[] arr1, arr2, res;
	
	public VectAdd(int lo, int hi, int[] a1, int[] a2, int[] r){
		this.lo = lo;
		this.hi = hi;
		this. arr1 = a1;
		this.arr2 = a2;
		this.res = r;
	}
	
	protected void compute() {
		if(lo - hi < 10){
			for(int i = lo; i < hi; i++) res[i] = arr1[i] + arr2[i];
		}else{
			int m = (lo + hi )/2;
			VectAdd left = new VectAdd(lo, m, arr1, arr2, res);
			VectAdd right = new VectAdd(m, hi, arr1, arr2, res);
			left.fork();
			right.compute();
			left.join();
		}
	}

}
