import java.util.concurrent.ForkJoinPool;

import fld007_packing.IPackProperty;
import fld008_packing_advanced.AdvPackingClass;
import fld009_quicksort.QuickSort;

public class Main {
	
	public static void main(String[] args){
		//lets try a packing function
		ForkJoinPool p = new ForkJoinPool();
		int[] in = {20, 19, 18, 17, 16, 15, 14, 13, 12 , 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
		QuickSort quick = new QuickSort(p);
		int[] out = quick.sort(in);
		
		for(int i = 0; i < out.length; i++){
			System.out.print(out[i] + ", ");
		}
	}
}
