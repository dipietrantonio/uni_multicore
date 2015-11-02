package fld009_quicksort;

import java.util.concurrent.RecursiveAction;

/**Questa classe è usata per calcolare il valore di fromleft*/
public class PackingFromLeftMap extends RecursiveAction{

	private static final long serialVersionUID = 1L;
	private BinaryTreeNode node;
	private int[] output;
	private int[] input;
	int left_limit, right_limit;
	private int[] bin; //Array di booleani per indicare l'ideoneitÃ  degli elementi 
	private int CUTOFF = 6;
	
	public PackingFromLeftMap(BinaryTreeNode node, int[] output, int[] input, int left_limit, int right_limit, int[] bin) {
		this.node = node;
		this.output = output;
		this.input = input;
		this.bin = bin;
		this.left_limit = left_limit;
		this.right_limit = right_limit;
	}
	@Override
	protected void compute(){
		int lo = node.getLo();
		int hi = node.getHi();
		
		if((hi - lo) > CUTOFF){
			BinaryTreeNode leftChild = node.getLeftChild();
			BinaryTreeNode rightChild = node.getRightChild(); //then there is also a right child
			/*set fromleft values*/
			leftChild.setMinFromLeft(node.getMinFromLeft());
			leftChild.setMajFromLeft(node.getMajFromLeft());
			rightChild.setMinFromLeft(node.getMinFromLeft() + leftChild.getMinorSum());
			rightChild.setMajFromLeft(node.getMajFromLeft() + leftChild.getMajorSum());
			/*recursive calls*/
			PackingFromLeftMap x = new PackingFromLeftMap(leftChild, output,input, left_limit, right_limit, bin);
			PackingFromLeftMap y = new PackingFromLeftMap(rightChild, output, input, left_limit, right_limit, bin);			
			
			x.fork();
			y.compute();
			x.join();
					
		}else{
			
			int t1 = node.getMajFromLeft(); //PREFIX SUM
			int t2 = node.getMinFromLeft();
			int temp1 = t1, temp2 = t2;
			for(int i = lo; i < hi; i++){
				if(bin[i]==1){
					temp1++;
					output[right_limit - temp1] = input[i]; 
				}else if(bin[i] == -1){
					temp2++;
					output[left_limit + temp2 - 1] = input[i];
				}		
			}
		}
	}	
}