package fld011_threads_counter;

public class ThreadsCounter {
	/**metodo della classe principale che esegue l'esempio della somma
	 * @throws InterruptedException */
	
	private int count = 0;
	synchronized public void increment(){
		count++;
	}
	
	public void execute() throws InterruptedException{
			int n = 1000;
			int[] a = new int[n];
			for(int i = 0; i < n; i++) a[i] = i*3; //inizializziamo l'array con valori fittizi
			SumThread first = new SumThread(a, 0, n, this, 1);
			first.start();
			first.join();
			System.out.println("Somma: " + String.valueOf(first.ans) + " Thread lanciati: " + count);
		
	}
	
	class SumThread extends java.lang.Thread{
		int lo;
		int hi;
		int CUTOFF;
		int[] arr;
		int ans;
		ThreadsCounter mainPointer;
		
		public SumThread(int[] a, int lo, int hi, ThreadsCounter pointer, int CUTOFF){
			this.lo = lo;
			this.mainPointer = pointer;
			this.hi = hi;
			this.arr = a;
			this.CUTOFF = CUTOFF;
		}
		
		public void run(){
			mainPointer.increment(); //chiamata ricorsiva
			if(hi - lo > CUTOFF){
				int m = (hi + lo) / 2;
				SumThread left = new SumThread(arr, lo, m, mainPointer, CUTOFF);
				SumThread right = new SumThread(arr, m, hi, mainPointer, CUTOFF);
				left.start();
				right.start();
				try {
					left.join();
					right.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				this.ans = right.ans + left.ans;
			}else{
				for(int i = lo; i < hi; i++){
					ans += arr[i];
				}
			}
		}
	}
}
