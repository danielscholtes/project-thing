package me.scholtes.proceduraldungeons.dungeon.floors;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.DungeonInfo;
import me.scholtes.proceduraldungeons.dungeon.DungeonManager;
import me.scholtes.proceduraldungeons.dungeon.tilesets.TileSet;

public class FloorInfo {

	private List<String> items;
	private List<TileSet> tileSets;
	private double chestChance;
	private int minRooms;
	private int maxRooms;
	private int minItems;
	private int maxItems;
	private final int floor;
	private final DungeonInfo dungeonInfo;
	
	public FloorInfo(DungeonInfo dungeonInfo, DungeonManager dungeonManager, int floor) {
		this.dungeonInfo = dungeonInfo;
		this.floor = floor;
		items = new ArrayList<String>();
		tileSets = new ArrayList<TileSet>();
		Bukkit.getScheduler().runTaskAsynchronously(ProceduralDungeons.getInstance(), () -> {	
			for (String tileSet : ProceduralDungeons.getInstance().getConfig().getStringList("dungeons." + dungeonInfo.getDungeonName() + ".floors." + floor + ".tile_sets")) {
				tileSets.add(dungeonManager.getTileSet(tileSet));
			}
			minRooms = ProceduralDungeons.getInstance().getConfig().getInt("dungeons." + dungeonInfo.getDungeonName() + ".floors." + floor + ".min_rooms");
			minRooms = ProceduralDungeons.getInstance().getConfig().getInt("dungeons." + dungeonInfo.getDungeonName() + ".floors." + floor + ".max_rooms");
			items = ProceduralDungeons.getInstance().getConfig().getStringList("dungeons." + dungeonInfo.getDungeonName() + ".floors." + floor + ".items");
			chestChance = ProceduralDungeons.getInstance().getConfig().getDouble("dungeons." + dungeonInfo.getDungeonName() + ".floors." + floor + ".chest_chance");
			minItems = ProceduralDungeons.getInstance().getConfig().getInt("dungeons." + dungeonInfo.getDungeonName() + ".floors." + floor + ".min_items");
			maxItems = ProceduralDungeons.getInstance().getConfig().getInt("dungeons." + dungeonInfo.getDungeonName() + ".floors." + floor + ".max_items");	
		});
	}
	
	public List<String> getItems() {
		return items;
	}
	
	public double getChestChance() {
		return chestChance;
	}

	public int getMinItems() {
		return minItems;
	}
	public int getMaxItems() {
		return maxItems;
	}

	public List<TileSet> getTileSets() {
		return tileSets;
	}
	
	public int getMinRooms() {
		return minRooms;
	}
	public int getMaxRooms() {
		return maxRooms;
	}

	public DungeonInfo getDungeonInfo() {
		return dungeonInfo;
	}

	public int getFloor() {
		return floor;
	}
	
}
