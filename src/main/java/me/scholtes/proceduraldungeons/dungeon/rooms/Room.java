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
		if (floor.getRooms().size() < floor.getMaxRooms()) {
			this.floor.getQueue().add(this);
		}
		generateRooms();
	}

	/**
	 * Checks if it can generate a new {@link Room} in a {@link Direction}, and if
	 * it can then it does
	 */
	private void generateRooms() {
		/**
		 * Generates Rooms asynchronously
		 */
		new BukkitRunnable() {
			@Override
			public void run() {
				/**
				 * Makes sure the Room didn't get overriden
				 */
                if (floor.getRooms().get(posx + "_" + posy) != getInstance()) {
                	floor.getQueue().remove(getInstance());
                    return;
                }
				String roomTypeString = roomType.toString();

				/**
				 * Checks if Direction is valid, if yes generate a new Room and
				 * if not update the available doors of this Room
				 */
				roomTypeString = checkDoors(roomTypeString, Direction.NORTH, Direction.SOUTH);
				roomTypeString = checkDoors(roomTypeString, Direction.EAST, Direction.WEST);
				roomTypeString = checkDoors(roomTypeString, Direction.SOUTH, Direction.NORTH);
				roomTypeString = checkDoors(roomTypeString, Direction.WEST, Direction.EAST);

				floor.getQueue().remove(getInstance());

				/**
				 * Updates RoomType of the Room according to previous checks
				 */
				if (roomTypeString.equals("")) {
					floor.getRooms().remove(posx + "_" + posy);
				} else {
					if (roomTypeString.startsWith("_")) {
						roomTypeString = roomTypeString.substring(1);
					}
					setRoomType(RoomType.valueOf(roomTypeString));
				}
			}
		}.runTaskLaterAsynchronously(ProceduralDungeons.getInstance(), 1L);
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

	/**
	 * Gets the instance of this {@link Room}
	 * 
	 * @return Instance of the room
	 */
	private Room getInstance() {
		return this;
	}

	/**
	 * Checks if the {@link Room} can generate a new room in the specified
	 * {@link Direction}. If so, it generates a room with a door in the
	 * opposite {@link Direction}.
	 * 
	 * @param roomTypeString The available doors of this room
	 * @param direction The {@link Direction} to check in
	 * @param opposite The opposite {@link Direction}
	 * @return A {@link String} representing the available doors
	 */
	private String checkDoors(String roomTypeString, final Direction direction, final Direction opposite) {
		final String getter = (posx + direction.getX()) + "_" + (posy + direction.getY());

		/*
		 * Checks if there is a door in this direction
		 */
		if (roomTypeString.contains(direction.toString())) {
			final Room room = floor.getRooms().get(getter);
			/*
			 * Updates available doors if there is a room in the direction that
			 * has no door in the opposite direction
			 */
			roomTypeString = DungeonUtils.checkDirection(this, floor, roomTypeString, direction, opposite, false);
			
			/*
			 * Checks if there is no room in the direction, and if not checks if
			 * it has reached the max room limit, if not it generates a new room
			 * in that direction that has a door in the opposite direction
			 */
			if (room == null) {
				if (floor.getRooms().size() < floor.getMaxRooms()) {
					RoomType randomRoomType = RoomType.values()[ThreadLocalRandom.current().nextInt(RoomType.values().length)];
					while (!randomRoomType.toString().contains(opposite.toString())) {
						randomRoomType = RoomType.values()[ThreadLocalRandom.current().nextInt(RoomType.values().length)];
					}
					System.out.println("(" + posx + "," + posy + " " + roomTypeString + ") -->" + "(" + (posx + direction.getX()) + "," + (posy + direction.getY()) + " " + randomRoomType.toString() + ")");
					floor.getRooms().put(getter, new Room(floor, randomRoomType, (posx + direction.getX()), (posy + direction.getY())));
				}
			}
		}
		return roomTypeString;
	}

}
