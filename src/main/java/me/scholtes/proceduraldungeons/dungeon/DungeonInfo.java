package me.scholtes.proceduraldungeons.dungeon;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.scholtes.proceduraldungeons.AsyncScheduler;
import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.floors.FloorInfo;

public class DungeonInfo {
	
	private Map<Integer, FloorInfo> floors;
	private int minFloors;
	private int maxFloors;
	private final String dungeonName;

	/**
	 * Constructor for the {@link DungeonInfo}
	 * 
	 * @param dungeonName The name of the dungeon
	 * @param dungeonManager The instance of the {@link DungeonManager}
	 */
	public DungeonInfo(String dungeonName, DungeonManager dungeonManager) {
		this.dungeonName = dungeonName;
		
		/**
		 * Loads all the information about this DungeonInfo
		 */
		AsyncScheduler.runAsync(() -> {
			floors = new ConcurrentHashMap<Integer, FloorInfo>();
			for (String floor : ProceduralDungeons.getInstance().getConfig().getConfigurationSection(("dungeons." + dungeonName + ".floors")).getKeys(false)) {
				int floorNumber = Integer.valueOf(floor);
				floors.put(floorNumber, new FloorInfo(getInstance(), dungeonManager, floorNumber));
			}
			
			minFloors = ProceduralDungeons.getInstance().getConfig().getInt("dungeons." + dungeonName + ".min_floors");
			maxFloors = ProceduralDungeons.getInstance().getConfig().getInt("dungeons." + dungeonName + ".max_floors");
		});
		
	}

	/**
	 * Gets all the {@link FloorInfo} in a {@link Map<Integer, FloorInfo>}
	 * 
	 * @return a {@link Map<Integer, FloorInfo>} with the {@link FloorInfo}
	 */
	public Map<Integer, FloorInfo> getFloors() {
		return floors;
	}
	
	/**
	 * Gets the minimum amount of {@link Floor}s the {@link Dungeon} can have
	 * 
	 * @return Minimum amount of {@link Floor}s
	 */
	public int getMinFloors() {
		return minFloors;
	}

	/**
	 * Gets the maximum amount of {@link Floor}s the {@link Dungeon} can have
	 * 
	 * @return Maximum amount of {@link Floor}s
	 */
	public int getMaxFloors() {
		return maxFloors;
	}

	/**
	 * Gets the name of the {@link DungeonInfo}
	 * 
	 * @return Name of the {@link DungeonInfo}
	 */
	public String getDungeonName() {
		return dungeonName;
	}
	
	/**
	 * Gets the instance of the {@link DungeonInfo}
	 * 
	 * @return Instance of the {@link DungeonInfo}
	 */
	private DungeonInfo getInstance() {
		return this;
	}

}
