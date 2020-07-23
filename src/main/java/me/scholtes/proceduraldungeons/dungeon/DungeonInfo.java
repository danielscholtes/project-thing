package me.scholtes.proceduraldungeons.dungeon;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.floors.FloorInfo;

public class DungeonInfo {
	
	private Map<Integer, FloorInfo> floors;
	private int minFloors;
	private int maxFloors;
	private final String dungeonName;
	
	public DungeonInfo(String dungeonName, DungeonManager dungeonManager) {
		this.dungeonName = dungeonName;
		Bukkit.getScheduler().runTaskAsynchronously(ProceduralDungeons.getInstance(), new Runnable() {
			@Override
			public void run() {
				floors = new ConcurrentHashMap<Integer, FloorInfo>();
				for (String floor : ProceduralDungeons.getInstance().getConfig().getConfigurationSection(("dungeons." + dungeonName + ".floors")).getKeys(false)) {
					int floorNumber = Integer.valueOf(floor);
					floors.put(floorNumber, new FloorInfo(getInstance(), dungeonManager, floorNumber));
				}
				
				minFloors = ProceduralDungeons.getInstance().getConfig().getInt("dungeons." + dungeonName + ".min_floors");
				maxFloors = ProceduralDungeons.getInstance().getConfig().getInt("dungeons." + dungeonName + ".max_floors");
			}
		});
	}

	public Map<Integer, FloorInfo> getFloors() {
		return floors;
	}
	
	public int getMinFloors() {
		return minFloors;
	}

	public int getMaxFloors() {
		return maxFloors;
	}

	public String getDungeonName() {
		return dungeonName;
	}
	
	private DungeonInfo getInstance() {
		return this;
	}

}
