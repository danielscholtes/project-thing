package me.scholtes.proceduraldungeons;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.grinderwolf.swm.api.exceptions.WorldAlreadyExistsException;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;

public class Dungeon {

	private final ProceduralDungeons plugin;
	private final String dungeon;
	private final UUID dungeonID;
	private SlimeWorld world;

	public Dungeon(final ProceduralDungeons plugin, final String dungeon) {
		this.plugin = plugin;
		this.dungeon = dungeon;
		this.dungeonID = UUID.randomUUID();
		try {
			SlimePropertyMap properties = new SlimePropertyMap();
			properties.setInt(SlimeProperties.SPAWN_X, 0);
			properties.setInt(SlimeProperties.SPAWN_X, 255);
			properties.setInt(SlimeProperties.SPAWN_X, 0);
			properties.setBoolean(SlimeProperties.PVP, false);
			world = plugin.getSlimePlugin().createEmptyWorld(plugin.getSlimePlugin().getLoader("file"), "Dungeon-" + dungeonID.toString(), true, properties);
			plugin.getSlimePlugin().generateWorld(world);
		} catch (WorldAlreadyExistsException | IOException e) {
			e.printStackTrace();
		}
	}

	public void generateDungeon() {
		if (world == null) {
			System.out.println("The world was not properly created!");
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				int minFloors = plugin.getConfig().getInt("dungeons." + dungeon + ".min_floors");
				int maxFloors = plugin.getConfig().getInt("dungeons." + dungeon + ".max_floors");
				int floorCount = ProceduralDungeons.getRandom().nextInt((maxFloors - minFloors) + 1) + minFloors;
				System.out.println("Started floor generation for dungeon1");
				SlimePropertyMap properties = new SlimePropertyMap();
				properties.setInt(SlimeProperties.SPAWN_X, 0);
				properties.setInt(SlimeProperties.SPAWN_X, 255);
				properties.setInt(SlimeProperties.SPAWN_X, 0);
				properties.setBoolean(SlimeProperties.PVP, false);

				new Floor(plugin, dungeon, floorCount, 1, 0, 0, Bukkit.getWorld(getWorld().getName()));

			}
		});

	}
	
	public SlimeWorld getWorld() {
		return world;
	}

	public Dungeon getInstance() {
		return this;
	}

}
