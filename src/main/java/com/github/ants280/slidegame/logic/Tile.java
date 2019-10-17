package com.github.ants280.slidegame.logic;

import java.util.HashMap;
import java.util.Map;

public class Tile implements Comparable<Tile> {
	private final int value;
	private final String displayValue;
	private static final Map<Integer, Tile> VALUE_CACHE = new HashMap<>();
	public static final Tile TWO = new Tile(2);

	static {
		VALUE_CACHE.put(2, TWO);
	}

	Tile(int value) {
		this.value = value;
		this.displayValue = String.valueOf(value);
	}

	public int getValue() {
		return value;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public Tile getNext() {
		int nextValue = Math.multiplyExact(2, value);

		if (!VALUE_CACHE.containsKey(nextValue)) {
			VALUE_CACHE.put(nextValue, new Tile(nextValue));
		}

		return VALUE_CACHE.get(nextValue);
	}

	@Override
	public int hashCode() {
		return this.value;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || obj != null && this.getClass() == obj.getClass() && value == ((Tile) obj).value;
	}

	@Override
	public int compareTo(Tile o) {
		return value - o.value;
	}

	@Override
	public String toString() {
		return String.format("Tile{%d}", value);
	}
}
