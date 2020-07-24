package me.scholtes.proceduraldungeons.dungeon.floors;

import java.util.ArrayList;
import java.util.List;

import me.scholtes.proceduraldungeons.AsyncScheduler;
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
	
	/**
	 * Constructor for the {@link FloorInfo}
	 * 
	 * @param dungeonInfo The instance of the {@link DungeonInfo} this {@link FloorInfo} belongs to
	 * @param dungeonManager The instance of the {@link DungeonManager}
	 * @param floor The floor number
	 */
	public FloorInfo(DungeonInfo dungeonInfo, DungeonManager dungeonManager, int floor) {
		this.dungeonInfo = dungeonInfo;
		this.floor = floor;
		items = new ArrayList<String>();
		tileSets = new ArrayList<TileSet>();
		
		/**
		 * Loads in all the information about this FloorInfo
		 */
		AsyncScheduler.runAsync(() -> {
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
	
	/**
	 * Gets a {@link List<String>} of all the items this {@link FloorInfo} has
	 * 
	 * @return A {@link List<String>} of all the items
	 */
	public List<String> getItems() {
		return items;
	}
	
	/**
	 * Gets the chance of a chest to spawn
	 * 
	 * @return Chance of a chest to spawn
	 */
	public double getChestChance() {
		return chestChance;
	}

	/**
	 * Gets the mimimum amount of items a chest can have
	 * 
	 * @return Mimimum amount of items
	 */
	public int getMinItems() {
		return minItems;
	}

	/**
	 * Gets the maximum amount of items a chest can have
	 * 
	 * @return Maximum amount of items
	 */
	public int getMaxItems() {
		return maxItems;
	}

	/**
	 * Gets a {@link List<TileSet>} of all possible {@link TileSet} this
	 * {@link FloorInfo} can have
	 * 
	 * @return A {@link List<TileSet>} of all possible {@link TileSet}
	 */
	public List<TileSet> getTileSets() {
		return tileSets;
	}

	/**
	 * Gets the minimum amount of {@link Room}s this {@link FloorInfo} can have
	 * 
	 * @return Minimum amount of {@link Room}s
	 */
	public int getMinRooms() {
		return minRooms;
	}

	/**
	 * Gets the maximum amount of {@link Room}s this {@link FloorInfo} can have
	 * 
	 * @return Maximum amount of {@link Room}s
	 */
	public int getMaxRooms() {
		return maxRooms;
	}

	/**
	 * Gets the instance of the {@link DungeonInfo} this {@link FloorInfo} belongs to
	 * 
	 * @return Instance of the {@link DungeonInfo}
	 */
	public DungeonInfo getDungeonInfo() {
		return dungeonInfo;
	}

	/**
	 * Gets the floor number of this {@link FloorInfo}
	 * 
	 * @return Floor number
	 */
	public int getFloor() {
		return floor;
	}
	
}
