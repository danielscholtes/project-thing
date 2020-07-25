package me.scholtes.proceduraldungeons.dungeon.floors;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.Dungeon;
import me.scholtes.proceduraldungeons.dungeon.rooms.Direction;
import me.scholtes.proceduraldungeons.dungeon.rooms.Room;
import me.scholtes.proceduraldungeons.dungeon.rooms.RoomType;
import me.scholtes.proceduraldungeons.dungeon.tilesets.TileSet;
import me.scholtes.proceduraldungeons.dungeon.tilesets.TileVariation;
import me.scholtes.proceduraldungeons.utils.ChatUtils;
import me.scholtes.proceduraldungeons.utils.DungeonUtils;

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
	public Floor(ProceduralDungeons plugin, Dungeon dungeon, FloorInfo floorInfo, int posX, int posY, double previousHeight) {
		this.plugin = plugin;
		this.floorInfo = floorInfo;
		this.dungeon = dungeon;
		this.maxRooms = ProceduralDungeons.getRandom().nextInt((floorInfo.getMaxRooms() - floorInfo.getMinRooms()) + 1) + floorInfo.getMinRooms();
        System.out.println("Generating floor " + floorInfo.getFloor() + " for " + floorInfo.getDungeonInfo().getDungeonName());
		generateFloor(posX, posY, previousHeight);
	}

	/**
	 * Generates the {@link Floor}
	 * 
	 * @param startPosX The X position of the first room
	 * @param startPosY The Y position of the first room
	 */
	public void generateFloor(final int startPosX, final int startPosY, double previousHeight) {
		rooms.put(startPosX + "_" + startPosY, new Room(this, RoomType.values()[ProceduralDungeons.getRandom().nextInt(RoomType.values().length)], startPosX, startPosY));
		new BukkitRunnable() {
			public void run() {
				
				/**
				 * After 6 ticks with no changes clear queue
				 */
				if (count >= 6 && previousRoomSize == rooms.size()) {
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
					TileSet tileSet =  tileSets.get(ProceduralDungeons.getRandom().nextInt(tileSets.size()));
					double newHeight = previousHeight - tileSet.getHeight();
					
					for (String room : rooms.keySet()) {
						String[] xy = room.split("_");
						int x = Integer.parseInt(xy[0]);
						int y = Integer.parseInt(xy[1]);
						
						rooms.get(room).setRoomType(getFinalRoomType(rooms.get(room)));
						
						if (rooms.get(room).getRoomType() == RoomType.INVALID) {
							continue;
						}
						
						if (dungeon.getSpawnPoint() == null) {
							dungeon.setSpawnPoint(new Location(dungeon.getWorld(), (x + 1) * tileSet.getSize() / 2, previousHeight - (tileSet.getHeight() / 2), (y + 1) * tileSet.getSize() / 2));
						}
						
						List<TileVariation> variations = tileSet.getTileVariations().get(rooms.get(room).getRoomType());
						
						TileVariation variation = variations.get(ProceduralDungeons.getRandom().nextInt(variations.size()));

						/**
						 * Pastes the schematic of the tile
						 */
						Clipboard clipboard;

						ClipboardFormat format = ClipboardFormats.findByFile(variation.getSchematic());
						try (ClipboardReader reader = format.getReader(new FileInputStream(variation.getSchematic()))) {
							clipboard = reader.read();
							try (EditSession editSession = new EditSessionBuilder(FaweAPI.getWorld(dungeon.getWorld().getName())).fastmode(true).build()) {
							    Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(BlockVector3.at(x * tileSet.getSize(), newHeight , y * tileSet.getSize())).build();
							    try {
									Operations.complete(operation);
								} catch (WorldEditException e) {
									e.printStackTrace();
								}
							}
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						/**
						 * Generates the chests with loot
						 */
						
						for (String loc : variation.getChestLocations()) {
							if (Math.random() >= floorInfo.getChestChance()) {
								continue;
							}
							
							Bukkit.getScheduler().runTask(plugin, () -> {
								String[] split = loc.split(";");
								double locX = (x * tileSet.getSize()) + Double.valueOf(split[0]);
								double locY = previousHeight + Double.valueOf(split[1]);
								double locZ = (y * tileSet.getSize()) + Double.valueOf(split[2]);
								System.out.println(locX + ";" + locY + ";" + locZ);
								Location location = new Location(dungeon.getWorld(), locX, locY, locZ);
								
								location.getBlock().setType(Material.CHEST);
								
								System.out.println(location.getBlock().getType());
								
								Chest chest = (Chest) location.getBlock().getState();
								
								int itemAmount = ProceduralDungeons.getRandom().nextInt((floorInfo.getMaxItems() - floorInfo.getMinItems()) + 1) + floorInfo.getMinItems();
								List<String> items = floorInfo.getItems();
								
								for (int i = 0; i < itemAmount; i++) {
									String randomItem = items.get(ProceduralDungeons.getRandom().nextInt(items.size()));
									chest.getInventory().setItem(ProceduralDungeons.getRandom().nextInt(27), plugin.getDungeonManager().getItem(randomItem));
								}
								
								chest.update();
							});
						}
						
					}
					
					System.out.println("Finished generating floor " + floorInfo.getFloor() + " for " + floorInfo.getDungeonInfo().getDungeonName() + " !");

					/**
					 * Checks if this is the last floor, and if not gets a random room that
					 * isn't the starting room to be the exit to the starting room
					 * of the next floor
					 */
					if (floorInfo.getFloor() < floorInfo.getDungeonInfo().getMaxFloors()) {
						Room exitRoom = (Room) rooms.values().toArray()[ProceduralDungeons.getRandom().nextInt(rooms.values().toArray().length)];
						while (exitRoom.getRoomType() == RoomType.INVALID && exitRoom.getX() == startPosX && exitRoom.getY() == startPosY) {
							exitRoom = (Room) rooms.values().toArray()[ProceduralDungeons.getRandom().nextInt(rooms.values().toArray().length)];
						}
						new Floor(plugin, dungeon, floorInfo.getDungeonInfo().getFloors().get(floorInfo.getFloor() + 1), exitRoom.getX(), exitRoom.getY(), newHeight);
						return;
					}
					
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
