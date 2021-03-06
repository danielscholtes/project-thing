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
	
	private final Map<RoomType, List<TileVariation>> variations;
	private double roomSize;
	private double roomHeight;
	private double bossHeight;
	private final String tileSetName;
	private final List<File> stairVariations;
	
	/**
	 * Constructor for the {@link TileSet}
	 * 
	 * @param tileSetName The name of the {@link TileSet}
	 */
	public TileSet(String tileSetName) {
		this.tileSetName = tileSetName;
		variations = new ConcurrentHashMap<>();
		stairVariations = new ArrayList<>();
		
		// Loads all the information about this TileSet
		Bukkit.getScheduler().runTaskAsynchronously(ProceduralDungeons.getInstance(), () -> {
			for (RoomType roomType : RoomType.values()) {
				if (roomType == RoomType.INVALID) {
					continue;
				}
				
				String path = ProceduralDungeons.getInstance().getDataFolder().getAbsolutePath() + File.separator + tileSetName + File.separator;
				File file = new File(path, roomType.toString() + "_variations.yml");
				FileConfiguration config = YamlConfiguration.loadConfiguration(file);
				
				List<TileVariation> tileVariations = new ArrayList<>();
				if (roomType == RoomType.BOSS) {
					for (String variation : config.getConfigurationSection("variations").getKeys(false)) {
						tileVariations.add(new BossTileVariation(getInstance(), variation, roomType));
					}
				} else {
					for (String variation : config.getConfigurationSection("variations").getKeys(false)) {
						tileVariations.add(new TileVariation(getInstance(), variation, roomType));
					}
				}
				variations.put(roomType, tileVariations);
			}
			roomSize = ProceduralDungeons.getInstance().getConfig().getDouble("tile_sets." + tileSetName + ".room_size");
			roomHeight = ProceduralDungeons.getInstance().getConfig().getDouble("tile_sets." + tileSetName + ".room_height");
			bossHeight = ProceduralDungeons.getInstance().getConfig().getDouble("tile_sets." + tileSetName + ".boss_height");

			String pathStairs = ProceduralDungeons.getInstance().getDataFolder().getAbsolutePath() + File.separator + tileSetName + File.separator;
			String pathSchematics = ProceduralDungeons.getInstance().getDataFolder().getAbsolutePath() + File.separator + tileSetName + File.separator + "schematics" + File.separator;
			File fileStairs = new File(pathStairs, "STAIRS_variations.yml");
			FileConfiguration configStairs = YamlConfiguration.loadConfiguration(fileStairs);

			for (String variation : configStairs.getStringList("variations")) {
				stairVariations.add(new File(pathSchematics, variation + ".schem"));
			}
		});
		
	}

	/**
	 * Gets a {@link Map<RoomType, List<TileVariation>>} of all the {@link TileVariation}
	 * 
	 * @return A {@link Map<RoomType, List<TileVariation>>} of all the {@link TileVariation}
	 */
	public Map<RoomType, List<TileVariation>> getVariations() {
		return variations;
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
	 * Gets the schematic room size of this {@link TileSet}
	 * 
	 * @return Schematic room size of this {@link TileSet}
	 */
	public double getRoomSize() {
		return roomSize;
	}

	/**
	 * Gets the schematic room height of this {@link TileSet}
	 * 
	 * @return Schematic room height of this {@link TileSet}
	 */
	public double getRoomHeight() {
		return roomHeight;
	}

	/**
	 * Gets the schematic boss room height of this {@link TileSet}
	 * 
	 * @return Schematic boss room height of this {@link TileSet}
	 */
	public double getBossHeight() {
		return bossHeight;
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
