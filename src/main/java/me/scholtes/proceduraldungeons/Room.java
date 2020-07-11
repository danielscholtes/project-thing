package me.scholtes.proceduraldungeons;

import org.bukkit.scheduler.BukkitRunnable;

final class Room {

	private final int AMOUNT = 20;
	private final Dungeon dungeon;
	private final int posx;
	private final int posy;
	private RoomType roomType;

	public Room(Dungeon dungeon, RoomType roomType, int posx, int posy) {
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
                if (dungeon.getRooms().get(posx + "_" + posy) != getInstance()) {
                    dungeon.getQueue().remove(getInstance());
                    return;
                }
				String roomTypeString = roomType.toString();

				roomTypeString = checkDoors(roomTypeString, Direction.NORTH, Direction.SOUTH);
				roomTypeString = checkDoors(roomTypeString, Direction.EAST, Direction.WEST);
				roomTypeString = checkDoors(roomTypeString, Direction.SOUTH, Direction.NORTH);
				roomTypeString = checkDoors(roomTypeString, Direction.WEST, Direction.EAST);

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

	public RoomType getRoomType() {
		return roomType;
	}
	
	public int getX() {
		return posx;
	}
	
	public int getY() {
		return posy;
	}

	public void setRoomType(RoomType roomType) {
		this.roomType = roomType;
	}

	private Room getInstance() {
		return this;
	}

	private String checkDoors(String roomTypeString, final Direction direction, final Direction opposite) {
		final String getter = (posx + direction.getX()) + "_" + (posy + direction.getY());

		if (roomTypeString.contains(direction.toString())) {
			final Room room = dungeon.getRooms().get(getter);
			roomTypeString = Utils.checkDirection(this, dungeon, roomTypeString, direction, opposite, false);
			if (room == null) {
				if (dungeon.getRooms().size() < AMOUNT) {
					RoomType randomRoomType = RoomType.values()[ProceduralDungeons.getRandom().nextInt(RoomType.values().length)];
					while (!randomRoomType.toString().contains(opposite.toString())) {
						randomRoomType = RoomType.values()[ProceduralDungeons.getRandom().nextInt(RoomType.values().length)];
					}
					System.out.println("(" + posx + "," + posy + " " + roomTypeString + ") -->" + "(" + (posx + direction.getX()) + "," + (posy + direction.getY()) + " " + randomRoomType.toString() + ")");
					dungeon.getRooms().put(getter, new Room(dungeon, randomRoomType, (posx + direction.getX()), (posy + direction.getY())));
				}
			}
		}
		return roomTypeString;
	}

}
