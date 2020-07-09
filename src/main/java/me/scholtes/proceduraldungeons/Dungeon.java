package me.scholtes.proceduraldungeons;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class Dungeon {

    private final ProceduralDungeons plugin;
    private final Map<String, Room> rooms = new HashMap<>();
    private final List<Room> queue = new ArrayList<>();

    Dungeon(final ProceduralDungeons plugin) {
        this.plugin = plugin;
    }

    void generateDungeon() {
        rooms.put("0_0", new Room(this, RoomType.values()[ProceduralDungeons.getRandom().nextInt(RoomType.values().length)], 0, 0));

        new BukkitRunnable() {
            public void run() {
                if (queue.isEmpty()) {
                    System.out.println("Finished generating");

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            final Location location = new Location(Bukkit.getWorld("world"), 0, 100, 0);
                            for (String room : rooms.keySet()) {
                                String[] xy = room.split("_");
                                int x = Integer.parseInt(xy[0]);
                                int y = Integer.parseInt(xy[1]);
                                System.out.println(x + "," + y);

                                int isValidRoom = 0;
                                generateStructure(location, x, y, room, isValidRoom);
                            }
                        }
                    }.runTaskLater(plugin, 1L);
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 1L);

    }

    Map<String, Room> getRooms() {
        return rooms;
    }

    List<Room> getQueue() {
        return queue;
    }

    private void generateStructure(final Location location, final int x, final int y, final String room, int isValidRoom) {

        Room adjacentRoom = rooms.get(x + "_" + (y + 1));
        if (rooms.get(room).getRoomType().toString().contains("NORTH") && adjacentRoom != null && adjacentRoom.getRoomType().toString().contains("SOUTH")) {
            isValidRoom++;
            location.clone().add(x * 5, 0, (y * 5) + 1).getBlock().setType(Material.REDSTONE_BLOCK);
            location.clone().add(x * 5, 0, (y * 5) + 2).getBlock().setType(Material.REDSTONE_BLOCK);
        } else {
            location.clone().add(x * 5, 0, (y * 5) + 1).getBlock().setType(Material.STONE);
            location.clone().add(x * 5, 0, (y * 5) + 2).getBlock().setType(Material.STONE);
        }

        adjacentRoom = rooms.get((x + 1) + "_" + y);
        if (rooms.get(room).getRoomType().toString().contains("EAST") && adjacentRoom != null && adjacentRoom.getRoomType().toString().contains("WEST")) {
            isValidRoom++;
            location.clone().add((x * 5) + 1, 0, y * 5).getBlock().setType(Material.REDSTONE_BLOCK);
            location.clone().add((x * 5) + 2, 0, y * 5).getBlock().setType(Material.REDSTONE_BLOCK);
        } else {
            location.clone().add((x * 5) + 1, 0, y * 5).getBlock().setType(Material.STONE);
            location.clone().add((x * 5) + 2, 0, y * 5).getBlock().setType(Material.STONE);
        }

        /*
        setDirectionStructure(location, "NORTH", "SOUTH", x, y, 0, +1, room, isValidRoom);
        setDirectionStructure(location, "EAST", "WEST", x, y, +1, 0, room, isValidRoom);
        setDirectionStructure(location, "SOUTH", "NORTH", x, y, 0, -1, room, isValidRoom);
        */

        adjacentRoom = rooms.get(x + "_" + (y - 1));
        if (rooms.get(room).getRoomType().toString().contains("SOUTH") && adjacentRoom != null && adjacentRoom.getRoomType().toString().contains("NORTH")) {
            isValidRoom++;
            location.clone().add(x * 5, 0, (y * 5) - 1).getBlock().setType(Material.REDSTONE_BLOCK);
            location.clone().add(x * 5, 0, (y * 5) - 2).getBlock().setType(Material.REDSTONE_BLOCK);
        } else {
            location.clone().add(x * 5, 0, (y * 5) - 1).getBlock().setType(Material.STONE);
            location.clone().add(x * 5, 0, (y * 5) - 2).getBlock().setType(Material.STONE);
        }

        /*
        adjacentRoom = rooms.get((x - 1) + "_" + y);
        if (rooms.get(room).getRoomType().toString().contains("WEST") && adjacentRoom != null && adjacentRoom.getRoomType().toString().contains("EAST")) {
            isValidRoom++;
            location.clone().add((x * 5) - 1, 0, y * 5).getBlock().setType(Material.REDSTONE_BLOCK);
            location.clone().add((x * 5) - 2, 0, y * 5).getBlock().setType(Material.REDSTONE_BLOCK);
        } else {
            location.clone().add((x * 5) - 1, 0, y * 5).getBlock().setType(Material.STONE);
            location.clone().add((x * 5) - 2, 0, y * 5).getBlock().setType(Material.STONE);
        }
        */
        setDirectionStructure(location, "WEST", "EAST", x, y, -1, 0, room, isValidRoom);


        if (isValidRoom > 0) {
            if (room.equalsIgnoreCase("0_0")) {
                location.clone().add(x * 5, 0, y * 5).getBlock().setType(Material.LIME_WOOL);
            } else {
                location.clone().add(x * 5, 0, y * 5).getBlock().setType(Material.LAPIS_BLOCK);
            }
            setOutskirts(location, x, y, + 2);
            setOutskirts(location, x, y, + 1);
            setOutskirts(location, x, y, - 1);
            setOutskirts(location, x, y, - 2);
            return;
        }

        location.clone().add(x * 5, 0, (y * 5) + 1).getBlock().setType(Material.AIR);
        location.clone().add(x * 5, 0, (y * 5) + 2).getBlock().setType(Material.AIR);
        location.clone().add((x * 5) + 1, 0, y * 5).getBlock().setType(Material.AIR);
        location.clone().add((x * 5) + 2, 0, y * 5).getBlock().setType(Material.AIR);
        location.clone().add(x * 5, 0, (y * 5) - 1).getBlock().setType(Material.AIR);
        location.clone().add(x * 5, 0, (y * 5) - 2).getBlock().setType(Material.AIR);
        location.clone().add((x * 5) - 1, 0, y * 5).getBlock().setType(Material.AIR);
        location.clone().add((x * 5) - 2, 0, y * 5).getBlock().setType(Material.AIR);
    }

    /*
        adjacentRoom = rooms.get((x - 1) + "_" + y);
        if (rooms.get(room).getRoomType().toString().contains("WEST") && adjacentRoom != null && adjacentRoom.getRoomType().toString().contains("EAST")) {
            isValidRoom++;
            location.clone().add((x * 5) - 1, 0, y * 5).getBlock().setType(Material.REDSTONE_BLOCK);
            location.clone().add((x * 5) - 2, 0, y * 5).getBlock().setType(Material.REDSTONE_BLOCK);
        } else {
            location.clone().add((x * 5) - 1, 0, y * 5).getBlock().setType(Material.STONE);
            location.clone().add((x * 5) - 2, 0, y * 5).getBlock().setType(Material.STONE);
        }
    */

    private void setDirectionStructure(final Location location, final String direction, final String opposite, final int x, final int y, final Number incrementX, final Number incrementY, final String room, int isValidRoom) {
        final Room adjacentRoom = rooms.get((x + incrementX.intValue()) + "_" + (y + incrementY.intValue()));
        final int intX = (x * 5) + incrementX.intValue();
        final int intY = (y * 5) + incrementY.intValue();
        if (rooms.get(room).getRoomType().toString().contains(direction) && adjacentRoom != null && adjacentRoom.getRoomType().toString().contains(opposite)) {
            isValidRoom++;
            location.clone().add(intX, 0, intY).getBlock().setType(Material.REDSTONE_BLOCK);
            location.clone().add(intX + incrementX.intValue(), 0, intY + incrementY.intValue()).getBlock().setType(Material.REDSTONE_BLOCK);
        } else {
            location.clone().add(intX, 0, intY).getBlock().setType(Material.STONE);
            location.clone().add(intX + incrementX.intValue(), 0, intY + incrementY.intValue()).getBlock().setType(Material.STONE);
        }
    }

    private void setOutskirts(final Location location, final int x, final int y, final Number incrementY) {
        location.clone().add((x * 5) - 2, 0, (y * 5) + incrementY.intValue()).getBlock().setType(Material.STONE);
        location.clone().add((x * 5) - 1, 0, (y * 5) + incrementY.intValue()).getBlock().setType(Material.STONE);
        location.clone().add((x * 5) + 1, 0, (y * 5) + incrementY.intValue()).getBlock().setType(Material.STONE);
        location.clone().add((x * 5) + 2, 0, (y * 5) + incrementY.intValue()).getBlock().setType(Material.STONE);
    }
}
