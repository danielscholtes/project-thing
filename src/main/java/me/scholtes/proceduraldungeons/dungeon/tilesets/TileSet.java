package me.scholtes.proceduraldungeons.dungeon.tilesets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.rooms.RoomType;

public class TileSet {
	
	private final Map<RoomType, List<TileVariation>> tileVariations;
	private double size;
	private double height;
	private final String tileSetName;
	private final List<File> stairVariations;
	
	/**
	 * Constructor for the {@link TileSet}
	 * 
	 * @param tileSetName The name of the {@link TileSet}
	 */
	public TileSet(String tileSetName) {
		this.tileSetName = tileSetName;
		tileVariations = new ConcurrentHashMap<RoomType, List<TileVariation>>();
		stairVariations = new ArrayList<File>();
		
		/**
		 * Loads all the information about this TileSet
		 */
		Bukkit.getScheduler().runTaskAsynchronously(ProceduralDungeons.getInstance(), () -> {
			for (RoomType roomType : RoomType.values()) {
				if (roomType == RoomType.INVALID) {
					continue;
				}
				
				String path = ProceduralDungeons.getInstance().getDataFolder().getAbsolutePath() + File.separator + tileSetName + File.separator + roomType.toString() + File.separator;
				File file = new File(path, "variations.yml");
				FileConfiguration config = YamlConfiguration.loadConfiguration(file);
				
				List<TileVariation> variations = new ArrayList<TileVariation>();
				for (String variation : config.getConfigurationSection("variations").getKeys(false)) {
					variations.add(new TileVariation(getInstance(), variation, roomType));
				}
				tileVariations.put(roomType, variations);
			}
			size = ProceduralDungeons.getInstance().getConfig().getDouble("tile_sets." + tileSetName + ".size");
			height = ProceduralDungeons.getInstance().getConfig().getDouble("tile_sets." + tileSetName + ".height");
			

			String pathStairs = ProceduralDungeons.getInstance().getDataFolder().getAbsolutePath() + File.separator + tileSetName + File.separator + "STAIRS" + File.separator;
			File fileStairs = new File(pathStairs, "variations.yml");
			FileConfiguration configStairs = YamlConfiguration.loadConfiguration(fileStairs);

			for (String variation : configStairs.getStringList("variations")) {
				stairVariations.add(new File(pathStairs, variation + ".schem"));
			}
		});
		
	}

	/**
	 * Gets a {@link Map<RoomType, List<TileVariation>>} of all the {@link TileVariation}
	 * 
	 * @return A {@link Map<RoomType, List<TileVariation>>} of all the {@link TileVariation}
	 */
	public Map<RoomType, List<TileVariation>> getTileVariations() {
		return tileVariations;
	}
	
	/**
	 * Gets the instance of this {@link TileSet}
	 * 
	 * @return Instance of this {@link TileSet}
	 */
	public TileSet getInstance() {
		return this;
	}

	/**
	 * Gets the schematic size of this {@link TileSet}
	 * 
	 * @return Schematic size of this {@link TileSet}
	 */
	public double getSize() {
		return size;
	}

	/**
	 * Gets the schematic height of this {@link TileSet}
	 * 
	 * @return Schematic height of this {@link TileSet}
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Gets the name of this {@link TileSet}
	 * 
	 * @return Name of this {@link TileSet}
	 */
	public String getTileSetName() {
		return tileSetName;
	}

	/**
	 * Gets a {@link List<File>} of all the stair variations
	 * 
	 * @return A {@link List<File>} of all the stair variations
	 */
	public List<File> getStairVariations() {
		return stairVariations;
	}

}
