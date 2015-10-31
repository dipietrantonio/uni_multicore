package fld002_forkjoin_sum;

public class SumArray extends java.util.concurrent.RecursiveTask<Integer> {
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
