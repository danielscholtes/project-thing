package me.scholtes.proceduraldungeons.dungeon;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.floors.AbstractFloorInfo;
import me.scholtes.proceduraldungeons.dungeon.floors.BossFloor;
import me.scholtes.proceduraldungeons.dungeon.floors.Floor;
import me.scholtes.proceduraldungeons.dungeon.floors.FloorInfo;

public class DungeonInfo {
	
	private Map<Integer, AbstractFloorInfo> floors;
	private int minFloors;
	private int maxFloors;
	private int livesPerPlayer;
	private int teleportCompleteDelay;
	private int teleportNoLivesDelay;
	private Location finishLocation;
	private final String dungeonName;
	private final FileConfiguration config;
	private final Set<Material> canBreakBlocks;
	private final Set<String> allowedCommands;
	private final GameMode enterGamemode;
	private final GameMode leaveGamemode;
	private final String enterResourcePack;
	private final String leaveResourcePack;
	private final List<String> joinCommands;
	private final List<String> leaveCommands;

	/**
	 * Constructor for the {@link DungeonInfo}
	 * 
	 * @param dungeonName The name of the dungeon
	 * @param dungeonManager The instance of the {@link DungeonManager}
	 */
	public DungeonInfo(String dungeonName, DungeonManager dungeonManager) {
		this.dungeonName = dungeonName;
		canBreakBlocks = new HashSet<>();
		allowedCommands = new HashSet<>();

		String path = ProceduralDungeons.getInstance().getDataFolder().getAbsolutePath() + File.separator + "dungeons" + File.separator;
		File file = new File(path, ProceduralDungeons.getInstance().getConfig().getString("dungeons." + dungeonName + ".file") + ".yml");
		this.config = YamlConfiguration.loadConfiguration(file);
		enterGamemode = GameMode.valueOf(config.getString("gamemode.enter_dungeon").toUpperCase());
		leaveGamemode = GameMode.valueOf(config.getString("gamemode.leave_dungeon").toUpperCase());
		enterResourcePack = config.getString("resource_pack.enter_dungeon");
		leaveResourcePack = config.getString("resource_pack.leave_dungeon");
		joinCommands = config.getStringList("commands.join");
		leaveCommands = config.getStringList("commands.leave");
		
		// Loads all the information about this DungeonInfo
		Bukkit.getScheduler().runTaskAsynchronously(ProceduralDungeons.getInstance(), () -> {
			floors = new ConcurrentHashMap<>();
			for (String floor : config.getConfigurationSection("floors").getKeys(false)) {
				if (floor.equalsIgnoreCase("boss")) {
					int floorNumber = -1;
					floors.put(floorNumber, new BossFloor(getInstance(), dungeonManager, floor));
				} else {
					int floorNumber = Integer.parseInt(floor);
					floors.put(floorNumber, new FloorInfo(getInstance(), dungeonManager, floor));
				}
			}
			
			String[] location = config.getString("location_teleport_after").split(";");
			finishLocation = new Location(Bukkit.getWorld(location[3]), Double.parseDouble(location[0]), Double.parseDouble(location[1]), Double.parseDouble(location[2]));
			
			minFloors = config.getInt("min_floors");
			maxFloors = config.getInt("max_floors");
			livesPerPlayer = config.getInt("lives_per_player");
			teleportCompleteDelay = config.getInt("teleport_after_complete_delay");
			teleportNoLivesDelay = config.getInt("teleport_after_no_lives_delay");
			for (String material : config.getStringList("can_break_blocks")) {
				canBreakBlocks.add(Material.valueOf(material.toUpperCase()));
			}
			for (String command : config.getStringList("allowed_commands")) {
				allowedCommands.add(command.toLowerCase());
			}
		});
		
	}

	/**
	 * Gets all the {@link FloorInfo} in a {@link Map<>}
	 * 
	 * @return {@link Map<>} with the {@link FloorInfo}
	 */
	public Map<Integer, AbstractFloorInfo> getFloors() {
		return floors;
	}

	/**
	 * Gets the commands the can be run in the {@link Dungeon}
	 * 
	 * @return Commands the can be run that can be broken in the {@link Dungeon}
	 */
	public Set<String> getAllowedCommands() {
		return allowedCommands;
	}

	/**
	 * Gets the block {@link Material}s that can be broken in the {@link Dungeon}
	 * 
	 * @return Block {@link Material}s that can be broken in the {@link Dungeon}
	 */
	public Set<Material> getCanBreakBlocks() {
		return canBreakBlocks;
	}
	
	/**
	 * Gets the {@link Location} to teleport to after finishing the {@link Dungeon}
	 * 
	 * @return {@link Location} to teleport to
	 */
	public Location getFinishLocation() {
		return finishLocation;
	}
	
	/**
	 * Gets the amount of seconds to wait after clearing the {@link Dungeon} 
	 * to teleport the player
	 * 
	 * @return Amount of seconds to wait
	 */
	public int getTeleportCompleteDelay() {
		return teleportCompleteDelay;
	}
	
	/**
	 * Gets the amount of seconds to wait after losing the {@link Dungeon} 
	 * to teleport the player
	 * 
	 * @return Amount of seconds to wait
	 */
	public int getTeleportNoLivesDelay() {
		return teleportNoLivesDelay;
	}
	
	/**
	 * Gets the lives per player in a {@link Dungeon}
	 * 
	 * @return Lives per player
	 */
	public int getLivesPerPlayer() {
		return livesPerPlayer;
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
	 * Gets the {@link GameMode} the player has to be in when joining the {@link Dungeon}
	 * 
	 * @return {@link GameMode} the player has to be in when joining
	 */
	public GameMode getEnterGameMode() {
		return enterGamemode;
	}
	
	/**
	 * Gets the {@link GameMode} the player has to be in when leaving the {@link Dungeon}
	 * 
	 * @return {@link GameMode} the player has to be in when leaving
	 */
	public GameMode getLeaveGameMode() {
		return leaveGamemode;
	}
	
	/**
	 * Gets the Resource Pack the player has to be in when entering the {@link Dungeon}
	 * 
	 * @return Resource Pack the player has to be in when entering
	 */
	public String getEnterResourcePack() {
		return enterResourcePack;
	}
	
	/**
	 * Gets the Resource Pack the player has to be in when leaving the {@link Dungeon}
	 * 
	 * @return {@link GameMode} the player has to be in when leaving
	 */
	public String getLeaveResourcePack() {
		return leaveResourcePack;
	}
	
	/**
	 * Gets a {@link List<String>} of the commands to run when joininh the {@link Dungeon}
	 * 
	 * @return {@link List<String>} of the commands to run when player joins
	 */
	public List<String> getJoinCommands() {
		return joinCommands;
	}
	
	/**
	 * Gets a {@link List<String>} of the commands to run when leaving the {@link Dungeon}
	 * 
	 * @return {@link List<String>} of the commands to run when player leaves
	 */
	public List<String> getLeaveCommands() {
		return leaveCommands;
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
