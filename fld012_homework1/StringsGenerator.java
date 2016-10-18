/**La seguente classe associa ad ogni interno non negativo una ed una sola stringa su un alfabeto 
 * definito dal campo values, che ha cardinalità 36. Passando al costruttore di questa classe
 * due interi non negativi definiamo in maniera univoca un range di stringhe da generare.
 *  Di fatto l'intero che rappresenta l'inizio del range viene convertito in base 36 utilizzando
 *  il minimo numero di cifre e generiamo tutti i suoi successivi facendo "+1" fino a quando non
 *   raggiungiamo la fine del range.
 * Ad ogni cifra di un intero in base 36 viene associato un simbolo dell'alfabeto.
 *  Dati due interi x, y entrambi >= 0 e  x < y ne segue che vale per le rispettive 
 *  stringhe associate s ed r la seguente disuguaglianza: s <= r. La classe genera le stringhe
 *  in ordine quasi-lessicografico (prima in ordine di lunghezza, poi lessicografico).
 * @author cristian
 */
public class StringsGenerator{

	private byte[] values = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
								'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
								'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
								'u', 'v', 'w', 'x', 'y', 'z'};
	private int[] number;  //rappresenta un numero in base 36
	private byte[] str;    /*per motivi di efficienza, quando aggiorniamo number, 
	                       aggiorniamo anche la stringa che rappresenta*/
	private int counter;  //numero di stringhe generate dalla classe in un dato momento.
	private short len = 1; //lunghezza della stringa attualmente rappresentata da number e str
	private int max; //numero massimo di stringhe da generare
	
	/**Al costruttore viene passato il range di interi che corrispondono alle stringhe
	 * che l'istanza di questa classe deve generare.*/
	public StringsGenerator(float start_index, float end_index){	
			this.max = (int) (end_index - start_index + 1); //settiamo il numero di stringhe da generare
			
			/*viene calcolato il numero minimo di cifre necessario a rappresentare
			 * start_index. old_pow, e number, rappresenterà la prima stringa (a^len)
			 * di tale lunghezza.
			 */
			float pow = (float) Math.pow(36, len);
			float old_pow = 0;
			while(pow  <= start_index){
				old_pow = pow;
				pow = pow + (float) Math.pow(36, ++len);
			}
			start_index -= old_pow;
			number = new int[len];
			str = new byte[len];
			for(int i = 0; i < len; i++) str[i] = values[0];
			
			/*iniziamo la conversione in base 36*/
			float quoz = (long) (start_index / 36);
			short rest = (short) (start_index % 36);
			short digits_generated = 1;
			number[len - digits_generated] = rest;
			str[len- digits_generated] = values[rest];
			while(quoz > 0){
				rest = (short) (quoz % 36);
				quoz = (long) (quoz / 36);
				digits_generated++;
				number[len - digits_generated] = rest;
				str[len- digits_generated] = values[rest];
			}
	}
	
	/**La seguente funzione calcola "in place" la stringa successiva alla corrente secondo
	 * l'ordine quasi-lessicografico. Ritorna true se l'operazione  andata a buon fine, false
	 * se  stato raggiunto il massimo numero di stringhe che era possibile generare.
	 * @return
	 */
	public boolean setAtNextString(){
		if(counter >= max) return false;
		else if(counter == 0){
			counter++;
			return true;
		}
		counter++;
		int current_location =  len - 1;
		byte reminder = 1;
		do{
			int sum = (number[current_location] + 1);
			if(sum == 36){
				reminder = 1;
				str[current_location] = values[0];
				number[current_location--] = 0;
			}else{
				reminder=0;
				str[current_location] = values[sum];
				number[current_location] = sum;
			}
		}while(reminder != 0 && current_location >= 0);
		
		if(current_location < 0) {
			number = new int[++len];
			str = new byte[len];
			for(int i = 0; i < len; i++) str[i] = values[0];
		}
		return true;
	}
	
	public byte[] toByteArray(){
		return str;
	}
	
	public String toString(){
		return new String(str);
	}
}
