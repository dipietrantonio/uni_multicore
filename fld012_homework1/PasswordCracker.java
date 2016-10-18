import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

import com.sun.jmx.snmp.Timestamp;

/**è la classe principale del programma. Carica il file di password e fa partire 
 * la computazione
 * @author cristian
 *
 */
public class PasswordCracker{

	final int BATCHSIZE = 10500000;  //10 milioni e 500 mila stringhe da processare ad ogni batch
	final int SEQWORK = 8300; 	     //ogni thread processa questo tot di stringhe sequenzialmente     
	public ConcurrentHashMap<Digest, String> digests; //contiene i digest ancora da crackare 
	
	public PasswordCracker(String File){
		digests = new ConcurrentHashMap<Digest, String>();
		readFile(File); //popoliamo l'hashmap digests
	}
	
	/**passwordFound rimuove il digest trovato dall'elenco di quelli cercati e lo
	 * stampa su stdout assieme al valore in chiaro trovato*/
	public void passwordFound(Digest key, String keyvalue, String value){
		digests.remove(key);
		System.out.println(keyvalue + " " + value);
	}
	
	
	/**Il seguente metodo legge il file in input e popola l'hashmap
	 * con i digest utilizzati come chiave
	 * @param filename
	 */
 	private void readFile(String filename){
		
		try {
			File f = new File(filename);
			FileReader reader = new FileReader(f);
			BufferedReader buff = new BufferedReader(reader);
			String currentDigest = buff.readLine();
			while(currentDigest != null){
				digests.put(new Digest(currentDigest), currentDigest);
				currentDigest = buff.readLine();
			}
			buff.close();
			reader.close();
			if(digests.isEmpty()) throw new Exception();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Il file non  stato trovato!");
		 	System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Problemi con la lettura del file!");
			System.exit(1);
		} catch (Exception e) {
			System.out.println("Nessun digest trovato!");
			System.exit(1);
			e.printStackTrace();
		}		
	}
	
 	/**start avvia la ricerca delle password utilizzando un attacco a forza bruta*/
	public void start(){
		java.util.Date date1 = new java.util.Date();
		System.out.println("Timestamp inizio: " + new Timestamp(date1.getTime()).getDate().toString()+ "\n");
		/*I seguenti due valori indicano il batch corrente*/
		float batch_start = 0;
		float batch_end = BATCHSIZE - 1;
		
		ForkJoinPool pool = new ForkJoinPool(); //gestisce il lavoro in parallelo
		
		/*iniziamo un ciclo che termina solo alla scoperta di tutte le password*/
		while(!digests.isEmpty()){			
			HelperThread start = new HelperThread(batch_start, batch_end, this);
			pool.invoke(start);
			/*impostiamo il range del prossimo batch*/
			batch_start = batch_end + 1;
			batch_end = batch_start + BATCHSIZE - 1;
		}
		
		/*se siamo arrivati a questo punto abbiamo scoperto tutte le password!
		 * Calcoliamo il tempo impiegato. e stampiamo le statistiche*/
		java.util.Date date2 = new java.util.Date();
		long totaltime = date2.getTime() - date1.getTime();
		long millisec = totaltime % 1000;
		long seconds = (totaltime / 1000) % 60;
		long minutes = (totaltime / (1000 * 60) % 60);
		long hours = (totaltime / (1000 * 60 * 60));
		
		System.out.println("\nTempo impiegato: " + hours + "h " + minutes +"m " + seconds + "s " + millisec + "ms\n");
	}
}
