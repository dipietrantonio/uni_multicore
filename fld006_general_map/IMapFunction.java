package fld006_general_map;

public interface IMapFunction<V> {
	public boolean calculateValue(V[] input, V[] output, int lo, int hi);
}
