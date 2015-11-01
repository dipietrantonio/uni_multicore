package fld005_prefix_sum2;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class PrefixSum {

	private ForkJoinPool pool;
	private int CUTOFF; 
	
	public PrefixSum(ForkJoinPool pool, int cutoff){
		this.pool = pool;
		this.CUTOFF = cutoff;
	}
	
	public int[] execute(int[] input){
		/*bottomup pass*/
		SumReduce first = new SumReduce(input, 0, input.length, CUTOFF, null);
		BinaryTreeNode root = pool.invoke(first);
		
		/*topdown pass*/
		int[] output = new int[input.length];
		root.setFromLeft(0);
		FromLeftMap second = new FromLeftMap(root, output, input, CUTOFF);
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
	
	public SumReduce(int[] input, int lo, int hi, int cutoff, BinaryTreeNode father){
		this.input = input;
		this.lo = lo;
		this.hi = hi;
		this.CUTOFF = cutoff;
		this.father = father;	
	}
	
	@Override
	protected BinaryTreeNode compute() {
		
		if(hi - lo > CUTOFF){
			//recursively resolve the task
			BinaryTreeNode node = new BinaryTreeNode(father, lo, hi);
			int m = (hi + lo) / 2;
			SumReduce left = new SumReduce(input, lo, m, CUTOFF, node);
			SumReduce right = new SumReduce(input, m, hi, CUTOFF, node);
			
			left.fork();
			BinaryTreeNode n2 = right.compute();			
			BinaryTreeNode n1 = left.join();
			//calcoliamo la somma del padre dai risultati dei figli
			node.setSum(n1.getSum()+ n2.getSum());
			//impostiamo gli archi dal nodo padre ai figli
			node.setLeftChild(n1);
			node.setRightChild(n2);
			return node;
			
		}else{
			int s = 0;
			for(int i = lo; i < hi; i++){
				s+= input[i];
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
	private int CUTOFF;
	
	public FromLeftMap(BinaryTreeNode node, int[] output, int[] input, int cutoff) {
		this.node = node;
		this.CUTOFF = cutoff;
		this.output = output;
		this.input = input;
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
			new FromLeftMap(leftChild, output,input, CUTOFF).fork();
			new FromLeftMap(rightChild, output, input, CUTOFF).fork();			
			
		}else{
			output[lo] = node.getFromLeft() + input[lo];
			for(int i = lo + 1; i < hi; i++){
				output[i] = output[i-1] + input[i];
			}
		}
	}	
}