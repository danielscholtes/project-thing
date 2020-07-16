package me.scholtes.proceduraldungeons;

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
import com.sk89q.worldedit.world.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class Floor {
	
	private final ProceduralDungeons plugin;
	private final Map<String, Room> rooms = new ConcurrentHashMap<String, Room>();
	private final List<Room> queue = new ArrayList<>();
	private final int maxFloors;
	private final int currentFloor;
	private final String dungeon;
	private String tileSet;
	private int maxRooms;

	private int previousRoomSize = 0;
	private int count = 0;

	public Floor(final ProceduralDungeons plugin, final String dungeon, final int maxFloors, final int currentFloor, final int posX, final int posY) {
		this.plugin = plugin;
		this.currentFloor = currentFloor;
		this.maxFloors = maxFloors;
		this.dungeon = dungeon;
		setMaxRooms();
		setTileSet();
        System.out.println("Generating floor " + currentFloor + " for dungeon1...");
		generateFloor(posX, posY);
	}

	public void generateFloor(final int startPosX, final int startPosY) {
		rooms.put(startPosX + "_" + startPosY, new Room(this, RoomType.values()[ProceduralDungeons.getRandom().nextInt(RoomType.values().length)], startPosX, startPosY));
		new BukkitRunnable() {
			public void run() {
				if (count >= 6 && previousRoomSize == rooms.size()) {
					queue.clear();
				}
				count++;
				previousRoomSize = rooms.size();

				if (queue.isEmpty()) {
					this.cancel();

					for (String room : rooms.keySet()) {
						String[] xy = room.split("_");
						int x = Integer.parseInt(xy[0]);
						int y = Integer.parseInt(xy[1]);
						
						rooms.get(room).setRoomType(getFinalRoomType(rooms.get(room)));
						
						if (rooms.get(room).getRoomType() == RoomType.INVALID) {
							continue;
						}

						World world = FaweAPI.getWorld("testworld");
						File file = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "schematics" + File.separator, rooms.get(room).getRoomType().toString() + ".schem");
						System.out.println(file);

						Clipboard clipboard;

						ClipboardFormat format = ClipboardFormats.findByFile(file);
						try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
							clipboard = reader.read();
							try (EditSession editSession = new EditSessionBuilder(world).fastmode(true).build()) {
							    Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(BlockVector3.at(x * 36, 256 - (13 * currentFloor) , y * 36)).build();
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
						
					}
					
					Room exitRoom = (Room) rooms.values().toArray()[ProceduralDungeons.getRandom().nextInt(rooms.values().toArray().length)];
					while (exitRoom.getRoomType() == RoomType.INVALID) {
						exitRoom = (Room) rooms.values().toArray()[ProceduralDungeons.getRandom().nextInt(rooms.values().toArray().length)];
					}
			        System.out.println("Finished generating floor " + currentFloor + " for dungeon1!");
					
					if (currentFloor < maxFloors) {
						new Floor(plugin, dungeon, maxFloors, currentFloor + 1, exitRoom.getX(), exitRoom.getY());
					}

					rooms.clear();
					queue.clear();
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
	
	public int getMaxRooms() {
		return maxRooms;
	}
	
	public void setMaxRooms() {
		int minimumRooms = plugin.getConfig().getInt("dungeons." + dungeon + ".floors." + currentFloor + ".min_rooms");
		int maximumRooms = plugin.getConfig().getInt("dungeons." + dungeon + ".floors." + currentFloor + ".max_rooms");
		maxRooms = ProceduralDungeons.getRandom().nextInt((maximumRooms - minimumRooms) + 1) + minimumRooms;
		System.out.println("Max room set to " + maxRooms);
	}
	
	public void setTileSet() {
		List<String> tileSets = plugin.getConfig().getStringList("dungeons." + dungeon + ".floors." + currentFloor + ".tile_sets");
		tileSet = tileSets.get(ProceduralDungeons.getRandom().nextInt(tileSets.size()));
	}
	
	private RoomType getFinalRoomType(final Room room) {
		String roomTypeString = room.getRoomType().toString();
		
		roomTypeString = Utils.checkDirection(room, this, roomTypeString, Direction.NORTH, Direction.SOUTH, true);
		roomTypeString = Utils.checkDirection(room, this, roomTypeString, Direction.EAST, Direction.WEST, true);
		roomTypeString = Utils.checkDirection(room, this, roomTypeString, Direction.SOUTH, Direction.NORTH, true);
		roomTypeString = Utils.checkDirection(room, this, roomTypeString, Direction.WEST, Direction.EAST, true);
		
		if (roomTypeString.equals("")) {
			return RoomType.INVALID;
		} else {
			if (roomTypeString.startsWith("_")) {
				roomTypeString = roomTypeString.substring(1);
			}
			return RoomType.valueOf(roomTypeString);
		}
	}
}
