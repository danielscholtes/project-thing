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

final class Dungeon {
	
	private final ProceduralDungeons plugin;
	private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();
	private final List<Room> queue = new ArrayList<>();

	private int previousRoomSize = 0;
	private int count = 0;

	public Dungeon(final ProceduralDungeons plugin) {
		this.plugin = plugin;
	}

	public void generateDungeon() {
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
							    Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(BlockVector3.at(x * 36, 100 , y * 36)).build();
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
