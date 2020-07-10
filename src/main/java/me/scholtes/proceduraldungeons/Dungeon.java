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

	private int previousRoomSize = 0;
	private int count = 0;

	Dungeon(final ProceduralDungeons plugin) {
		this.plugin = plugin;
	}

	void generateDungeon() {
		rooms.put("0_0", new Room(this, RoomType.values()[ProceduralDungeons.getRandom().nextInt(RoomType.values().length)], 0, 0));

		new BukkitRunnable() {
			public void run() {
				if (count >= 6 && previousRoomSize == rooms.size()) {
					queue.clear();
				}
				
				count++;
				previousRoomSize = rooms.size();

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

                                generateStructure(location, x, y, room);
                            }

                            rooms.clear();
                            queue.clear();
                        }
                    }.runTask(plugin);
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

	private void generateStructure(final Location location, final int x, final int y, final String room) {

		int isValidRoom = 0;
		isValidRoom += setDirectionBlocks(location, Direction.NORTH, Direction.SOUTH, x, y, 0, 1, room);
		isValidRoom += setDirectionBlocks(location, Direction.EAST, Direction.WEST, x, y, 1, 0, room);
		isValidRoom += setDirectionBlocks(location, Direction.SOUTH, Direction.NORTH, x, y, 0, -1, room);
		isValidRoom += setDirectionBlocks(location, Direction.WEST, Direction.EAST, x, y, -1, 0, room);

		if (isValidRoom > 0) {
			if (room.equalsIgnoreCase("0_0")) {
				location.clone().add(x * 5, 0, y * 5).getBlock().setType(Material.LIME_WOOL);
			} else {
				location.clone().add(x * 5, 0, y * 5).getBlock().setType(Material.LAPIS_BLOCK);
			}
			setOutskirts(location, x, y, +2);
			setOutskirts(location, x, y, +1);
			setOutskirts(location, x, y, -1);
			setOutskirts(location, x, y, -2);
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

	private int setDirectionBlocks(final Location location, final Direction direction, final Direction opposite, final int x, final int y, final int incrementX, final int incrementY, final String room) {
		System.out.println("Checking room: " + x + "," + y + " -- " + rooms.get(room).getRoomType().toString());
		final Room adjacentRoom = rooms.get((x + incrementX) + "_" + (y + incrementY));
		if (adjacentRoom != null) {
			System.out.println("Adjacent room in " + direction.toString() + " at " + (x + incrementX) + "," + (y + incrementY));
			System.out.println("Adjacent room type is: " + adjacentRoom.getRoomType());	
		}
		if (rooms.get(room).getRoomType().toString().contains(direction.toString()) && adjacentRoom != null && adjacentRoom.getRoomType().toString().contains(opposite.toString())) {
			location.clone().add((x * 5) + direction.getX1(), 0, (y * 5) + direction.getY1()).getBlock().setType(Material.REDSTONE_BLOCK);
			location.clone().add((x * 5) + direction.getX2(), 0, (y * 5) + direction.getY2()).getBlock().setType(Material.REDSTONE_BLOCK);
			return 1;
		} else {
			location.clone().add((x * 5) + direction.getX1(), 0, (y * 5) + direction.getY1()).getBlock().setType(Material.STONE);
			location.clone().add((x * 5) + direction.getX2(), 0, (y * 5) + direction.getY2()).getBlock().setType(Material.STONE);
			return 0;
		}
	}

	private void setOutskirts(final Location location, final int x, final int y, final Number incrementY) {
		location.clone().add((x * 5) - 2, 0, (y * 5) + incrementY.intValue()).getBlock().setType(Material.STONE);
		location.clone().add((x * 5) - 1, 0, (y * 5) + incrementY.intValue()).getBlock().setType(Material.STONE);
		location.clone().add((x * 5) + 1, 0, (y * 5) + incrementY.intValue()).getBlock().setType(Material.STONE);
		location.clone().add((x * 5) + 2, 0, (y * 5) + incrementY.intValue()).getBlock().setType(Material.STONE);
	}
}
