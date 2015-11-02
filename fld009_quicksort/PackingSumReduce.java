package fld009_quicksort;

import java.util.concurrent.RecursiveTask;

/**Questa classe è usata per calcolare la somma bottom-up e costuisce un albero*/
public class PackingSumReduce extends RecursiveTask<BinaryTreeNode>{

	private static final long serialVersionUID = 1L;
	private int[] input;
	private int lo;
	private int hi;
	private int CUTOFF = 6;
	private int ind_pivot;
	private BinaryTreeNode father;
	private int[] boolean_values;
	
	public PackingSumReduce(int[] input, int lo, int hi,  int[] bool_arr, int ind_pivot, BinaryTreeNode father){
		this.input = input;
		this.lo = lo;
		this.hi = hi;
		this.ind_pivot = ind_pivot;
		this.father = father;
		this.boolean_values = bool_arr;
	}
	
	@Override
	protected BinaryTreeNode compute() {
		
		if(hi - lo > CUTOFF){
			//recursively resolve the task
			BinaryTreeNode node = new BinaryTreeNode(father, lo, hi);
			int m = (hi + lo) / 2;
			PackingSumReduce left = new PackingSumReduce(input, lo, m, boolean_values, ind_pivot,  node);
			PackingSumReduce right = new PackingSumReduce(input, m, hi, boolean_values, ind_pivot, node);
			
			left.fork();
			BinaryTreeNode n2 = right.compute();			
			BinaryTreeNode n1 = left.join();
			//calcoliamo la somma del padre dai risultati dei figli
			node.setMajorSum(n1.getMajorSum()+ n2.getMajorSum());
			node.setMinorSum(n1.getMinorSum()+ n2.getMinorSum());
			//impostiamo gli archi dal nodo padre ai figli
			node.setLeftChild(n1);
			node.setRightChild(n2);
			return node;
			
		}else{
			/*mappiamo in contemporanea gli elementi maggiori e minori
			 * nell'array di valori -1 0 1. Il pivot sarà l'unico
			 * ad avere associato il valore 0.
			 */
			int n_maggiori = 0;
			int n_minori = 0;
			for(int i = lo; i < hi; i++){
				if(input[i] > input[ind_pivot]){
					boolean_values[i] = 1;
					n_maggiori += 1;
				}else if(input[i] <= input[ind_pivot]  && i != ind_pivot){
					boolean_values[i] = -1;
					n_minori += 1;
				}
				
			}
			BinaryTreeNode node = new BinaryTreeNode(father, lo, hi);
			node.setMajorSum(n_maggiori);
			node.setMinorSum(n_minori);
			
			return node;
		}
	}
	
}
