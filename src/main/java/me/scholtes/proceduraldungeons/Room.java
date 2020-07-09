package me.scholtes.proceduraldungeons;

import org.bukkit.scheduler.BukkitRunnable;

final class Room {

    private static final int AMOUNT = 40;
    private final Dungeon dungeon;
    private final int posx;
    private final int posy;
    private RoomType roomType;

    Room(Dungeon dungeon, RoomType roomType, int posx, int posy) {
        this.dungeon = dungeon;
        this.roomType = roomType;
        this.posx = posx;
        this.posy = posy;
        if (dungeon.getRooms().size() < AMOUNT) {
            this.dungeon.getQueue().add(this);
        }
        System.out.println("new room");
        generateRooms();
    }

    private void generateRooms() {
        new BukkitRunnable() {
            @Override
            public void run() {
                String roomTypeString = roomType.toString();

                checkDirection(roomTypeString, "NORTH", "SOUTH", 0, +1);
                checkDirection(roomTypeString, "EAST", "WEST", +1, 0);
                checkDirection(roomTypeString, "SOUTH", "NORTH", 0, -1);
                checkDirection(roomTypeString, "WEST", "EAST", -1, 0);

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

    private void checkDirection(String roomTypeString, final String direction, final String opposite, final Number incrementX, final Number incrementY) {
        final String getter = (posx + incrementX.intValue()) + "_" + (posy + incrementY.intValue());

        if (roomTypeString.contains(direction)) {
            final Room room = dungeon.getRooms().get(getter);
            if (room != null && !room.getRoomType().toString().contains(opposite)) {
                if (direction.equalsIgnoreCase("NORTH")) {
                    roomTypeString = roomTypeString.replaceAll(direction + "_", "");
                } else {
                    roomTypeString = roomTypeString.replaceAll("_" + direction, "");
                }
                roomTypeString = roomTypeString.replaceAll(direction, "");
            } else if (room == null) {
                if (dungeon.getRooms().size() < AMOUNT) {
                    RoomType randomRoomType = RoomType.values()[ProceduralDungeons.getRandom().nextInt(RoomType.values().length)];
                    while (!randomRoomType.toString().contains(opposite)) {
                        randomRoomType = RoomType.values()[ProceduralDungeons.getRandom().nextInt(RoomType.values().length)];
                    }

                    dungeon.getRooms().put(getter, new Room(dungeon, randomRoomType, (posx + incrementX.intValue()), (posy + incrementY.intValue())));
                }
            }
        }
    }

}
