package me.scholtes.proceduraldungeons.dungeon.floors;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import io.lumine.xikage.mythicmobs.MythicMobs;
import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.AbstractMob;
import me.scholtes.proceduraldungeons.dungeon.Boss;
import me.scholtes.proceduraldungeons.dungeon.Dungeon;
import me.scholtes.proceduraldungeons.dungeon.Mob;
import me.scholtes.proceduraldungeons.dungeon.rooms.Direction;
import me.scholtes.proceduraldungeons.dungeon.rooms.Room;
import me.scholtes.proceduraldungeons.dungeon.rooms.RoomType;
import me.scholtes.proceduraldungeons.dungeon.tilesets.BossVariation;
import me.scholtes.proceduraldungeons.dungeon.tilesets.TileSet;
import me.scholtes.proceduraldungeons.dungeon.tilesets.TileVariation;
import me.scholtes.proceduraldungeons.dungeon.tilesets.Variation;
import me.scholtes.proceduraldungeons.utils.ChatUtils;
import me.scholtes.proceduraldungeons.utils.DungeonUtils;
import me.scholtes.proceduraldungeons.utils.WorldUtils;

public final class Floor {
	
	private final ProceduralDungeons plugin;
	private final Map<String, Room> rooms = new ConcurrentHashMap<String, Room>();
	private final Set<Room> queue = new HashSet<Room>();
	private final Dungeon dungeon;
	private final FloorInfo floorInfo;
	private int maxRooms;

	private int previousRoomSize = 0;
	private int count = 0;

	/**
	 * Constructor for the {@link Floor}
	 * 
	 * @param plugin The instance of {@link ProceduralDungeons}
	 * @param floorInfo The {@link FloorInfo} of this {@link Floor}
	 * @param currentFloor The current floor
	 * @param posX The X position of the first room
	 * @param posY The Y position of the first room
	 */
	public Floor(ProceduralDungeons plugin, Dungeon dungeon, FloorInfo floorInfo, int posX, int posY, double previousHeight, double previousTileSize) {
		this.plugin = plugin;
		this.floorInfo = floorInfo;
		this.dungeon = dungeon;
		this.maxRooms = ThreadLocalRandom.current().nextInt((floorInfo.getMaxRooms() - floorInfo.getMinRooms()) + 1) + floorInfo.getMinRooms();
        System.out.println("Generating floor " + floorInfo.getFloor() + " for " + floorInfo.getDungeonInfo().getDungeonName());
		generateFloor(posX, posY, previousHeight, previousTileSize);
	}

	/**
	 * Generates the {@link Floor}
	 * 
	 * @param startPosX The X position of the first room
	 * @param startPosY The Y position of the first room
	 */
	public void generateFloor(final int startPosX, final int startPosY, double previousHeight, double previousTileSize) {
		RoomType randomRoomType = RoomType.values()[ThreadLocalRandom.current().nextInt(RoomType.values().length)];
		while (randomRoomType == RoomType.INVALID || randomRoomType == RoomType.BOSS) {
			randomRoomType = RoomType.values()[ThreadLocalRandom.current().nextInt(RoomType.values().length)];
		}
		rooms.put(startPosX + "_" + startPosY, new Room(this, randomRoomType, startPosX, startPosY));
		new BukkitRunnable() {
			public void run() {
				
				/**
				 * After 20 ticks with no changes clear queue
				 */
				if (count >= 20 && previousRoomSize == rooms.size()) {
					queue.clear();
				}
				count++;
				previousRoomSize = rooms.size();

				/**
				 * If queue is empty paste schematics
				 */
				if (queue.isEmpty()) {
					this.cancel();

					/**
					 * A loop that goes through all the rooms, checks if they are valid
					 * and if they are, gets a tile variation from the tileset
					 * 
					 * TO-DO: Add it so it gets a random tile variation
					 */
					
					List<TileSet> tileSets =  floorInfo.getTileSets();
					TileSet tileSet =  tileSets.get(ThreadLocalRandom.current().nextInt(tileSets.size()));
					double newHeight = previousHeight - tileSet.getRoomHeight();
					
					for (String room : rooms.keySet()) {
						String[] xy = room.split("_");
						int x = Integer.parseInt(xy[0]);
						int y = Integer.parseInt(xy[1]);
						
						rooms.get(room).setRoomType(getFinalRoomType(rooms.get(room)));
						
						if (rooms.get(room).getRoomType() == RoomType.INVALID || rooms.get(room).getRoomType() == RoomType.BOSS) {
							continue;
						}
						
						if (dungeon.getSpawnPoint() == null) {
							dungeon.setSpawnPoint(new Location(dungeon.getWorld(), x * tileSet.getRoomSize() - (tileSet.getRoomSize() / 2), previousHeight + (tileSet.getRoomHeight() / 2),  y * tileSet.getRoomSize() - (tileSet.getRoomSize() / 2)));
						}
						
						List<Variation> variations = tileSet.getVariations().get(rooms.get(room).getRoomType());
						
						TileVariation variation = (TileVariation) variations.get(ThreadLocalRandom.current().nextInt(variations.size()));
						
						WorldUtils.pasteSchematic(variation.getSchematic(), dungeon.getWorld().getName(), x * tileSet.getRoomSize(), newHeight , y * tileSet.getRoomSize());
						
						if (x == startPosX && y == startPosY && floorInfo.getFloor() > 1) {
							File randomStairs = tileSet.getStairVariations().get(ThreadLocalRandom.current().nextInt(tileSet.getStairVariations().size()));
							WorldUtils.pasteSchematic(randomStairs, dungeon.getWorld().getName(), x * previousTileSize - (previousTileSize / 2), previousHeight , y * previousTileSize - (previousTileSize / 2));
						}
						
						/**
						 * Generates the chests with loot and all the mobs
						 */
						generateChests(floorInfo, variation, x, y, tileSet, newHeight, tileSet.getRoomSize(), tileSet.getRoomSize());
						generateMobs(floorInfo.getMobs(), variation.getMobLocations(), x, y, tileSet, newHeight, tileSet.getRoomSize(), tileSet.getRoomSize());
						
					}
					
					System.out.println("Finished generating floor " + floorInfo.getFloor() + " for " + floorInfo.getDungeonInfo().getDungeonName() + "!");

					/**
					 * Gets a random room that isn't the starting room to 
					 * be the exit to the starting room of the next floor
					 */
					Room exitRoom = (Room) rooms.values().toArray()[ThreadLocalRandom.current().nextInt(rooms.values().toArray().length)];
					while (exitRoom.getRoomType() == RoomType.INVALID || (exitRoom.getX() == startPosX && exitRoom.getY() == startPosY)) {
						exitRoom = (Room) rooms.values().toArray()[ThreadLocalRandom.current().nextInt(rooms.values().toArray().length)];
					}
					
					/**
					 * Checks if this is the last floor, and if not generates a new one
					 */
					if (floorInfo.getFloor() < floorInfo.getDungeonInfo().getMaxFloors()) {
						new Floor(plugin, dungeon, (FloorInfo) floorInfo.getDungeonInfo().getFloors().get(floorInfo.getFloor() + 1), exitRoom.getX(), exitRoom.getY(), newHeight, tileSet.getRoomSize());
						return;
					}
					
					/**
					 * If it's the last floor, generates the boss room
					 */

					BossFloor bossFloor = (BossFloor) floorInfo.getDungeonInfo().getFloors().get(-1);
					double height = newHeight - tileSet.getBossHeight();
					List<TileSet> bossTileSets =  bossFloor.getTileSets();
					TileSet bossTileSet =  bossTileSets.get(ThreadLocalRandom.current().nextInt(bossTileSets.size()));	
					List<Variation> variations = bossTileSet.getVariations().get(RoomType.BOSS);
					BossVariation variation = (BossVariation) variations.get(ThreadLocalRandom.current().nextInt(variations.size()));
					
					double sizeX = bossTileSet.getBossSizeX();
					double sizeZ = bossTileSet.getBossSizeZ();
					WorldUtils.pasteSchematic(variation.getSchematic(), dungeon.getWorld().getName(), exitRoom.getX() * tileSet.getRoomSize(), height , exitRoom.getY() * tileSet.getRoomSize());
					File randomStairs = tileSet.getStairVariations().get(ThreadLocalRandom.current().nextInt(tileSet.getStairVariations().size()));
					WorldUtils.pasteSchematic(randomStairs, dungeon.getWorld().getName(), exitRoom.getX() * tileSet.getRoomSize() - (tileSet.getRoomSize() / 2), newHeight , exitRoom.getY() * tileSet.getRoomSize() - (tileSet.getRoomSize() / 2));
					

					generateChests(bossFloor, variation, exitRoom.getX(), exitRoom.getY(), tileSet, height, sizeX, sizeZ);
					generateMobs(bossFloor.getMobs(), variation.getMobLocations(), exitRoom.getX(), exitRoom.getY(), tileSet, height, sizeX, sizeZ);;
					List<Boss> bosses = bossFloor.getBosses();
					Boss randomBoss = bosses.get(ThreadLocalRandom.current().nextInt(bosses.size()));
					
					String[] split = variation.getBossLocation().split(";");
					double locX = (exitRoom.getX() * tileSet.getRoomSize()) + Double.valueOf(split[0]);
					double locY = previousHeight + Double.valueOf(split[1]);
					double locZ = (exitRoom.getY() * tileSet.getRoomSize()) + Double.valueOf(split[2]);
					Location location = new Location(dungeon.getWorld(), locX, locY, locZ);
					
					dungeon.setBoss(MythicMobs.inst().getMobManager().spawnMob(randomBoss.getName(), location));
					
					rooms.clear();
					queue.clear();

					/**
					 * If this is the last floor, teleports the player to the dungeon
					 * 
					 * TO-DO: Add a party system and stuff
					 */
					Bukkit.getScheduler().runTask(plugin, () -> {
						Player bukkitPlayer = Bukkit.getPlayer(dungeon.getPlayer());
						if (bukkitPlayer == null) {
							return;
						}

						ChatUtils.message(bukkitPlayer, "&aDungeon generated! Teleporting...");
						bukkitPlayer.teleport(dungeon.getSpawnPoint());
						
					});
				}
			}
		}.runTaskTimerAsynchronously(plugin, 0L, 1L);
	}
	
	private void generateChests(AbstractFloorInfo floorInfo, Variation variation, double x, double y, TileSet tileSet, double height, double sizeX, double sizeZ) {
		for (String loc : variation.getChestLocations()) {
			if (Math.random() >= floorInfo.getChestChance()) {
				continue;
			}
			
			Bukkit.getScheduler().runTask(plugin, () -> {
				String[] split = loc.split(";");
				double locX = (x * sizeX) + Double.valueOf(split[0]);
				double locY = height + Double.valueOf(split[1]);
				double locZ = (y * sizeZ) + Double.valueOf(split[2]);
				Location location = new Location(dungeon.getWorld(), locX, locY, locZ);
				
				location.getBlock().setType(Material.CHEST);
				
				Chest chest = (Chest) location.getBlock().getState();
				
				int itemAmount = ThreadLocalRandom.current().nextInt((floorInfo.getMaxItems() - floorInfo.getMinItems()) + 1) + floorInfo.getMinItems();
				List<String> items = floorInfo.getItems();
				
				for (int i = 0; i < itemAmount; i++) {
					String randomItem = items.get(ThreadLocalRandom.current().nextInt(items.size()));
					chest.getInventory().setItem(ThreadLocalRandom.current().nextInt(27), plugin.getDungeonManager().getItem(randomItem));
				}
			});
		}
	}
	
	private void generateMobs(Set<AbstractMob> mobs, List<String> locations, double x, double y, TileSet tileSet, double height, double sizeX, double sizeZ) {
		for (String loc : locations) {
			for (AbstractMob mob : floorInfo.getMobs()) {
				if (mob instanceof Mob) {
					if (Math.random() >= ((Mob) mob).getChance()) {
						continue;
					}
				}
				
				Bukkit.getScheduler().runTask(plugin, () -> {
					String[] split = loc.split(";");
					double locX = (x * sizeX) + Double.valueOf(split[0]);
					double locY = height + Double.valueOf(split[1]);
					double locZ = (y * sizeZ) + Double.valueOf(split[2]);
					Location location = new Location(dungeon.getWorld(), locX, locY, locZ);
					int mobAmount = 1;
					if (mob instanceof Mob) {
						mobAmount = ThreadLocalRandom.current().nextInt((((Mob) mob).getMaxMobs() -  ((Mob) mob).getMinMobs()) + 1) + ((Mob) mob).getMinMobs();
					}
					
					for (int i = 0; i < mobAmount; i++) {
						MythicMobs.inst().getMobManager().spawnMob(mob.getName(), location);
					}
				});
			}
		}
	}
	
	/**
	 * Checks and fixes any invalid doors a {@link Room} may have from
	 * generation
	 * 
	 * @param room A {@link Room} to check and fix any invalid doors it may have
	 * @return A {@link RoomType} for the doors the room has available
	 */
	private RoomType getFinalRoomType(final Room room) {
		String roomTypeString = room.getRoomType().toString();
		
		roomTypeString = DungeonUtils.checkDirection(room, this, roomTypeString, Direction.NORTH, Direction.SOUTH, true);
		roomTypeString = DungeonUtils.checkDirection(room, this, roomTypeString, Direction.EAST, Direction.WEST, true);
		roomTypeString = DungeonUtils.checkDirection(room, this, roomTypeString, Direction.SOUTH, Direction.NORTH, true);
		roomTypeString = DungeonUtils.checkDirection(room, this, roomTypeString, Direction.WEST, Direction.EAST, true);
		
		if (roomTypeString.equals("")) {
			return RoomType.INVALID;
		}
		
		if (roomTypeString.startsWith("_")) {
			roomTypeString = roomTypeString.substring(1);
		}
		return RoomType.valueOf(roomTypeString);
	}
	
	/**
	 * Gets all the {@link Room}s this instance of the {@link Floor} has
	 * 
	 * @return A {@link Map<String, Room>} containing the rooms
	 */
	public Map<String, Room> getRooms() {
		return rooms;
	}

	/**
	 * Gets the queue for any {@link Room}s generating a new {@link Room} in this
	 * instance of the {@link Floor}
	 * 
	 * @return A {@link List<Room>} containing the queue
	 */
	public Set<Room> getQueue() {
		return queue;
	}
	
	/**
	 * Gets the maximum amount of {@link Room}s this floor can have
	 * 
	 * @return The maximum amount of rooms
	 */
	public int getMaxRooms() {
		return maxRooms;
	}
}
