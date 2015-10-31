package sum_thread;

public class MainClass {
	/**metodo della classe principale che esegue l'esempio della somma
	 * @throws InterruptedException */
	public static void mymain(String[] args) throws InterruptedException{
			int n = Integer.parseInt(args[0]); //numero interi da sommare
			int t = Integer.parseInt(args[1]); //numero threads
			int[] a = new int[n];
			int iter = 30; //numero di iterazioni del programma
			
			for(int i = 0; i < n; i++) a[i] = i + 1; //inizializziamo l'array
			
			for(int j = 0; j < iter; j++){
				System.out.println("Iterazione " + String.valueOf(j));
				SumThread[] ts = new SumThread[t]; 
				for(int i = 0; i < t; i++){
					int lo = (int)((((long)i)*n)/t);
				    int hi = (int)((((long)(i+1))*n)/t);
				    ts[i] = new SumThread(a, lo, hi);
				    ts[i].start();
				}
				int an = 0;
				for(int i = 0; i < t; i++){
					ts[i].join();
					an += ts[i].ans;
				}
				System.out.println("Somma: " + String.valueOf(an));
			}
	}
}
