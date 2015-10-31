package esempio_uno;


public class MainClass {
	/**Metodo principale per l'esempio uno che crea i threads*/
	public static void mymain(){
		for(int i = 0; i < 20; i++){
			SimpleThread x = new SimpleThread(i);
			x.start();
		}
	}
}
