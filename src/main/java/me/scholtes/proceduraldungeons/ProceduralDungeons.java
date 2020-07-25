package me.scholtes.proceduraldungeons;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.scholtes.proceduraldungeons.commands.DungeonCommand;
import me.scholtes.proceduraldungeons.dungeon.Dungeon;
import me.scholtes.proceduraldungeons.dungeon.DungeonManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import java.util.Random;
import java.util.stream.Stream;

public final class ProceduralDungeons extends JavaPlugin {

	private static final Random RANDOM = new Random();
	private static ProceduralDungeons instance = null;
	private DungeonManager dungeonManager = null;

	/**
	 * Run when the plugin is enabled
	 */
	public void onEnable() {
		saveDefaultConfig();
		instance = this;

		/**
		 * Registering the commands
		 */
		getCommand("dungeon").setExecutor(new DungeonCommand(this, getDungeonManager()));
		
		getDungeonManager().loadItems();
		getDungeonManager().loadDungeonInfo();
		getDungeonManager().loadTileSets();
	}

	/**
	 * Run when the plugin is disabled
	 */
	public void onDisable() {
		/**
		 * Goes through all the dungeon worlds and deletes them
		 */
		for (Dungeon dungeon : getDungeonManager().getDungeons().values()) {
			if (dungeon.getWorld() == null) {
				continue;
			}

			for (Player p : dungeon.getWorld().getPlayers()) {
				p.teleport(getServer().getWorlds().get(0).getSpawnLocation());
			}

			Bukkit.getServer().unloadWorld(dungeon.getWorld(), false);

			/**
			 * Deletes the world files of the dungeon world
			 */
			try (Stream<Path> files = Files.walk(dungeon.getWorld().getWorldFolder().toPath())) {
				files.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		getDungeonManager().getDungeons().clear();
	}

	/**
	 * Gets the instance of the {@link DungeonManager}
	 * 
	 * @return Instance of the DungeonManager
	 */
	public DungeonManager getDungeonManager() {
		if (dungeonManager == null) {
			dungeonManager = new DungeonManager();
		}
		return dungeonManager;
	}

	/**
	 * Gets the instance of the {@link Random}
	 * 
	 * @return Instance of the random
	 */
	public static Random getRandom() {
		return RANDOM;
	}

	/**
	 * Gets the instance of the {@link ProceduralDungeons}
	 * 
	 * @return Instance of the plugin
	 */
	public static ProceduralDungeons getInstance() {
		return instance;
	}
}
