package fld004_prefix_sum1;

import java.util.concurrent.RecursiveAction;


public class SumThread extends RecursiveAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int[] _input;
	int[] _output;
	int _lo, _hi, _offset, _cutoff;
	
	public SumThread(int[] input, int[] output, int lo, int hi, int offset, int cutoff){
		_input = input;
		_output = output;
		_lo = lo;
		_hi = hi;
		_cutoff = cutoff;
		_offset = offset;
	}
	
	@Override
	protected void compute() {
		
		//create recursive calls to manage subarrays
		if (_hi - _lo > _cutoff){ 
			
			int n = _input.length;
			int z = (n / _cutoff);

			SumThread[] arr_th = new SumThread[z];
			
			for(int i = 0; i < z; i++){
				arr_th[i] = new SumThread(_input, _output, i *_cutoff, (i + 1) *_cutoff, _offset, _cutoff);
				arr_th[i].fork();
			}
			for(int i = 0; i < z; i++){
				arr_th[i].join();
			}
		}else{ //sequential code
			for(int i = _lo; i < _hi; i++){
				_output[i] = _input[i];
				if((i - _offset) >= 0){
					_output[i] +=  _input[i - _offset];
				}
			}	
		}
	}
}
