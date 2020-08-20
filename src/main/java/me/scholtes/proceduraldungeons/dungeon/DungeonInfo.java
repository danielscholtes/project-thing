package me.scholtes.proceduraldungeons.dungeon;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.floors.AbstractFloorInfo;
import me.scholtes.proceduraldungeons.dungeon.floors.BossFloor;
import me.scholtes.proceduraldungeons.dungeon.floors.FloorInfo;

public class DungeonInfo {
	
	private Map<Integer, AbstractFloorInfo> floors;
	private int minFloors;
	private int maxFloors;
	private final String dungeonName;
	private final FileConfiguration config;

	/**
	 * Constructor for the {@link DungeonInfo}
	 * 
	 * @param dungeonName The name of the dungeon
	 * @param dungeonManager The instance of the {@link DungeonManager}
	 */
	public DungeonInfo(String dungeonName, DungeonManager dungeonManager) {
		this.dungeonName = dungeonName;
		

		String path = ProceduralDungeons.getInstance().getDataFolder().getAbsolutePath() + File.separator + "dungeons" + File.separator;
		File file = new File(path, dungeonName + ".yml");
		this.config = YamlConfiguration.loadConfiguration(file);
		
		
		/**
		 * Loads all the information about this DungeonInfo
		 */
		Bukkit.getScheduler().runTaskAsynchronously(ProceduralDungeons.getInstance(), () -> {
			floors = new ConcurrentHashMap<Integer, AbstractFloorInfo>();
			for (String floor : config.getConfigurationSection("floors").getKeys(false)) {
				if (floor.equalsIgnoreCase("boss")) {
					int floorNumber = -1;
					floors.put(floorNumber, new BossFloor(getInstance(), dungeonManager, floor));
				} else {
					int floorNumber = Integer.valueOf(floor);
					floors.put(floorNumber, new FloorInfo(getInstance(), dungeonManager, floor));
				}
			}
			
			minFloors = config.getInt("min_floors");
			maxFloors = config.getInt("max_floors");
		});
		
	}

	/**
	 * Gets all the {@link FloorInfo} in a {@link Map<Integer, FloorInfo>}
	 * 
	 * @return {@link Map<Integer, FloorInfo>} with the {@link FloorInfo}
	 */
	public Map<Integer, AbstractFloorInfo> getFloors() {
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
	 * Gets the {@link FileConfiguration} of the {@link DungeonInfo}
	 * 
	 * @return {@link FileConfiguration} of the {@link DungeonInfo}
	 */
	public FileConfiguration getConfig() {
		return config;
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
