import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**La classe MD5Encoder è utilizzata dal programma per creare i digest*/
public class MD5Encoder{
	
	public MessageDigest md = null;

	/**L'istanza dell'agoritmo viene cercata una sola volta, al momento dell'inizializzazione
	 * dell'oggetto. In questo modo la computazione è più efficiente
	 */
	public MD5Encoder(){
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	/**Prende in input un array di bytes rappresentante una password in chiaro
	 * e ne genera il digest associato. Tale digest viene ritornato in un wrapper
	 * Digest
	 * @param text
	 * @return
	 */
	public Digest encode(byte[] text){	
		md.update(text);
		byte byteData[] = md.digest();
		return new Digest(byteData);
	}
}

