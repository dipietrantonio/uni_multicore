package fld009_quicksort;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class QuickSort{

	private ForkJoinPool pool;
	
	public QuickSort(ForkJoinPool pool){
		this.pool = pool;
	}
	/**Questa funzione attiva l'ordinamento sull'array input.
	 * elementi ordinati si troveranno in "output" alla fine 
	 * dell'esecuzione.
	 * @param input
	 * @return
	 */
	public int[] sort(int[] input){
		int[] out = new int[input.length];
		int[] boolean_values = new int[input.length]; //usato per il packing
		PartitionAction first = new PartitionAction(input, out, out, boolean_values, 0, input.length);
		pool.invoke(first);
		return out;
	}
	
	class PartitionAction extends RecursiveAction{

		
		private static final long serialVersionUID = 1L;
		private int[] input;
		private int[] output;
		private int[] boolean_values;
		private int lo;
		private int hi;
		private int[] work;

		/**il puntatore input e work vengono scambiati ad ogni chiamata. Il puntatore output è costante
		 * in quanto indica in quale array vanno posizionati i pivot.
		 */
		public PartitionAction(int[] input, int[] output, int[] work, int[] boolean_values, int lo, int hi){
			this.input = input;
			this.work = work;
			this.output = output;
			this.lo = lo;
			this.hi = hi;
			this.boolean_values = boolean_values;
		}
		@Override
		protected void compute() {
			if(hi > lo){
				/*Il seguente codice effettua la partition. è una particolare implementazione
				 * di advanced packing. Le classi utilizzate per rappresentare le unità di 
				 * lavoro sequenziale sono in file separati per maggiore chiarezza*
				 */
				int ind_pivot = (hi + lo) / 2; //scegliamo il pivot come l'elemento centrale
				
			    /*bottomup pass*/
				PackingSumReduce first = new PackingSumReduce(input, lo, hi, boolean_values, ind_pivot, null);
				BinaryTreeNode root = first.compute();				
				/*topdown pass*/
				root.setMajFromLeft(0);
				root.setMinFromLeft(0);
				PackingFromLeftMap second = new PackingFromLeftMap(root, work, input, lo, hi, boolean_values);
				second.compute();
				/*la posizione attuale del pivot nel nuovo array è quella definitiva e non verrà mai sovrascritta.
				 * la inseriamo quindi nell'array finale.
				 * getMinorSum nella radice contiene la posizione successiva a quella dell'ultimo elemento minore
				 * del pivot inserito.
				 */
				int actual_pos = root.getMinorSum() + lo;			
				output[actual_pos] = input[ind_pivot]; //Infine inseriamo il pivot al suo posto
				
				/*ora ordiniamo i sottoarray*/
				
				PartitionAction left = new PartitionAction(work, output, input, boolean_values, lo, actual_pos);
				PartitionAction right = new PartitionAction(work, output, input, boolean_values, actual_pos + 1, hi);
				left.fork();
				right.compute();
				left.join();
			}
		}		
	}
}
