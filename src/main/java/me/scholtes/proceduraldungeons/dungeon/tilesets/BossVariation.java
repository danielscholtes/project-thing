package me.scholtes.proceduraldungeons.dungeon.tilesets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.rooms.RoomType;

public class BossVariation extends Variation {
	
	private String bossLocation;
	private final List<File> bossStairVariations;
	
	/**
	 * Constructor for the {@link BossVariation}
	 * 
	 * @param tileSet The instance of the {@link TileSet} this {@link BossVariation} belongs to
	 * @param variation The name of this {@link BossVariation}
	 * @param roomType The {@link RoomType} of this {@link BossVariation}
	 */
	public BossVariation(TileSet tileSet, String variation, RoomType roomType) {
		super(tileSet, variation, roomType);
		bossStairVariations = new ArrayList<File>();
		/**
		 * Loads all the information about the TileVariation
		 */
		Bukkit.getScheduler().runTaskAsynchronously(ProceduralDungeons.getInstance(), () -> {
			String path = ProceduralDungeons.getInstance().getDataFolder().getAbsolutePath() + File.separator + tileSet.getTileSetName() + File.separator + roomType.toString() + File.separator;
			File file = new File(path, "variations.yml");
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			
			bossLocation = config.getString("variations." + variation + ".boss");
			String pathStairs = ProceduralDungeons.getInstance().getDataFolder().getAbsolutePath() + File.separator + tileSet.getTileSetName() + File.separator + roomType.toString() + File.separator + File.separator + "STAIRS" + File.separator;
			File fileStairs = new File(pathStairs, "variations.yml");
			FileConfiguration configStairs = YamlConfiguration.loadConfiguration(fileStairs);

			for (String stairVariation : configStairs.getStringList("variations")) {
				bossStairVariations.add(new File(pathStairs, stairVariation + ".schem"));
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
