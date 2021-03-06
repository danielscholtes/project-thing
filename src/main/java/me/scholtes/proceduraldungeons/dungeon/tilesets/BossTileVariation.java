package me.scholtes.proceduraldungeons.dungeon.tilesets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.rooms.RoomType;

public class BossTileVariation extends TileVariation {
	
	private String bossLocation;
	private final List<File> bossStairVariations;
	
	/**
	 * Constructor for the {@link BossTileVariation}
	 * 
	 * @param tileSet The instance of the {@link TileSet} this {@link BossTileVariation} belongs to
	 * @param variation The name of this {@link BossTileVariation}
	 * @param roomType The {@link RoomType} of this {@link BossTileVariation}
	 */
	public BossTileVariation(TileSet tileSet, String variation, RoomType roomType) {
		super(tileSet, variation, roomType);
		bossStairVariations = new ArrayList<>();
		// Loads all the information about the TileVariation
		Bukkit.getScheduler().runTaskAsynchronously(ProceduralDungeons.getInstance(), () -> {
			String path = ProceduralDungeons.getInstance().getDataFolder().getAbsolutePath() + File.separator + tileSet.getTileSetName() + File.separator;
			File file = new File(path, roomType.toString() + "_variations.yml");
			String pathSchematics = ProceduralDungeons.getInstance().getDataFolder().getAbsolutePath() + File.separator + tileSet.getTileSetName() + File.separator + "schematics" + File.separator;
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			
			bossLocation = config.getString("variations." + variation + ".boss");
			File fileStairs = new File(path, "BOSS_STAIRS_variations.yml");
			FileConfiguration configStairs = YamlConfiguration.loadConfiguration(fileStairs);

			for (String stairVariation : configStairs.getStringList("variations")) {
				bossStairVariations.add(new File(pathSchematics, stairVariation + ".schem"));
			}
		});
	}

	/**
	 * Gets a {@link String} of the boss location
	 * 
	 * @return A {@link String} of the boss location
	 */
	public String getBossLocation() {
		return bossLocation;
	}
	
	/**
	 * Gets a {@link List<File>} of all the boss stair variations
	 * 
	 * @return A {@link List<File>} of all boss the stair variations
	 */
	public List<File> getBossStairVariations() {
		return bossStairVariations;
	}
	
}
