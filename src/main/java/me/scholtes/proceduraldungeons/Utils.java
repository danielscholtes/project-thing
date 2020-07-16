package me.scholtes.proceduraldungeons;

public class Utils {

	public static String checkDirection(final Room room, final Floor dungeon, String roomTypeString, final Direction direction, final Direction opposite, final boolean checkNoRoom) {
		if (roomTypeString.contains(direction.toString())) {
			Room adjacentRoom = dungeon.getRooms().get((room.getX() + direction.getX()) + "_" + (room.getY() + direction.getY()));
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
