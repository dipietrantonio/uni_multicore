package fld006_general_map;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class Map<V>{

	private int CUTOFF;
	private ForkJoinPool pool;
	
	public Map(ForkJoinPool pool, int cutoff){
		this.pool = pool;
		this.CUTOFF = cutoff;
	}
	
	public V[] map(V[] input, IMapFunction<V> func){
		@SuppressWarnings("unchecked")
		V[] output = (V[]) new Object[input.length];
		MapThread<V> start = new MapThread<V>(input, output, 0, input.length, CUTOFF, func);
		pool.invoke(start);
		return output;
	}
}

class MapThread<V> extends RecursiveAction{

	private static final long serialVersionUID = 1L;
	private int CUTOFF;
	private V[] input;
	private V[] output;
	private int hi;
	private int lo;
	private IMapFunction<V> func;
	
	public MapThread(V[] input, V[] output, int lo, int hi, int cutoff,  IMapFunction<V> func){
		this.input = input;
		this.output = output;
		this.lo = lo;
		this.hi = hi;
		this.CUTOFF = cutoff;
		this.func = func;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void compute() {
		// TODO Auto-generated method stub
		if(hi - lo > CUTOFF){
			//Do recursive calls!
			int tot_units = (hi - lo) / CUTOFF;
			Object[] arr = new Object[tot_units];
			
			for(int i = 0; i < tot_units; i++){
				arr[i] = new MapThread<V>(input, output, i*CUTOFF, (i+1)*CUTOFF, CUTOFF, func);
				((MapThread<V>) arr[i]).fork();
			}
			for(int i = 0; i < tot_units; i++){
				((MapThread<V>) arr[i]).join();
			}
		}else{ //sequential code
			func.calculateValue(input, output, lo, hi);
		}
	}
	
}


