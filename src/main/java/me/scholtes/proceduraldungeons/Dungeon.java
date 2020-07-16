package me.scholtes.proceduraldungeons;

import org.bukkit.Bukkit;

public class Dungeon {

	private final ProceduralDungeons plugin;
	private final String dungeon;
	
	public Dungeon(final ProceduralDungeons plugin, final String dungeon) {
		this.plugin = plugin;
		this.dungeon = dungeon;
	}

	public void generateDungeon() {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				int minFloors = plugin.getConfig().getInt("dungeons." + dungeon + ".min_floors");
				int maxFloors = plugin.getConfig().getInt("dungeons." + dungeon + ".max_floors");
				int floorCount = ProceduralDungeons.getRandom().nextInt((maxFloors - minFloors) + 1) + minFloors;
		        System.out.println("Started floor generation for dungeon1");
				new Floor(plugin, dungeon, floorCount, 1, 0, 0);
				
			}
		});
		
	}
	
}
