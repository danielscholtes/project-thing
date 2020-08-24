package me.scholtes.proceduraldungeons.dungeon.rooms;

public enum Direction {
	
	NORTH(0, 1),
	EAST(1, 0),
	SOUTH(0, -1),
	WEST(-1, 0);
	
	private int x, y;
	
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
