package fld007_packing;

import java.util.concurrent.ForkJoinPool;

import fld005_prefix_sum2.PrefixSum;
import fld006_general_map.IMapFunction;
import fld006_general_map.Map;

public class PackingClass{

	private ForkJoinPool pool;     
	private int CUTOFF;
	/*Usata per verificare se un dato elemento ha una certa proprietà*/
	private IPackProperty property;
	
	public PackingClass(ForkJoinPool pool, int cutoff, IPackProperty prop){
		this.pool = pool;
		this.CUTOFF = cutoff;
	    this.property = prop;
	}
	/**La seguente classe è usata per specificare il comportamento della prima map*/
	class MapToBin implements IMapFunction{

		@Override
		public void calculateValue(int[] input, int[] output, int lo, int hi) {
			for(int i = lo; i < hi; i++){
				if(property.check(input[i])){ //check if the element meets the requisite
					output[i] = 1;
				}else{ 
					output[i] = 0;
				}
			}
		}
		
	}
	
	/**la seguente classe è usata per specificare il comportamento della seconda map*/
	class MapToResult implements IMapFunction{

		private int[] boolValues, prefixSums;
		
		public MapToResult(int[] boolValues, int[] prefixSums){
			this.boolValues = boolValues;
			this.prefixSums = prefixSums;
		}
		@Override
		public void calculateValue(int[] input, int[] output, int lo, int hi) {
			for(int i = lo; i < hi; i++){
				/*se l'i-esimo elemento è idoneo lo mettiamo nel primo slot disponibile*/
				if(boolValues[i] == 1){
					int a = prefixSums[i] - 1;
					output[a] = input[i];
				}
			}
		}
		
	}
	
	public int[] pack(int[] input){
		
		/*step 1*/
		Map Mapclass = new Map(pool, CUTOFF);
		MapToBin map1 = new MapToBin();
		int[] boolarray = Mapclass.map(input, map1);

		/*step 2*/
		PrefixSum pref = new PrefixSum(pool, CUTOFF);
		int[] prefixSums = pref.execute(boolarray);
	
		/*step 3*/
		MapToResult map2 = new MapToResult(boolarray, prefixSums);
		/*il costruttore usato di seguito può specificare la dimensione 
		 * dell'array di output*/
		int output_size = prefixSums[input.length - 1];
		int[] result = Mapclass.map(input,output_size , map2);
		return result;
		
	}

}
