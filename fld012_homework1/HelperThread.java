import java.util.concurrent.RecursiveAction;

/**Questa classe svolge il compito di enumerare un certo intervallo
 * di stringhe assegnatogli e testare se sono delle password.
 * @author cristian
 *
 */
public class HelperThread extends RecursiveAction {

	private static final long serialVersionUID = 1L;
	private float index_start; 
	private float index_end;
	private PasswordCracker mainClass;
	public HelperThread(float start, float end, PasswordCracker pointer){
		index_start = start;
		index_end = end;
		mainClass = pointer;
	}
	
	protected void compute() {
		if(index_end - index_start > mainClass.SEQWORK){
			/*dividiamo ancora il lavoro */
			float m = (long)((index_end + index_start) / 2);
			HelperThread left = new HelperThread(index_start, m, mainClass);
			HelperThread right = new HelperThread(m+1, index_end, mainClass);
			left.fork();
			right.compute();
			left.join();
		}else{
			/*siamo nel thread che svolge il lavoro di decodifica. Creaimo una nuova
			 * istanza della classe che enumera le password.*/
			StringsGenerator str_gen = new StringsGenerator(index_start, index_end);
			MD5Encoder md5 = new MD5Encoder();
			String digestvalue; /*utilizzata per salvare la stringa ritornata dal ConcurrentHashMap.
								E' pi� efficiente che calcolarla di nuovo dall'array di bytes.*/
			
			while(str_gen.setAtNextString()){ //finchè ci sono ancora stringhe da generare nel range..
				byte[] clear = str_gen.toByteArray(); //ottieni la stringa da testare in bytes
				Digest encoded = md5.encode(clear);  
				
				if(mainClass.digests.isEmpty()){ 
					/*tutte le password sono state trovate,  inutile continuare*/
					return;
				}else if((digestvalue = mainClass.digests.get(encoded)) != null){
					/*l hashmap contiene il digest, quindi abbiamo trovato una password.
					 * Chiamiamo il metodo passwordFound della classe PasswordCraker*/
					mainClass.passwordFound(encoded, digestvalue, str_gen.toString());
				}
			}
		}
	}
	 
}
