package me.scholtes.proceduraldungeons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

public class Dungeon {
	
	private ProceduralDungeons plugin;
	
	private Map<String, Room> rooms = new HashMap<String, Room>();
	private List<Room> queue = new ArrayList<Room>();
	
	public Dungeon(ProceduralDungeons plugin) {
		this.plugin = plugin;
	}
	
	public void generateDungeon() {
		
		rooms.put("0_0", new Room(this, RoomType.values()[ProceduralDungeons.getRandom().nextInt(RoomType.values().length)], 0, 0));
		
		new BukkitRunnable() {
			
			public void run() {
				
				System.out.println("START ---- " + queue.toArray() + " ---- FINISH");
				
				if (queue.isEmpty()) {
					System.out.println("Finished generating");
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							
							Location location = new Location(Bukkit.getWorld("world"), 0, 100, 0);
							
							for (String room : rooms.keySet()) {
								System.out.println("doing placing");
								String[] xy = room.split("_");
								int x = Integer.parseInt(xy[0]);
								int y = Integer.parseInt(xy[1]);
								
								System.out.println(x + "," + y);
								
								int isValidRoom = 0;
								
								Room adjacentRoom = rooms.get(String.valueOf(x) + "_" + String.valueOf(y + 1));
								if (rooms.get(room).getRoomType().toString().contains("NORTH") && adjacentRoom != null && adjacentRoom.getRoomType().toString().contains("SOUTH")) {
									isValidRoom++;
									location.clone().add(x * 5, 0, (y * 5) + 1).getBlock().setType(Material.REDSTONE_BLOCK);
									location.clone().add(x * 5, 0, (y * 5) + 2).getBlock().setType(Material.REDSTONE_BLOCK);
								} else {
									location.clone().add(x * 5, 0, (y * 5) + 1).getBlock().setType(Material.STONE);
									location.clone().add(x * 5, 0, (y * 5) + 2).getBlock().setType(Material.STONE);
								}
								
								adjacentRoom = rooms.get(String.valueOf(x + 1) + "_" + String.valueOf(y));
								if (rooms.get(room).getRoomType().toString().contains("EAST") && adjacentRoom != null && adjacentRoom.getRoomType().toString().contains("WEST")) {
									isValidRoom++;
									location.clone().add((x * 5) + 1, 0, y * 5).getBlock().setType(Material.REDSTONE_BLOCK);
									location.clone().add((x * 5) + 2, 0, y * 5).getBlock().setType(Material.REDSTONE_BLOCK);
								} else {
									location.clone().add((x * 5) + 1, 0, y * 5).getBlock().setType(Material.STONE);
									location.clone().add((x * 5) + 2, 0, y * 5).getBlock().setType(Material.STONE);
								}

								adjacentRoom = rooms.get(String.valueOf(x) + "_" + String.valueOf(y - 1));
								if (rooms.get(room).getRoomType().toString().contains("SOUTH") && adjacentRoom != null && adjacentRoom.getRoomType().toString().contains("NORTH")) {
									isValidRoom++;
									location.clone().add(x * 5, 0, (y * 5) - 1).getBlock().setType(Material.REDSTONE_BLOCK);
									location.clone().add(x * 5, 0, (y * 5) - 2).getBlock().setType(Material.REDSTONE_BLOCK);
								} else {
									location.clone().add(x * 5, 0, (y * 5) - 1).getBlock().setType(Material.STONE);
									location.clone().add(x * 5, 0, (y * 5) - 2).getBlock().setType(Material.STONE);
								}
								
								adjacentRoom = rooms.get(String.valueOf(x - 1) + "_" + String.valueOf(y));
								if (rooms.get(room).getRoomType().toString().contains("WEST") && adjacentRoom != null && adjacentRoom.getRoomType().toString().contains("EAST")) {
									isValidRoom++;
									location.clone().add((x * 5) - 1, 0, y * 5).getBlock().setType(Material.REDSTONE_BLOCK);
									location.clone().add((x * 5) - 2, 0, y * 5).getBlock().setType(Material.REDSTONE_BLOCK);
								} else {
									location.clone().add((x * 5) - 1, 0, y * 5).getBlock().setType(Material.STONE);
									location.clone().add((x * 5) - 2, 0, y * 5).getBlock().setType(Material.STONE);
								}

								if (isValidRoom > 0) {
									location.clone().add(x * 5, 0, y * 5).getBlock().setType(Material.LAPIS_BLOCK);
									
									location.clone().add((x * 5) - 2, 0, (y * 5) + 2).getBlock().setType(Material.STONE);
									location.clone().add((x * 5) - 1, 0, (y * 5) + 2).getBlock().setType(Material.STONE);
									location.clone().add((x * 5) + 1, 0, (y * 5) + 2).getBlock().setType(Material.STONE);
									location.clone().add((x * 5) + 2, 0, (y * 5) + 2).getBlock().setType(Material.STONE);
									

									location.clone().add((x * 5) - 2, 0, (y * 5) + 1).getBlock().setType(Material.STONE);
									location.clone().add((x * 5) - 1, 0, (y * 5) + 1).getBlock().setType(Material.STONE);
									location.clone().add((x * 5) + 1, 0, (y * 5) + 1).getBlock().setType(Material.STONE);
									location.clone().add((x * 5) + 2, 0, (y * 5) + 1).getBlock().setType(Material.STONE);
									

									location.clone().add((x * 5) - 2, 0, (y * 5) - 1).getBlock().setType(Material.STONE);
									location.clone().add((x * 5) - 1, 0, (y * 5) - 1).getBlock().setType(Material.STONE);
									location.clone().add((x * 5) + 1, 0, (y * 5) - 1).getBlock().setType(Material.STONE);
									location.clone().add((x * 5) + 2, 0, (y * 5) - 1).getBlock().setType(Material.STONE);
									

									location.clone().add((x * 5) - 2, 0, (y * 5) - 2).getBlock().setType(Material.STONE);
									location.clone().add((x * 5) - 1, 0, (y * 5) - 2).getBlock().setType(Material.STONE);
									location.clone().add((x * 5) + 1, 0, (y * 5) - 2).getBlock().setType(Material.STONE);
									location.clone().add((x * 5) + 2, 0, (y * 5) - 2).getBlock().setType(Material.STONE);
									continue;
								}

								location.clone().add(x * 5, 0,  (y * 5) + 1).getBlock().setType(Material.AIR);
								location.clone().add(x * 5, 0,  (y * 5) + 2).getBlock().setType(Material.AIR);
								location.clone().add((x * 5) + 1, 0, y * 5).getBlock().setType(Material.AIR);
								location.clone().add((x * 5) + 2, 0, y * 5).getBlock().setType(Material.AIR);
								location.clone().add(x * 5, 0, (y * 5) - 1).getBlock().setType(Material.AIR);
								location.clone().add(x * 5, 0, (y * 5) - 2).getBlock().setType(Material.AIR);
								location.clone().add((x * 5) - 1, 0, y * 5).getBlock().setType(Material.AIR);
								location.clone().add((x * 5) - 2, 0, y * 5).getBlock().setType(Material.AIR);
							}
						}
					});
					this.cancel();
				}
				
			}
		}.runTaskTimerAsynchronously(plugin, 0L, 1L);
		
	}
	
	public Map<String, Room> getRooms() {
		return rooms;
	}
	
	public List<Room> getQueue() {
		return queue;
	}

}
