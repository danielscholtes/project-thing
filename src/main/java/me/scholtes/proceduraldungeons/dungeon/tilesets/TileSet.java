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
	
	private Map<RoomType, List<TileVariation>> tileVariations;
	private double size;
	private double height;
	private final String tileSetName;
	
	public TileSet(String tileSetName) {
		this.tileSetName = tileSetName;
		Bukkit.getScheduler().runTaskAsynchronously(ProceduralDungeons.getInstance(), new Runnable() {
			@Override
			public void run() {
				tileVariations = new ConcurrentHashMap<RoomType, List<TileVariation>>();
				for (RoomType roomType : RoomType.values()) {
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
			}
		});
		
	}

	public Map<RoomType, List<TileVariation>> getTileVariations() {
		return tileVariations;
	}
	
	public TileSet getInstance() {
		return this;
	}

	public double getSize() {
		return size;
	}

	public double getHeight() {
		return height;
	}

	public String getTileSetName() {
		return tileSetName;
	}

}
