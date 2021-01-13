package me.scholtes.proceduraldungeons.dungeon;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.floors.Floor;
import me.scholtes.proceduraldungeons.dungeon.floors.FloorInfo;
import me.scholtes.proceduraldungeons.generator.VoidGenerator;
import me.scholtes.proceduraldungeons.party.Party;

public class Dungeon {

	private final ProceduralDungeons plugin;
	private final DungeonInfo dungeonInfo;
	private final UUID dungeonID;
	private final World world;
	private int maxFloors = 0;
	private int totalLives;
	private UUID player;
	private Location spawnPoint = null;
	private UUID bossID = null;

	/**
	 * Constructor for the {@link Dungeon}
	 * 
	 * @param plugin The instance of {@link ProceduralDungeons}
	 * @param dungeonInfo The {@link DungeonInfo} of this dungeon
	 * @param player The UUID of the main dungeon player
	 */
	public Dungeon(ProceduralDungeons plugin, DungeonInfo dungeonInfo, UUID player) {
		this.plugin = plugin;
		this.dungeonInfo = dungeonInfo;
		this.player = player;
		this.dungeonID = UUID.randomUUID();
		this.totalLives = dungeonInfo.getLivesPerPlayer();
		
		Party party = plugin.getPartyData().getPartyFromPlayer(player);
		if (party != null) {
			this.totalLives = dungeonInfo.getLivesPerPlayer() + (party.getMembers().size() * dungeonInfo.getLivesPerPlayer());
		}

		/**
		 * Creates a void world
		 */
		WorldCreator creator = new WorldCreator("Dungeon-" + dungeonID.toString());
		creator.generator(new VoidGenerator());
		world = creator.createWorld();
		world.setAutoSave(false);
		world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		world.setGameRule(GameRule.MOB_GRIEFING, false);
		world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
		world.setGameRule(GameRule.KEEP_INVENTORY, true);
		world.setGameRule(GameRule.DO_MOB_SPAWNING, false);

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
				return;
			}
			
			setMaxFloors(ThreadLocalRandom.current().nextInt((dungeonInfo.getMaxFloors() - dungeonInfo.getMinFloors()) + 1) + dungeonInfo.getMinFloors());

			new Floor(plugin, getInstance(), (FloorInfo) dungeonInfo.getFloors().get(1), 0, 0, 0, 0, 0, 0, 256, 0);
		});

	}

	/**
	 * Gets the total amount of lives the players have left
	 * 
	 * @return Total amount of lives the players have left
	 */
	public int getTotalLives() {
		return totalLives;
	}
	
	/**
	 * Sets the total amount of lives the players have left
	 * 
	 * @param totalLives Total amount of lives the players have left
	 */
	public void setTotalLives(int totalLives) {
		this.totalLives = totalLives;
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
	 * Gets the boss {@link UUID} of the {@link Dungeon}
	 * 
	 * @return Boss {@link UUID} of the {@link Dungeon}
	 */
	public UUID getBossID() {
		return bossID;
	}

	/**
	 * Sets the boss {@link UUID} of the {@link Dungeon}
	 * 
	 * @param bossID {@link UUID} of the {@link Dungeon}
	 */
	public void setBossID(UUID bossID) {
		this.bossID = bossID;
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
	 * @return Returns the {@link World} of the {@link Dungeon}
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

	/**
	 * Gets the {@link UUID} of this {@link Dungeon}
	 * 
	 * @return {@link UUID} of this {@link Dungeon}
	 */
	public UUID getDungeonID() {
		return dungeonID;
	}

}
