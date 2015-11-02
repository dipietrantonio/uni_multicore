package fld008_packing_advanced;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

import fld005_prefix_sum2.BinaryTreeNode;
import fld007_packing.IPackProperty;
/**Questa versione implementa il packing unendo la prima fase con la seconda, ossia
 *  -il mapping degli elementi dell'array di input in un array di booleani
 *  -la costruzione dell'abero esplicito bottom up durante il calcolo degli 
 *  elementi validi nei sottoarray (sommando gli elmenti dei corrispondenti dei sottoarray
 *  di booleani.
 *  
 *  Unisce inoltre la fase di discesa topdown dell'albero esplicito (per il calcolo delle
 *  somme prefisse sull'array di booleani) con il posizionamento degli elementi validi in
 *  input in un array di output, l'uno di seguito all'altro.
 * @author cristian
 *
 */
public class AdvPackingClass{

	private ForkJoinPool pool;
	private int CUTOFF; 
	private IPackProperty property;

	
	public AdvPackingClass(ForkJoinPool pool, int cutoff, IPackProperty prop){
		this.pool = pool;
		this.property = prop;
		this.CUTOFF = cutoff;
	}
	
	public int[] pack(int[] input){
		
		int len = input.length;
		int[] boolean_values = new int[len];
		
		/*bottomup pass*/
		SumReduce first = new SumReduce(input, 0, input.length, CUTOFF, boolean_values, property, null);
		BinaryTreeNode root = pool.invoke(first);

		/*topdown pass*/
		int[] output = new int[root.getSum()];
		root.setFromLeft(0);
		FromLeftMap second = new FromLeftMap(root, output, input, boolean_values, CUTOFF);
		pool.invoke(second);
		
		return output;
	}
}

/**Questa classe è usata per calcolare la somma bottom-up e costuisce un albero*/
class SumReduce extends RecursiveTask<BinaryTreeNode>{

	private static final long serialVersionUID = 1L;
	private int[] input;
	private int lo;
	private int hi;
	private int CUTOFF;
	private BinaryTreeNode father;
	private int[] boolean_values;
	private IPackProperty prop;
	
	public SumReduce(int[] input, int lo, int hi, int cutoff, int[] bool_arr, IPackProperty prop, BinaryTreeNode father){
		this.input = input;
		this.lo = lo;
		this.hi = hi;
		this.CUTOFF = cutoff;
		this.prop = prop;
		this.father = father;
		this.boolean_values = bool_arr;
	}
	
	@Override
	protected BinaryTreeNode compute() {
		
		if(hi - lo > CUTOFF){
			//recursively resolve the task
			BinaryTreeNode node = new BinaryTreeNode(father, lo, hi);
			int m = (hi + lo) / 2;
			SumReduce left = new SumReduce(input, lo, m, CUTOFF, boolean_values, prop,  node);
			SumReduce right = new SumReduce(input, m, hi, CUTOFF, boolean_values, prop, node);
			
			left.fork();
			BinaryTreeNode n2 = right.compute();			
			BinaryTreeNode n1 = left.join();
			
			//calcoliamo la somma del padre dai risultati dei figli
			node.setSum(n1.getSum() + n2.getSum());
			//impostiamo gli archi dal nodo padre ai figli
			node.setLeftChild(n1);
			node.setRightChild(n2);
			return node;
			
		}else{
			int s = 0;
			for(int i = lo; i < hi; i++){
				if(prop.check(input[i])){
					boolean_values[i] = 1; //OTTIMIZZAZIONE 1: Calcolo dell'array di booleani
					s = s + 1;
				}
			}
			BinaryTreeNode node = new BinaryTreeNode(father, lo, hi);
			node.setSum(s);
			return node;
		}
	}
	
}

/**Questa classe è usata per calcolare il valore di fromleft*/
class FromLeftMap extends RecursiveAction{

	private static final long serialVersionUID = 1L;
	private BinaryTreeNode node;
	private int[] output;
	private int[] input;
	private int[] bin; //Array di booleani per indicare l'ideoneità degli elementi 
	private int CUTOFF;
	
	public FromLeftMap(BinaryTreeNode node, int[] output, int[] input, int[] bin, int cutoff) {
		this.node = node;
		this.CUTOFF = cutoff;
		this.output = output;
		this.input = input;
		this.bin = bin;
	}
	@Override
	protected void compute(){
		int lo = node.getLo();
		int hi = node.getHi();
		
		if((hi - lo) > CUTOFF){
			BinaryTreeNode leftChild = node.getLeftChild();
			BinaryTreeNode rightChild = node.getRightChild(); //then there is also a right child
			/*set fromleft values*/
			leftChild.setFromLeft(node.getFromLeft());
			rightChild.setFromLeft(node.getFromLeft() + leftChild.getSum());
			/*recursive calls*/
			FromLeftMap x = new FromLeftMap(leftChild, output,input, bin, CUTOFF);
			FromLeftMap y = new FromLeftMap(rightChild, output, input, bin, CUTOFF);			
			
			x.fork();
			y.compute();
			x.join();
			
		}else{
			int temp = node.getFromLeft() + bin[lo]; //somma prefissa (necessaria solamente l'ultima)
			if(bin[lo] == 1) output[temp - 1] = input[lo]; //OTTIMIZZAZIONE 2: output
			
			for(int i = lo + 1; i < hi; i++){
				temp = temp + bin[i];
				if(bin[i] == 1) output[temp - 1] = input[i];				
			}
		}
	}	
}