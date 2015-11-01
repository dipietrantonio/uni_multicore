package fld005_prefix_sum2;

public class BinaryTreeNode {

	private BinaryTreeNode father;
	private int sum;
	private int fromLeft;
	private int lo;
	private int hi;
	
	private BinaryTreeNode[] children;
	
	public BinaryTreeNode(BinaryTreeNode father, int lo, int hi){
		this.father = father;
		this.lo = lo;
		this.hi = hi;
		children = new BinaryTreeNode[2];
	}
	
	public void setSum(int v){sum = v;}
	
	public int getSum(){return sum;}
	
	public int getLo(){return lo;}

	public int getHi(){return hi;}
	
	public void setFromLeft(int v){fromLeft = v;}
	
	public int getFromLeft(){return fromLeft;}
	
	public BinaryTreeNode getFather(){return father;}
	
	public BinaryTreeNode getLeftChild(){return children[0];}
	
	public BinaryTreeNode getRightChild(){return children[1];}
	
	public void setLeftChild(BinaryTreeNode e){children[0] = e;}
	
	public void setRightChild(BinaryTreeNode e){children[1] = e;}
	
}
