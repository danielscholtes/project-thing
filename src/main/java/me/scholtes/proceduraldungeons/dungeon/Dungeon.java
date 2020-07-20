package me.scholtes.proceduraldungeons.dungeon;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.floors.Floor;
import me.scholtes.proceduraldungeons.generator.VoidGenerator;

public class Dungeon {

	private final ProceduralDungeons plugin;
	private final String dungeon;
	private final UUID dungeonID;
	private final World world;
	private UUID player;

	public Dungeon(final ProceduralDungeons plugin, final String dungeon, final UUID player) {
		this.plugin = plugin;
		this.dungeon = dungeon;
		this.player = player;
		this.dungeonID = UUID.randomUUID();

		WorldCreator creator = new WorldCreator("Dungeon-" + dungeonID.toString());
		creator.generator(new VoidGenerator());
		world = creator.createWorld();
		world.setAutoSave(false);

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

				new Floor(plugin, dungeon, floorCount, 1, 0, 0, getWorld(), player);

			}
		});

	}

	public UUID getPlayer() {
		return player;
	}
	
	public void setPlayer(UUID player) {
		this.player = player;
	}
	
	public World getWorld() {
		return world;
	}

	public Dungeon getInstance() {
		return this;
	}

}
