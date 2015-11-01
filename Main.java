import java.util.concurrent.ForkJoinPool;

import fld007_packing.IPackProperty;
import fld007_packing.PackingClass;
import fld008_packing_advanced.AdvPackingClass;

public class Main {
	
	public static void main(String[] args){
		//lets try a packing function
		ForkJoinPool p = new ForkJoinPool();
		int[] in = {3, 2, 123, 1, 43, 3, 22, 87, 3, 6, 9, 23, 11, 9867, 4, 23, 57, 332, 8};
				
		AdvPackingClass packing = new AdvPackingClass(p, 7, 
				new IPackProperty() {

					public boolean check(int x) {
						return x > 10;
					}
				});
		int[] out = packing.pack(in);
		
		for(int i = 0; i < out.length; i++){
			System.out.print(out[i] + ", ");
		}
	}
}
