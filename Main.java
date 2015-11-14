import java.util.concurrent.ForkJoinPool;

import fld007_packing.IPackProperty;
import fld008_packing_advanced.AdvPackingClass;
import fld009_quicksort.QuickSort;
import fld010_mergesort.MergeSort;
import fld011_threads_counter.ThreadsCounter;

public class Main {
	
	public static void main(String[] args){
		
		//fld011 execution example
		fld011_threads_counter.ThreadsCounter tr = new ThreadsCounter();
		try {
			tr.execute();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
