package me.scholtes.proceduraldungeons;

public enum Direction {
	
	NORTH(0, 0, 1, 2),
	EAST(1, 2, 0, 0),
	SOUTH(0, 0, -1, -2),
	WEST(-1, -2, 0, 0);
	
	private int x1, x2, y1, y2;
	
	Direction(int x1, int x2, int y1, int y2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}
	
	public int getX1() {
		return x1;
	}
	
	public int getX2() {
		return x2;
	}
	
	public int getY1() {
		return y1;
	}
	
	public int getY2() {
		return y2;
	}

}
