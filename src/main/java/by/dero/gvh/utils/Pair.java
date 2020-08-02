package by.dero.gvh.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Pair<K, V> {
	@Getter @Setter
	private K key;
	@Getter @Setter
	private V value;
	
	public static <K, V> Pair<K, V> of(K key, V value) {
		return new Pair<>(key, value);
	}
}
