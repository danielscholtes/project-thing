package me.scholtes.proceduraldungeons;

import org.bukkit.scheduler.BukkitRunnable;

final class Room {

	private static final int AMOUNT = 40;
	private final Dungeon dungeon;
	private final int posx;
	private final int posy;
	private RoomType roomType;
	private Room this_ = this;

	Room(Dungeon dungeon, RoomType roomType, int posx, int posy) {
		this.dungeon = dungeon;
		this.roomType = roomType;
		this.posx = posx;
		this.posy = posy;
		if (dungeon.getRooms().size() < AMOUNT) {
			this.dungeon.getQueue().add(this);
		}
		generateRooms();
	}

	private void generateRooms() {
		new BukkitRunnable() {
			@Override
			public void run() {
                if (!dungeon.getRooms().get(posx + "_" + posy).equals(this_)) {
                    dungeon.getQueue().remove(this_);
                    return;
                }
				String roomTypeString = roomType.toString();

				roomTypeString = checkDirection(roomTypeString, Direction.NORTH, Direction.SOUTH, 0, 1);
				roomTypeString = checkDirection(roomTypeString, Direction.EAST, Direction.WEST, 1, 0);
				roomTypeString = checkDirection(roomTypeString, Direction.SOUTH, Direction.NORTH, 0, -1);
				roomTypeString = checkDirection(roomTypeString, Direction.WEST, Direction.EAST, -1, 0);

				dungeon.getQueue().remove(getInstance());

				if (roomTypeString.equals("")) {
					dungeon.getRooms().remove(posx + "_" + posy);
				} else {
					if (roomTypeString.startsWith("_")) {
						roomTypeString = roomTypeString.substring(1);
					}
					setRoomType(RoomType.valueOf(roomTypeString));
				}
			}
		}.runTaskLaterAsynchronously(ProceduralDungeons.getInstance(), 1L);
	}

	RoomType getRoomType() {
		return roomType;
	}

	private void setRoomType(RoomType roomType) {
		this.roomType = roomType;
	}

	private Room getInstance() {
		return this;
	}

	private String checkDirection(String roomTypeString, final Direction direction, final Direction opposite, final int incrementX, final int incrementY) {
		final String getter = (posx + incrementX) + "_" + (posy + incrementY);

		if (roomTypeString.contains(direction.toString())) {
			final Room room = dungeon.getRooms().get(getter);
			if (room != null && !room.getRoomType().toString().contains(opposite.toString())) {
				if (direction == Direction.NORTH) {
					roomTypeString = roomTypeString.replaceAll(direction.toString() + "_", "");
				} else {
					roomTypeString = roomTypeString.replaceAll("_" + direction.toString(), "");
				}
				roomTypeString = roomTypeString.replaceAll(direction.toString(), "");
			} else if (room == null) {
				if (dungeon.getRooms().size() < AMOUNT) {
					RoomType randomRoomType = RoomType.values()[ProceduralDungeons.getRandom().nextInt(RoomType.values().length)];
					while (!randomRoomType.toString().contains(opposite.toString())) {
						randomRoomType = RoomType.values()[ProceduralDungeons.getRandom().nextInt(RoomType.values().length)];
					}
					System.out.println("(" + posx + "," + posy + " " + roomTypeString + ") -->" + "(" + (posx + incrementX) + "," + (posy + incrementY) + " " + randomRoomType.toString() + ")");
					dungeon.getRooms().put(getter, new Room(dungeon, randomRoomType, (posx + incrementX), (posy + incrementY)));
				}
			}
		}

		return roomTypeString;
	}

}
