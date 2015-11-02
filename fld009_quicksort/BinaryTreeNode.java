package fld009_quicksort;

public class BinaryTreeNode {
	private BinaryTreeNode father;
	private int major_sum;
	private int minor_sum;
	private int minfromLeft;
	private int majfromLeft;
	private int lo;
	private int hi;
	
	private BinaryTreeNode[] children;
	
	public BinaryTreeNode(BinaryTreeNode father, int lo, int hi){
		this.father = father;
		this.lo = lo;
		this.hi = hi;
		children = new BinaryTreeNode[2];
	}
	
	public void setMajorSum(int v){major_sum = v;}
	
	public int getMajorSum(){return major_sum;}
	
	public void setMinorSum(int v){minor_sum = v;}
	
	public int getMinorSum(){return minor_sum;}
	
	public int getLo(){return lo;}

	public int getHi(){return hi;}
	
	public void setMajFromLeft(int v){majfromLeft = v;}
	
	public int getMajFromLeft(){return majfromLeft;}
	
	public void setMinFromLeft(int v){minfromLeft = v;}
	
	public int getMinFromLeft(){return minfromLeft;}
	
	public BinaryTreeNode getFather(){return father;}
	
	public BinaryTreeNode getLeftChild(){return children[0];}
	
	public BinaryTreeNode getRightChild(){return children[1];}
	
	public void setLeftChild(BinaryTreeNode e){children[0] = e;}
	
	public void setRightChild(BinaryTreeNode e){children[1] = e;}
	
}
