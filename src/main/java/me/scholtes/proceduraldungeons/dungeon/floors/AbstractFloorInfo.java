package me.scholtes.proceduraldungeons.dungeon.floors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.DungeonInfo;
import me.scholtes.proceduraldungeons.dungeon.DungeonManager;
import me.scholtes.proceduraldungeons.dungeon.Mob;
import me.scholtes.proceduraldungeons.dungeon.tilesets.TileSet;

public abstract class AbstractFloorInfo {

	private List<String> items;
	private final List<TileSet> tileSets;
	private final Set<Mob> mobs;
	private double chestChance;
	private int minItems;
	private int maxItems;
	private final int floor;
	private final DungeonInfo dungeonInfo;
	
	/**
	 * Constructor for the {@link AbstractFloorInfo}
	 * 
	 * @param dungeonInfo The instance of the {@link DungeonInfo} this {@link AbstractFloorInfo} belongs to
	 * @param dungeonManager The instance of the {@link DungeonManager}
	 * @param floor The floor number
	 */
	public AbstractFloorInfo(DungeonInfo dungeonInfo, DungeonManager dungeonManager, String floor) {
		this.dungeonInfo = dungeonInfo;
		if (floor.equalsIgnoreCase("boss")) {
			this.floor = -1;
		} else { 
			this.floor = Integer.parseInt(floor);
		}
		mobs = new HashSet<>();
		items = new ArrayList<>();
		tileSets = new ArrayList<>();
		
		// Loads in all the information about this AbstractFloorInfo
		Bukkit.getScheduler().runTaskAsynchronously(ProceduralDungeons.getInstance(), () -> {
			
			for (String tileSet : dungeonInfo.getConfig().getStringList("floors." + floor + ".tile_sets")) {
				tileSets.add(dungeonManager.getTileSet(tileSet));
			}
			if (dungeonInfo.getConfig().getConfigurationSection("floors." + floor + ".mobs") != null) {
				for (String mob : dungeonInfo.getConfig().getConfigurationSection("floors." + floor + ".mobs").getKeys(false)) {
					double chance = dungeonInfo.getConfig().getDouble("floors." + floor + ".mobs." + mob + ".chance");
					int minMobs = dungeonInfo.getConfig().getInt("floors." + floor + ".mobs." + mob + ".min_mobs");
					int maxMobs = dungeonInfo.getConfig().getInt("floors." + floor + ".mobs." + mob + ".max_mobs");
					mobs.add(new Mob(mob, chance, minMobs, maxMobs));
				}
			}
			items = dungeonInfo.getConfig().getStringList("floors." + floor + ".chests.items");
			chestChance = dungeonInfo.getConfig().getDouble("floors." + floor + ".chests.chance");
			minItems = dungeonInfo.getConfig().getInt("floors." + floor + ".chests.min_items");
			maxItems = dungeonInfo.getConfig().getInt("floors." + floor + ".chests.max_items");	
		});
	}

	/**
	 * Gets a {@link Set<Mob>} of all the mobs this {@link AbstractFloorInfo} has
	 * 
	 * @return A {@link Set<Mob>} of all the mobs
	 */
	public Set<Mob> getMobs() {
		return mobs;
	}
	
	/**
	 * Gets a {@link List<String>} of all the items this {@link AbstractFloorInfo} has
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
	 * Gets the minimum amount of items a chest can have
	 * 
	 * @return Minimum amount of items
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
	 * {@link AbstractFloorInfo} can have
	 * 
	 * @return A {@link List<TileSet>} of all possible {@link TileSet}
	 */
	public List<TileSet> getTileSets() {
		return tileSets;
	}

	/**
	 * Gets the instance of the {@link DungeonInfo} this {@link AbstractFloorInfo} belongs to
	 * 
	 * @return Instance of the {@link DungeonInfo}
	 */
	public DungeonInfo getDungeonInfo() {
		return dungeonInfo;
	}

	/**
	 * Gets the floor number of this {@link AbstractFloorInfo}
	 * 
	 * @return Floor number
	 */
	public int getFloor() {
		return floor;
	}
	
}
