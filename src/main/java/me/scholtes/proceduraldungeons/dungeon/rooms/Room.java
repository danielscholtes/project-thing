package me.scholtes.proceduraldungeons.dungeon.rooms;

public final class Room {

	private final int posX;
	private final int posY;
	private RoomType roomType;

	/**
	 * Constructor for the {@link Room}
	 *
	 * @param roomType The {@link RoomType} of this room (available doors)
	 * @param posX The X position of this room
	 * @param posY The Y position of this room
	 */
	public Room(RoomType roomType, int posX, int posY) {
		this.roomType = roomType;
		this.posX = posX;
		this.posY = posY;
	}

	/**
	 * Gets the {@link RoomType} of this {@link Room}
	 * 
	 * @return The room type
	 */
	public RoomType getRoomType() {
		return roomType;
	}
	
	/**
	 * Gets the X position of this {@link Room}
	 * 
	 * @return The X position
	 */
	public int getX() {
		return posX;
	}
	
	/**
	 * Gets the Y position of this {@link Room}
	 * 
	 * @return The Y position
	 */
	public int getY() {
		return posY;
	}

	/**
	 * Sets the {@link RoomType} of this {@link Room}
	 * 
	 * @param roomType The room type
	 */
	public void setRoomType(RoomType roomType) {
		this.roomType = roomType;
	}

}
