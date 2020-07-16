package me.scholtes.proceduraldungeons;

import org.bukkit.scheduler.BukkitRunnable;

final class Room {

	private final Floor floor;
	private final int posx;
	private final int posy;
	private RoomType roomType;

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

	private void generateRooms() {
		new BukkitRunnable() {
			@Override
			public void run() {
                if (floor.getRooms().get(posx + "_" + posy) != getInstance()) {
                	floor.getQueue().remove(getInstance());
                    return;
                }
				String roomTypeString = roomType.toString();

				roomTypeString = checkDoors(roomTypeString, Direction.NORTH, Direction.SOUTH);
				roomTypeString = checkDoors(roomTypeString, Direction.EAST, Direction.WEST);
				roomTypeString = checkDoors(roomTypeString, Direction.SOUTH, Direction.NORTH);
				roomTypeString = checkDoors(roomTypeString, Direction.WEST, Direction.EAST);

				floor.getQueue().remove(getInstance());

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
			final Room room = floor.getRooms().get(getter);
			roomTypeString = Utils.checkDirection(this, floor, roomTypeString, direction, opposite, false);
			if (room == null) {
				if (floor.getRooms().size() < floor.getMaxRooms()) {
					RoomType randomRoomType = RoomType.values()[ProceduralDungeons.getRandom().nextInt(RoomType.values().length)];
					while (!randomRoomType.toString().contains(opposite.toString())) {
						randomRoomType = RoomType.values()[ProceduralDungeons.getRandom().nextInt(RoomType.values().length)];
					}
					System.out.println("(" + posx + "," + posy + " " + roomTypeString + ") -->" + "(" + (posx + direction.getX()) + "," + (posy + direction.getY()) + " " + randomRoomType.toString() + ")");
					floor.getRooms().put(getter, new Room(floor, randomRoomType, (posx + direction.getX()), (posy + direction.getY())));
				}
			}
		}
		return roomTypeString;
	}

}
