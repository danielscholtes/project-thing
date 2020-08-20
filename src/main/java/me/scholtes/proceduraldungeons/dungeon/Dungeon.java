package me.scholtes.proceduraldungeons.dungeon;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.floors.Floor;
import me.scholtes.proceduraldungeons.dungeon.floors.FloorInfo;
import me.scholtes.proceduraldungeons.generator.VoidGenerator;

public class Dungeon {

	private final ProceduralDungeons plugin;
	private final DungeonInfo dungeonInfo;
	private final UUID dungeonID;
	private final World world;
	private int maxFloors = 0;
	private UUID player;
	private Location spawnPoint = null;
	private ActiveMob boss = null;

	/**
	 * Constructor for the {@link Dungeon}
	 * 
	 * @param plugin The instance of {@link ProceduralDungeons}
	 * @param dungeonName The name of the dungeon
	 * @param player The UUID of the main dungeon player
	 */
	public Dungeon(final ProceduralDungeons plugin, final DungeonInfo dungeonInfo, final UUID player) {
		this.plugin = plugin;
		this.dungeonInfo = dungeonInfo;
		this.player = player;
		this.dungeonID = UUID.randomUUID();

		/**
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
		Bukkit.getScheduler().runTaskAsynchronously(ProceduralDungeons.getInstance(), () -> {
			/**
			 * Checks if the world was properly created
			 */
			if (getWorld() == null) {
				System.out.println("The world was not properly created!");
				return;
			}
			
			setMaxFloors(ThreadLocalRandom.current().nextInt((dungeonInfo.getMaxFloors() - dungeonInfo.getMinFloors()) + 1) + dungeonInfo.getMinFloors());
			System.out.println("Started floor generation for " + dungeonInfo.getDungeonName());

			new Floor(plugin, getInstance(), (FloorInfo) dungeonInfo.getFloors().get(1), 0, 0, 256, 0);
		});

	}


	/**
	 * Gets the spawn point {@link Location} of the {@link Dungeon}
	 * 
	 * @return Spawn point {@link Location}
	 */
	public Location getSpawnPoint() {
		return spawnPoint;
	}

	/**
	 * Gets the {@link ActiveMob} boss of the {@link Dungeon}
	 * 
	 * @return {@link ActiveMob} boss
	 */
	public ActiveMob getBoss() {
		return boss;
	}

	/**
	 * Sets the {@link ActiveMob} boss of the {@link Dungeon}
	 * 
	 * @param boss {@link ActiveMob} boss
	 */
	public void setBoss(ActiveMob boss) {
		this.boss = boss;
	}

	/**
	 * Sets the spawn point {@link Location} of the {@link Dungeon}
	 * 
	 * @param spawnPoint Spawn point {@link Location}
	 */
	public void setSpawnPoint(Location spawnPoint) {
		this.spawnPoint = spawnPoint;
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
	 * Gets the {@link DungeonInfo} name of this {@link Dungeon}
	 * 
	 * @return The {@link DungeonInfo}
	 */
	public DungeonInfo getDungeonInfo() {
		return dungeonInfo;
	}

}
