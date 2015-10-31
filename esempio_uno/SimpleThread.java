package esempio_uno;

public class SimpleThread extends java.lang.Thread {
	int i;
	SimpleThread(int i){
		this.i = i;
	}
	
	public void run(){
		System.out.println("Thread " + i + " says hi");
		System.out.println("Thread " + i + " says bye");
	}
}


