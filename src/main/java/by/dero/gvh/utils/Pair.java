package by.dero.gvh.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
public class Pair<K, V> {
	@Getter @Setter
	private K key;
	@Getter @Setter
	private V value;
	
	public static <K, V> Pair<K, V> of(K key, V value) {
		return new Pair<>(key, value);
	}
	
	public static <K, V> Pair<K, V> of(Map.Entry<K, V> entry) {
		return new Pair<>(entry.getKey(), entry.getValue());
	}
	
	@Override
	public String toString() {
		return String.format("Pair(%s, %s)", key.toString(), value.toString());
	}
}
