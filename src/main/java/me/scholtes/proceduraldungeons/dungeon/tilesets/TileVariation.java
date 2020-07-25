package me.scholtes.proceduraldungeons.dungeon.tilesets;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.rooms.RoomType;

public class TileVariation {
	
	private List<String> chestLocations;
	private List<String> mobLocations;
	private File schematic;
	
	/**
	 * Constructor for the {@link TileVariation}
	 * 
	 * @param tileSet The instance of the {@link TileSet} this {@link TileVariation} belongs to
	 * @param variation The name of this {@link TileVariation}
	 * @param roomType The {@link RoomType} of this {@link TileVariation}
	 */
	public TileVariation(TileSet tileSet, String variation, RoomType roomType) {
		/**
		 * Loads all the information about the TileVariation
		 */
		Bukkit.getScheduler().runTaskAsynchronously(ProceduralDungeons.getInstance(), () -> {
			String path = ProceduralDungeons.getInstance().getDataFolder().getAbsolutePath() + File.separator + tileSet.getTileSetName() + File.separator + roomType.toString() + File.separator;
			File file = new File(path, "variations.yml");
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			
			chestLocations = config.getStringList("variations." + variation + ".chests");
			mobLocations = config.getStringList("variations." + variation + ".mobs");
			schematic = new File(path, variation + ".schem");
		});
	}

	/**
	 * Gets a {@link File} representing the schematic
	 * 
	 * @return A {@link File} representing the schematic
	 */
	public File getSchematic() {
		return schematic;
	}

	/**
	 * Gets a {@link List<String>} of all possible chest locations
	 * 
	 * @return A {@link List<String>} of all possible chest locations
	 */
	public List<String> getChestLocations() {
		return chestLocations;
	}

	/**
	 * Gets a {@link List<String>} of all possible mob locations
	 * 
	 * @return A {@link List<String>} of all possible mob locations
	 */
	public List<String> getMobLocations() {
		return mobLocations;
	}
	
}
