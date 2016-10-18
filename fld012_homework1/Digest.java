import java.util.Arrays;
/**La classe Digest rappresenta un digest MD5. Essa è un wrapper attorno ad un 
 * array di bytes, salvati in byteArray, che permette a quest'ultimo di poter 
 * essere inserito in un ConcurrentHashMap implementando le i metodi necessari 
 * (hashCode() ed equals()). In questi metodi viene fatto utilizzo delle 
 * funzioni messe a disposizione dalla classe Arrays.  
 * @author cristian
 *
 */
public class Digest {

	public byte[] byteArray;      //array di bytes
	private int hash = 0;         //l'hash utilizzato nell'indicizzazione nell'HashMap
	
	/**Il seguente costruttore è impiegato quando vengono letti i Digest
	 * dall'input del programma.
	 * @param hex
	 */
	public Digest(String hex){
		byte[] arr = hexStringToByteArray(hex);
		this.byteArray = arr;
		hash = Arrays.hashCode(arr);		
	}
	
	/**Questo metodo è usato quando l'md5 è generato dal nostro programma.
	 * Lavorare con i byte è molto più efficiente che con le stringhe.
	 * @param arr
	 */
	public Digest(byte[] arr){
		this.byteArray = arr;
		hash = Arrays.hashCode(arr);		
	}
	
	 public int hashCode() {
	      return hash;
	 }
	 
	 public boolean equals(Object o){
		Digest s = (Digest) o;
		if(Arrays.equals(byteArray, s.byteArray)) return true;
		return false;
	 }
	 

	 private static byte[] hexStringToByteArray(String s) {
	 	    int len = s.length();
	 	    byte[] data = new byte[len / 2];
	 	    for (int i = 0; i < len; i += 2) {
	 	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	 	                             + Character.digit(s.charAt(i+1), 16));
	 	    }
	 	    return data;
	 	}
}
