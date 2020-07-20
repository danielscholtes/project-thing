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
	private final String dungeonName;
	private final UUID dungeonID;
	private final World world;
	private int maxFloors = 0;
	private UUID player;

	/**
	 * Constructor for the {@link Dungeon}
	 * 
	 * @param plugin The instance of {@link ProceduralDungeons}
	 * @param dungeonName The name of the dungeon
	 * @param player The UUID of the main dungeon player
	 */
	public Dungeon(final ProceduralDungeons plugin, final String dungeonName, final UUID player) {
		this.plugin = plugin;
		this.dungeonName = dungeonName;
		this.player = player;
		this.dungeonID = UUID.randomUUID();

		/*
		 * Creates a void world
		 */
		WorldCreator creator = new WorldCreator("Dungeon-" + dungeonID.toString());
		creator.generator(new VoidGenerator());
		world = creator.createWorld();
		world.setAutoSave(false);

	}

	/**
	 * Generates the dungeon
	 */
	public void generateDungeon() {
		/**
		 * Generates the dungeon asynchronously
		 */
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {

				/*
				 * Checks if the world was properly created
				 */
				if (getWorld() == null) {
					System.out.println("The world was not properly created!");
					return;
				}
				
				int minFloors = plugin.getConfig().getInt("dungeons." + getDungeonName() + ".min_floors");
				int maxFloors = plugin.getConfig().getInt("dungeons." + getDungeonName() + ".max_floors");
				setMaxFloors(ProceduralDungeons.getRandom().nextInt((maxFloors - minFloors) + 1) + minFloors);
				System.out.println("Started floor generation for " + getDungeonName());

				new Floor(plugin, getInstance(), 1, 0, 0);

			}
		});

	}
	
	/**
	 * Sets the maximum amount of {@link Floor}s the {@link Dungeon} can have
	 * 
	 * @param maxFloors The maximum amount of floors
	 */
	public void setMaxFloors(int maxFloors) {
		this.maxFloors = maxFloors;
	}
	
	/**
	 * Gets the maximum amount of {@link Floor}s the {@link Dungeon} can have
	 * 
	 * @return The maximum amount of floors
	 */
	public int getMaxFloors() {
		return maxFloors;
	}

	/**
	 * Gets the {@link UUID} of the main player (or the party leader) of the {@link Dungeon}
	 * 
	 * @return The UUID of the main dungeon player
	 */
	public UUID getPlayer() {
		return player;
	}
	
	/**
	 * Sets the {@link UUID} of the main player (or the party leader) of the {@link Dungeon}
	 * 
	 * @param player The UUID of the main dungeon player
	 */
	public void setPlayer(UUID player) {
		this.player = player;
	}
	
	/**
	 * Gets the {@link World} of the {@link Dungeon}
	 * 
	 * @param player The world of the dungeon
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * Gets the instance of this {@link Dungeon}
	 * 
	 * @return Instance of the dungeon
	 */
	public Dungeon getInstance() {
		return this;
	}
	
	/**
	 * Gets the dungeon name of this {@link Dungeon}
	 * 
	 * @return The dungeon name
	 */
	public String getDungeonName() {
		return dungeonName;
	}

}
