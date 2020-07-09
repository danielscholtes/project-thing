package me.scholtes.proceduraldungeons;

import org.bukkit.Bukkit;

public class Room {

	private RoomType roomType;
	private final Dungeon dungeon;
	private final int posx;
	private final int posy;
	
	public Room(Dungeon dungeon, RoomType roomType, int posx, int posy) {
		this.dungeon = dungeon;
		this.roomType = roomType;
		this.posx = posx;
		this.posy = posy;
		if (dungeon.getRooms().size() < 1000) { 
			this.dungeon.getQueue().add(this);
		}
		System.out.println("new room");
		generateRooms();
	}
	
	public void generateRooms() {
		Bukkit.getScheduler().runTaskLaterAsynchronously(ProceduralDungeons.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				String roomTypeString = roomType.toString();
				
				if (roomTypeString.contains("NORTH")) {
					Room room = dungeon.getRooms().get(String.valueOf(posx) + "_" + String.valueOf(posy + 1));
					if (room != null && !room.getRoomType().toString().contains("SOUTH")) {
						roomTypeString = roomTypeString.replaceAll("NORTH_", "");
						roomTypeString = roomTypeString.replaceAll("NORTH", "");
					} else if (room == null) {
						if (dungeon.getRooms().size() < 1000) { 
							RoomType randomRoomType = RoomType.values()[ProceduralDungeons.getRandom().nextInt(RoomType.values().length)];
							while (!randomRoomType.toString().contains("SOUTH")) {
								randomRoomType = RoomType.values()[ProceduralDungeons.getRandom().nextInt(RoomType.values().length)];
							}

							dungeon.getRooms().put(String.valueOf(posx) + "_" + String.valueOf(posy + 1), new Room(dungeon, randomRoomType, posx, posy + 1));
						}
					}
				}
				
				if (roomTypeString.contains("EAST")) {
					Room room = dungeon.getRooms().get(String.valueOf(posx + 1) + "_" + String.valueOf(posy));
					if (room != null && !room.getRoomType().toString().contains("WEST")) {
						roomTypeString = roomTypeString.replaceAll("_EAST", "");
						roomTypeString = roomTypeString.replaceAll("EAST", "");
					} else if (room == null) {
						if (dungeon.getRooms().size() < 1000) { 
							RoomType randomRoomType = RoomType.values()[ProceduralDungeons.getRandom().nextInt(RoomType.values().length)];
							while (!randomRoomType.toString().contains("WEST")) {
								randomRoomType = RoomType.values()[ProceduralDungeons.getRandom().nextInt(RoomType.values().length)];
							}

							dungeon.getRooms().put(String.valueOf(posx + 1) + "_" + String.valueOf(posy), new Room(dungeon, randomRoomType, posx + 1, posy));
						}
					}
				}
				
				if (roomTypeString.contains("SOUTH")) {
					Room room = dungeon.getRooms().get(String.valueOf(posx) + "_" + String.valueOf(posy - 1));
					if (room != null && !room.getRoomType().toString().contains("NORTH")) {
						roomTypeString = roomTypeString.replaceAll("_SOUTH", "");
						roomTypeString = roomTypeString.replaceAll("SOUTH", "");
					} else if (room == null) {
						if (dungeon.getRooms().size() < 1000) {
							RoomType randomRoomType = RoomType.values()[ProceduralDungeons.getRandom().nextInt(RoomType.values().length)];
							while (!randomRoomType.toString().contains("NORTH")) {
								randomRoomType = RoomType.values()[ProceduralDungeons.getRandom().nextInt(RoomType.values().length)];
							}

							dungeon.getRooms().put(String.valueOf(posx) + "_" + String.valueOf(posy - 1), new Room(dungeon, randomRoomType, posx, posy - 1));
						}
					}
				}
				
				if (roomTypeString.contains("WEST")) {
					Room room = dungeon.getRooms().get(String.valueOf(posx - 1) + "_" + String.valueOf(posy));
					if (room != null && !room.getRoomType().toString().contains("EAST")) {
						roomTypeString = roomTypeString.replaceAll("_WEST", "");
						roomTypeString = roomTypeString.replaceAll("WEST", "");
					} else if (room == null) {
						if (dungeon.getRooms().size() < 1000) {
							RoomType randomRoomType = RoomType.values()[ProceduralDungeons.getRandom().nextInt(RoomType.values().length)];
							while (!randomRoomType.toString().contains("EAST")) {
								randomRoomType = RoomType.values()[ProceduralDungeons.getRandom().nextInt(RoomType.values().length)];
							}

							dungeon.getRooms().put(String.valueOf(posx - 1) + "_" + String.valueOf(posy), new Room(dungeon, randomRoomType, posx - 1, posy));
						}
					}
				}

				dungeon.getQueue().remove(getInstance());
				
				if (roomTypeString.equals("")) {
					dungeon.getRooms().remove(String.valueOf(posx) + "_" + String.valueOf(posy));
				} else {
					if (roomTypeString.startsWith("_")) {
						roomTypeString = roomTypeString.substring(1, roomTypeString.length());
					}
					setRoomType(RoomType.valueOf(roomTypeString));
				}
				
			}
		}, 1);
	}
	
	public void setRoomType(RoomType roomType) {
		this.roomType = roomType;
	}
	
	public RoomType getRoomType() {
		return roomType;
	}
	
	public Room getInstance() {
		return this;
	}
	
}
