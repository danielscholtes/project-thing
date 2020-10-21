package me.scholtes.proceduraldungeons.dungeon.floors;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.boydti.fawe.util.TaskManager;

import io.lumine.xikage.mythicmobs.MythicMobs;
import me.scholtes.proceduraldungeons.ProceduralDungeons;
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
import me.scholtes.proceduraldungeons.party.Party;
import me.scholtes.proceduraldungeons.utils.StringUtils;
import me.scholtes.proceduraldungeons.utils.DungeonUtils;
import me.scholtes.proceduraldungeons.utils.Message;
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
	 * @param dungeon The {@link Dungeon} associated to this {@link Floor}
	 * @param floorInfo The {@link FloorInfo} of this {@link Floor}
	 * @param startWorldX The X world position to add onto the pasting of the {@link Floor}
	 * @param startWorldY The Y world position to add onto the pasting of the {@link Floor}
	 * @param prevStartWorldX The previous X world position to add onto the pasting of the previous {@link Floor}
	 * @param prevStartWorldY The previous Y world position to add onto the pasting of the previous {@link Floor}
	 * @param exitRoomX The X position of the exit room for the previous {@link Floor}
	 * @param exitRoomY The Y position of the exit room for the previous {@link Floor}
	 * @param previousHeight The pasting height of the last {@link Floor}
	 * @param previousTileSize The previous tile size (room size) of the previous {@link Floor}
	 */
	public Floor(ProceduralDungeons plugin, Dungeon dungeon, FloorInfo floorInfo, double startWorldX, double startWorldY, double prevStartWorldX, double prevStartWorldY, int exitRoomX, int exitRoomY, double previousHeight, double previousTileSize) {
		this.plugin = plugin;
		this.floorInfo = floorInfo;
		this.dungeon = dungeon;
		this.maxRooms = ThreadLocalRandom.current().nextInt((floorInfo.getMaxRooms() - floorInfo.getMinRooms()) + 1) + floorInfo.getMinRooms();
		generateFloor(exitRoomX, exitRoomY, startWorldX, startWorldY, prevStartWorldX, prevStartWorldY, previousHeight, previousTileSize);
	}

	/**
	 * Generates the {@link Floor}
	 * 
	 * @param exitRoomX The X position of the exit room for the previous {@link Floor}
	 * @param exitRoomY The Y position of the exit room for the previous {@link Floor}
	 * @param startWorldX The X world position to add onto the pasting of the {@link Floor}
	 * @param startWorldY The Y world position to add onto the pasting of the {@link Floor}
	 * @param prevStartWorldX The previous X world position to add onto the pasting of the previous {@link Floor}
	 * @param prevStartWorldY The previous Y world position to add onto the pasting of the previous {@link Floor}
	 * @param previousHeight The pasting height of the last {@link Floor}
	 * @param previousTileSize The previous tile size (room size) of the previous {@link Floor}
	 */
	public void generateFloor(int exitRoomX, int exitRoomY, double startWorldX, double startWorldY, double prevStartWorldX, double prevStartWorldY, double previousHeight, double previousTileSize) {
		RoomType randomRoomType = RoomType.values()[ThreadLocalRandom.current().nextInt(RoomType.values().length)];
		while (randomRoomType == RoomType.INVALID || randomRoomType == RoomType.BOSS) {
			randomRoomType = RoomType.values()[ThreadLocalRandom.current().nextInt(RoomType.values().length)];
		}
		rooms.put("0_0", new Room(this, randomRoomType, 0, 0));
		new BukkitRunnable() {
			public void run() {
				/**
				 * After 20 ticks with no changes clear queue
				 */
				boolean generateAnyway = false;
				if (count >= 12 && previousRoomSize == rooms.size()) {
					queue.clear();
					generateAnyway = true;
				}
				count++;
				previousRoomSize = rooms.size();

				/**
				 * If queue is empty paste schematics
				 */
				if (queue.isEmpty() || generateAnyway) {
					this.cancel();
					
					if (rooms.size() < floorInfo.getMinRooms()) {
						rooms.clear();
						queue.clear();
						generateFloor(exitRoomX, exitRoomY, startWorldX, startWorldY, prevStartWorldX, prevStartWorldY, previousHeight, previousTileSize);
						return;
					}
					
					TaskManager.IMP.async(() -> {
						

						int startRoomX = 0;
						int startRoomY = 0;
						
						/**
						 * A loop that goes through all the rooms, checks if they are valid
						 * and if they are, gets a tile variation from the tileset
						 */
						
						List<TileSet> tileSets =  floorInfo.getTileSets();
						TileSet tileSet =  tileSets.get(ThreadLocalRandom.current().nextInt(tileSets.size()));
						double oldTileSize = (previousTileSize == 0) ? tileSet.getRoomSize() : previousTileSize;
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
								startRoomX = x;
								startRoomY = y;
								dungeon.setSpawnPoint(new Location(dungeon.getWorld(), startWorldX + x * tileSet.getRoomSize() - (tileSet.getRoomSize() / 2), newHeight + (tileSet.getRoomHeight() / 2),  startWorldY + y * tileSet.getRoomSize() - (tileSet.getRoomSize() / 2)));
							}
							
							List<Variation> variations = tileSet.getVariations().get(rooms.get(room).getRoomType());
							
							TileVariation variation = (TileVariation) variations.get(ThreadLocalRandom.current().nextInt(variations.size()));
							
							//WorldUtils.pasteSchematic(variation.getSchematic(), dungeon.getWorld().getName(), startWorldX + x * tileSet.getRoomSize(), newHeight , startWorldY + y * tileSet.getRoomSize());
							WorldUtils.pasteSchematic(variation.getSchematic(), dungeon.getWorld().getName(), startWorldX + x * tileSet.getRoomSize() + ((tileSet.getRoomSize() - oldTileSize) / 2), newHeight , startWorldY + y * tileSet.getRoomSize() + ((tileSet.getRoomSize() - oldTileSize) / 2));

							if (startRoomX == x && startRoomY == y && floorInfo.getFloor() > 1) {
								File randomStairs = tileSet.getStairVariations().get(ThreadLocalRandom.current().nextInt(tileSet.getStairVariations().size()));
								WorldUtils.pasteSchematic(randomStairs, dungeon.getWorld().getName(), prevStartWorldX + exitRoomX * oldTileSize - (oldTileSize / 2), previousHeight, prevStartWorldY + exitRoomY * oldTileSize - (oldTileSize / 2));	
							}
							
							/**
							 * Generates the chests with loot and all the mobs
							 */
							generateChests(floorInfo, variation, startWorldX, startWorldY, x, y, tileSet, newHeight);
							generateMobs(floorInfo.getMobs(), variation.getMobLocations(), startWorldX, startWorldY, x, y, tileSet, newHeight);
							
						}

						/**
						 * Gets a random room that isn't the starting room to 
						 * be the exit to the starting room of the next floor
						 */
						Room exitRoom = (Room) rooms.values().toArray()[ThreadLocalRandom.current().nextInt(rooms.values().toArray().length)];
						while (exitRoom.getRoomType() == RoomType.INVALID || (exitRoom.getX() == startRoomX && exitRoom.getY() == startRoomY)) {
							exitRoom = (Room) rooms.values().toArray()[ThreadLocalRandom.current().nextInt(rooms.values().toArray().length)];
						}
						
						/**
						 * Checks if this is the last floor, and if not generates a new one
						 */

						double startWorldXNew = startWorldX + exitRoom.getX() * tileSet.getRoomSize() + ((tileSet.getRoomSize() - oldTileSize) / 2);
						double startWorldYNew = startWorldY + exitRoom.getY() * tileSet.getRoomSize() + ((tileSet.getRoomSize() - oldTileSize) / 2);
						if (floorInfo.getFloor() < dungeon.getMaxFloors()) {
							new Floor(plugin, dungeon, (FloorInfo) floorInfo.getDungeonInfo().getFloors().get(floorInfo.getFloor() + 1), startWorldXNew, startWorldYNew, startWorldX, startWorldY, exitRoom.getX(), exitRoom.getY(), newHeight, tileSet.getRoomSize());
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
						double positionXSchematic = startWorldXNew + ((bossTileSet.getRoomSize() - tileSet.getRoomSize()) / 2);
						double positionYSchematic = startWorldYNew + ((bossTileSet.getRoomSize() - tileSet.getRoomSize()) / 2);
						WorldUtils.pasteSchematic(variation.getSchematic(), dungeon.getWorld().getName(), positionXSchematic, height , positionYSchematic);

						File randomStairs = variation.getBossStairVariations().get(ThreadLocalRandom.current().nextInt(variation.getBossStairVariations().size()));
						WorldUtils.pasteSchematic(randomStairs, dungeon.getWorld().getName(), startWorldXNew - (tileSet.getRoomSize() / 2), newHeight , startWorldYNew - (tileSet.getRoomSize() / 2));
						
						generateChests(bossFloor, variation, startWorldXNew, startWorldYNew, 0, 0, tileSet, height);
						generateMobs(bossFloor.getMobs(), variation.getMobLocations(), startWorldXNew, startWorldYNew, 0, 0, tileSet, height);
						List<Boss> bosses = bossFloor.getBosses();
						Boss randomBoss = bosses.get(ThreadLocalRandom.current().nextInt(bosses.size()));
						
						String[] split = variation.getBossLocation().split(";");
						double locX = startWorldXNew + Double.valueOf(split[0]);
						double locY = height + Double.valueOf(split[1]);
						double locZ = startWorldYNew + Double.valueOf(split[2]);
						Location location = new Location(dungeon.getWorld(), locX, locY, locZ);
						
						rooms.clear();
						queue.clear();
						
						/**
						 * Teleports the players to the dungeon
						 */
						Bukkit.getScheduler().runTask(plugin, () -> {
							while (dungeon.getSpawnPoint().getBlock().getType() != Material.AIR || 
									dungeon.getSpawnPoint().clone().add(0, 1, 0).getBlock().getType()!= Material.AIR || 
											dungeon.getSpawnPoint().clone().add(0, 2, 0).getBlock().getType() != Material.AIR) {
								
								if (dungeon.getSpawnPoint().clone().add(0, -1, 0).getBlock().getType() != Material.AIR && 
										dungeon.getSpawnPoint().clone().add(0, 0, 0).getBlock().getType() != Material.AIR && 
												dungeon.getSpawnPoint().clone().add(0, 1, 0).getBlock().getType() != Material.AIR) {
									dungeon.setSpawnPoint(dungeon.getSpawnPoint().clone().add(0, -1, 0));
								} else if (dungeon.getSpawnPoint().clone().add(0, 0, -1).getBlock().getType() != Material.AIR && 
										dungeon.getSpawnPoint().clone().add(0, 1, -1).getBlock().getType() != Material.AIR && 
												dungeon.getSpawnPoint().clone().add(0, 2, -1).getBlock().getType() != Material.AIR) {
									dungeon.setSpawnPoint(dungeon.getSpawnPoint().clone().add(0, 0, -1));
								} else if (dungeon.getSpawnPoint().clone().add(1, 0, 0).getBlock().getType() != Material.AIR && 
										dungeon.getSpawnPoint().clone().add(1, 1, 0).getBlock().getType() != Material.AIR && 
												dungeon.getSpawnPoint().clone().add(1, 2, 0).getBlock().getType() != Material.AIR) {
									dungeon.setSpawnPoint(dungeon.getSpawnPoint().clone().add(1, 0, 0));
								} else if (dungeon.getSpawnPoint().clone().add(0, 0, 1).getBlock().getType() != Material.AIR && 
										dungeon.getSpawnPoint().clone().add(0, 1, 1).getBlock().getType() != Material.AIR && 
												dungeon.getSpawnPoint().clone().add(0, 2, 1).getBlock().getType() != Material.AIR) {
									dungeon.setSpawnPoint(dungeon.getSpawnPoint().clone().add(0, 0, 1));
								} else if (dungeon.getSpawnPoint().clone().add(-1, 0,  0).getBlock().getType() != Material.AIR && 
										dungeon.getSpawnPoint().clone().add(-1, 1, 0).getBlock().getType() != Material.AIR && 
												dungeon.getSpawnPoint().clone().add(-1, 2, 0).getBlock().getType() != Material.AIR) {
									dungeon.setSpawnPoint(dungeon.getSpawnPoint().clone().add(-1, 0, 0));
								} else {
									dungeon.setSpawnPoint(dungeon.getSpawnPoint().clone().add(0, -1, 0));
								}
								
								dungeon.getSpawnPoint().getBlock().setType(Material.AIR);
								dungeon.getSpawnPoint().clone().add(0, 1, 0).getBlock().setType(Material.AIR);
								dungeon.getSpawnPoint().clone().add(0, 2, 0).getBlock().setType(Material.AIR);
							}
							
							dungeon.setBossID(MythicMobs.inst().getMobManager().spawnMob(randomBoss.getName(), location).getUniqueId());
							
							Party party = plugin.getPartyData().getPartyFromPlayer(dungeon.getPlayer());
							if (party != null) {
								for (UUID uuid : party.getMembers()) {
									Player bukkitPlayer = Bukkit.getPlayer(uuid);
									if (bukkitPlayer == null) {
										continue;
									}
									bukkitPlayer.teleport(dungeon.getSpawnPoint());
									bukkitPlayer.setGameMode(dungeon.getDungeonInfo().getEnterGameMode());
									if (!dungeon.getDungeonInfo().getEnterResourcePack().equalsIgnoreCase("none")) {
										bukkitPlayer.setResourcePack(dungeon.getDungeonInfo().getEnterResourcePack(), StringUtils.generateSHA1(dungeon.getDungeonInfo().getEnterResourcePack()).getBytes());	
									}
								}
								Bukkit.getPlayer(party.getOwner()).teleport(dungeon.getSpawnPoint());
								Bukkit.getPlayer(party.getOwner()).setGameMode(dungeon.getDungeonInfo().getEnterGameMode());
								if (!dungeon.getDungeonInfo().getEnterResourcePack().equalsIgnoreCase("none")) {
									Bukkit.getPlayer(party.getOwner()).setResourcePack(dungeon.getDungeonInfo().getEnterResourcePack(), StringUtils.generateSHA1(dungeon.getDungeonInfo().getEnterResourcePack()).getBytes());	
								}
								party.messageMembers(StringUtils.getMessage(Message.DUNGEON_JOIN_GENERATED));
								party.messageMembers(StringUtils.replaceAll(StringUtils.getMessage(Message.DUNGEON_LIVES_LEFT), "{lives}", String.valueOf(dungeon.getTotalLives())));
							} else {

								Player bukkitPlayer = Bukkit.getPlayer(dungeon.getPlayer());
								if (bukkitPlayer == null) {
									return;
								}
								StringUtils.message(bukkitPlayer, StringUtils.getMessage(Message.DUNGEON_JOIN_GENERATED));
								StringUtils.message(bukkitPlayer, StringUtils.replaceAll(StringUtils.getMessage(Message.DUNGEON_LIVES_LEFT), "{lives}", String.valueOf(dungeon.getTotalLives())));
								bukkitPlayer.teleport(dungeon.getSpawnPoint());
								bukkitPlayer.setGameMode(dungeon.getDungeonInfo().getEnterGameMode());
								if (!dungeon.getDungeonInfo().getEnterResourcePack().equalsIgnoreCase("none")) {
									bukkitPlayer.setResourcePack(dungeon.getDungeonInfo().getEnterResourcePack(), StringUtils.generateSHA1(dungeon.getDungeonInfo().getEnterResourcePack()).getBytes());	
								}
							}
						});
					});
				}
			}
		}.runTaskTimerAsynchronously(plugin, 0L, 1L);
	}

	/**
	 * Generates all the chests for the {@link Room}
	 * 
	 * @param floorInfo The {@link AbstractFloorInfo} of this {@link Room}
	 * @param variation The {@link Variation} of this {@link Room}
	 * @param startWorldX The X world position to add onto the spawning of the chests
	 * @param startWorldY The Y world position to add onto the pasting of the chests
	 * @param x The X coordinate of the {@link Room}
	 * @param y The Y coordinate of the {@link Room}
	 * @param tileSet The {@link TileSet} for this {@link Room}
	 * @param height The height of the {@link Room}
	 */
	private void generateChests(AbstractFloorInfo floorInfo, Variation variation, double startWorldX, double startWorldY, double x, double y, TileSet tileSet, double height) {
		for (String loc : variation.getChestLocations()) {
			if (Math.random() >= floorInfo.getChestChance()) {
				continue;
			}
			
			Bukkit.getScheduler().runTask(plugin, () -> {
				String[] split = loc.split(";");
				double locX = (startWorldX + x * tileSet.getRoomSize()) + Double.valueOf(split[0]);
				double locY = height + Double.valueOf(split[1]);
				double locZ = (startWorldY + y * tileSet.getRoomSize()) + Double.valueOf(split[2]);
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
	
	/**
	 * Generates all the mobs for the {@link Room}
	 * 
	 * @param mobs A {@link Set<Mob>} of mobs to spawn
	 * @param locations A {@link List<String>} list of all possible locations
	 * @param startWorldX The X world position to add onto the spawning of the mobs
	 * @param startWorldY The Y world position to add onto the pasting of the mobs
	 * @param x The X coordinate of the {@link Room}
	 * @param y The Y coordinate of the {@link Room}
	 * @param tileSet The {@link TileSet} for this {@link Room}
	 * @param height The height of the {@link Room}
	 */
	private void generateMobs(Set<Mob> mobs, List<String> locations, double startWorldX, double startWorldY, double x, double y, TileSet tileSet, double height) {
		for (String loc : locations) {
			for (Mob mob : mobs) {
				if (Math.random() >=  mob.getChance()) {
					continue;
				}
				
				Bukkit.getScheduler().runTask(plugin, () -> {
					String[] split = loc.split(";");
					double locX = (startWorldX + x * tileSet.getRoomSize()) + Double.valueOf(split[0]);
					double locY = height + Double.valueOf(split[1]);
					double locZ = (startWorldY + y * tileSet.getRoomSize()) + Double.valueOf(split[2]);
					Location location = new Location(dungeon.getWorld(), locX, locY, locZ);
					int mobAmount = 1;
					mobAmount = ThreadLocalRandom.current().nextInt((mob.getMaxMobs() -  mob.getMinMobs()) + 1) + mob.getMinMobs();
					
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
