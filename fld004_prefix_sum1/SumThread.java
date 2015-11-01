package fld004_prefix_sum1;

import java.util.concurrent.RecursiveAction;


public class SumThread extends RecursiveAction{

	private static final long serialVersionUID = 1L;
	int[] input;
	int[] output;
	int lo, hi, offset, cutoff;
	
	public SumThread(int[] input, int[] output, int lo, int hi, int offset, int cutoff){
		this.input = input;
		this.output = output;
		this.lo = lo;
		this.hi = hi;
		this.cutoff = cutoff;
		this.offset = offset;
	}
	
	@Override
	protected void compute() {
		
		//create recursive calls to manage subarrays
		if (hi - lo > cutoff){ 
			/*this piece of code is executed only once. We can jump directly to the tree leaves
			 * because we do not need to combine intermediate results*/
			
			int n = input.length;
			//in this way each subarray has a number of element that is less than cutoff
			int z = (n / cutoff);
			SumThread[] arr_th = new SumThread[z];
			
			for(int i = 0; i < z; i++){
				arr_th[i] = new SumThread(input, output, i *cutoff, (i + 1) *cutoff,offset, cutoff);
				arr_th[i].fork();
			}
			for(int i = 0; i < z; i++){
				arr_th[i].join();
			}
		}else{ //sequential code
			for(int i = lo; i < hi; i++){
				output[i] = input[i];
				if((i - offset) >= 0){
					output[i] +=  input[i - offset];
				}
			}	
		}
	}
}
