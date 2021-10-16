package me.scholtes.proceduraldungeons.dungeon.rooms;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.scheduler.BukkitRunnable;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.floors.Floor;
import me.scholtes.proceduraldungeons.utils.DungeonUtils;

public final class Room {

	private final Floor floor;
	private final int posx;
	private final int posy;
	private RoomType roomType;

	/**
	 * Constructor for the {@link Room}
	 * 
	 * @param floor The instance of the {@link Floor} this {@link Room} belongs to
	 * @param roomType The {@link RoomType} of this room (available doors)
	 * @param posx The X position of this room
	 * @param posy The Y position of this room
	 */
	public Room(Floor floor, RoomType roomType, int posx, int posy) {
		this.floor = floor;
		this.roomType = roomType;
		this.posx = posx;
		this.posy = posy;
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
		return posx;
	}
	
	/**
	 * Gets the Y position of this {@link Room}
	 * 
	 * @return The Y position
	 */
	public int getY() {
		return posy;
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
