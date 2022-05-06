package me.scholtes.proceduraldungeons.dungeon.floors;

import me.scholtes.proceduraldungeons.dungeon.rooms.Room;
import org.bukkit.Bukkit;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.DungeonInfo;
import me.scholtes.proceduraldungeons.dungeon.DungeonManager;

public class FloorInfo extends AbstractFloorInfo {

	private int minRooms;
	private int maxRooms;
	
	/**
	 * Constructor for the {@link FloorInfo}
	 * 
	 * @param dungeonInfo The instance of the {@link DungeonInfo} this {@link FloorInfo} belongs to
	 * @param dungeonManager The instance of the {@link DungeonManager}
	 * @param floor The floor number
	 */
	public FloorInfo(DungeonInfo dungeonInfo, DungeonManager dungeonManager, String floor) {
		super(dungeonInfo, dungeonManager, floor);
		
		// Loads in all the information about this AbstractFloorInfo
		Bukkit.getScheduler().runTaskAsynchronously(ProceduralDungeons.getInstance(), () -> {
			minRooms = dungeonInfo.getConfig().getInt("floors." + floor + ".min_rooms");
			maxRooms = dungeonInfo.getConfig().getInt("floors." + floor + ".max_rooms");
		});
	}

	/**
	 * Gets the minimum amount of {@link Room}s this {@link FloorInfo} can have
	 * 
	 * @return Minimum amount of {@link Room}s
	 */
	public int getMinRooms() {
		return minRooms;
	}

	/**
	 * Gets the maximum amount of {@link Room}s this {@link FloorInfo} can have
	 * 
	 * @return Maximum amount of {@link Room}s
	 */
	public int getMaxRooms() {
		return maxRooms;
	}
	
}
