package fld000_esempio;


public class FirstThreads {
	
	class SimpleThread extends java.lang.Thread {
		int i;
		SimpleThread(int i){
			this.i = i;
		}
		
		public void run(){
			System.out.println("Thread " + i + " says hi");
			System.out.println("Thread " + i + " says bye");
		}
	}
	
	/**Metodo principale per l'esempio uno che crea i threads*/
	public void start(){
		for(int i = 0; i < 20; i++){
			SimpleThread x = new SimpleThread(i);
			x.start();
		}
	}
	
	



}
