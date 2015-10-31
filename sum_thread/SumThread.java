package sum_thread;

public class SumThread extends java.lang.Thread{
	int lo;
	int hi;
	int[] arr;
	int ans;
	
	public SumThread(int[] a, int lo, int hi){
		this.lo = lo;
		this.hi = hi;
		this.arr = a;
	}
	
	public void run(){
		for(int i = lo; i < hi; i++){
			//ans += arr[i];
			ans += (long)(Math.sqrt(arr[i])*Math.sqrt((i)/3.14)*3.14);
		}
		
	}
}