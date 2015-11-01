package fld002_forkjoin_sum;

import java.util.concurrent.*;

public class ReduceSum {
	
	public static ForkJoinPool pool = new ForkJoinPool();
	
	public void start(){
		/*creaimo e inizializziamo un array*/
		int[] arr = new int[100];
		for(int i = 0; i < 100; i++) arr[i] = i;
		
		/*creiamo il task iniziale che passeremo al pool*/
		SumArray sum = new SumArray(arr, 0, 100); 
		int ris = pool.invoke(sum);
		
		System.out.println("Risultato: " + ris);
	}
}

class SumArray extends java.util.concurrent.RecursiveTask<Integer> {

	private static final long serialVersionUID = 1L;
	private int[] a;
	private int lo;
	private int hi;
	public int ans = 0;
	public SumArray(int[] arr, int lo, int hi){
		a = arr;
		this.lo = lo;
		this.hi = hi;		
	}
	protected Integer compute() {
		
		if((hi - lo) <= 10){ //cutoff
			for(int i = lo; i < hi; i++) ans += a[i];
		}else{
			int m = (lo + hi) / 2;
			SumArray left = new SumArray(a, lo, m);
			SumArray right = new SumArray(a, m, hi);
			left.fork();
			int c = right.compute();
			left.join();
			this.ans = c + left.ans;
		}	
		return ans;
	}	
}
