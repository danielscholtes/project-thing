package me.scholtes.proceduraldungeons.utils;

import me.scholtes.proceduraldungeons.dungeon.floors.Floor;
import me.scholtes.proceduraldungeons.dungeon.rooms.Direction;
import me.scholtes.proceduraldungeons.dungeon.rooms.Room;

public class DungeonUtils {

	/**
	 * Checks if the door to a room is valid in the specified {@link Direction}
	 * 
	 * @param room The {@link Room} to check the directions for
	 * @param floor Instance of the {@link Floor} that the {@link Room} belongs to
	 * @param roomTypeString A {@link String} that represent the doors the {@link Room} has
	 * @param direction The {@link Direction} to check if adjacent position has a room
	 * @param opposite The {@link Direction} the adjacent room has to have for the door to be valid
	 * @param checkNoRoom A {@link boolean} to see if it should check if the adjacent room is null
	 * @return The final string of the room type
	 */
	public static String checkDirection(Room room, Floor floor, String roomTypeString, Direction direction, Direction opposite, boolean checkNoRoom) {
		if (roomTypeString.contains(direction.toString())) {
			Room adjacentRoom = floor.getRooms().get((room.getX() + direction.getX()) + "_" + (room.getY() + direction.getY()));
			if ((checkNoRoom && adjacentRoom == null) || (adjacentRoom != null && !adjacentRoom.getRoomType().toString().contains(opposite.toString()))) {
				if (direction == Direction.NORTH) {
					roomTypeString = roomTypeString.replaceAll(direction.toString() + "_", "");
				} else {
					roomTypeString = roomTypeString.replaceAll("_" + direction.toString(), "");
				}
				roomTypeString = roomTypeString.replaceAll(direction.toString(), "");
			}
		}
		return roomTypeString;
	}
	
}
