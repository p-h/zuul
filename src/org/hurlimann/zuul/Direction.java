package org.hurlimann.zuul;

import java.util.HashMap;
import java.util.Map;

public enum Direction {
	NORTH("north"), EAST("east"), SOUTH("south"), WEST("west");

	private final String name;

	Direction(String name) {
		this.name = name;
		DirectionLookup.lookup.put(name, this);
	}

	public static Direction findDirection(String name) {
		return DirectionLookup.lookup.get(name);
	}

	@Override
	public String toString() {
		return name;
	}

	private static final class DirectionLookup {
		private static final Map<String, Direction> lookup = new HashMap<>();
	}
}
