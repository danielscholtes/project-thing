package me.scholtes.proceduraldungeons.dungeon.rooms;

import java.util.HashMap;
import java.util.Map;

public enum Direction {
	
	NORTH(0, 1),
	EAST(1, 0),
	SOUTH(0, -1),
	WEST(-1, 0);

	private static Map<Direction, Direction> oppositeDirections = new HashMap<>();
	static {
		oppositeDirections.put(Direction.NORTH, Direction.SOUTH);
		oppositeDirections.put(Direction.SOUTH, Direction.NORTH);
		oppositeDirections.put(Direction.EAST, Direction.WEST);
		oppositeDirections.put(Direction.WEST, Direction.EAST);
	}

	public static Direction getOpposite(Direction direction) {
		return oppositeDirections.get(direction);
	}

	private final int x, y;
	
	/**
	 * Constructor for the {@link Direction}
	 * 
	 * @param x The X increment
	 * @param y The Y increment
	 */
	Direction(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Gets the X increment for this {@link Direction}
	 * 
	 * @return The X increment for this {@link Direction}
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Gets the Y increment for this {@link Direction}
	 * 
	 * @return The Y increment for this {@link Direction}
	 */
	public int getY() {
		return y;
	}

}
