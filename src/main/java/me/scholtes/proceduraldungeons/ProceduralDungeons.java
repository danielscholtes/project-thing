package me.scholtes.proceduraldungeons;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.scholtes.proceduraldungeons.dungeon.Dungeon;
import me.scholtes.proceduraldungeons.dungeon.DungeonManager;
import me.scholtes.proceduraldungeons.dungeon.commands.DungeonCommand;
import me.scholtes.proceduraldungeons.dungeon.listeners.BossListener;
import me.scholtes.proceduraldungeons.dungeon.listeners.PlayerListeners;
import me.scholtes.proceduraldungeons.dungeon.listeners.WandListeners;
import me.scholtes.proceduraldungeons.party.PartyData;
import me.scholtes.proceduraldungeons.party.commands.PartyCommand;
import me.scholtes.proceduraldungeons.utils.ChatUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import java.util.stream.Stream;

public final class ProceduralDungeons extends JavaPlugin {

	private static ProceduralDungeons instance = null;
	private DungeonManager dungeonManager = null;
	private PartyData partyData = null;
	private File messageFile = null;

	/**
	 * Run when the plugin is enabled
	 */
	public void onEnable() {
		saveDefaultConfig();
		instance = this;

		/**
		 * Registering the commands
		 */
		getCommand("dungeon").setExecutor(new DungeonCommand(this, getDungeonManager(), getPartyData()));
		getCommand("party").setExecutor(new PartyCommand(getDungeonManager(), getPartyData()));
		
		getDungeonManager().loadItems();
		getDungeonManager().loadDungeonInfo();
		getDungeonManager().loadTileSets();
		ChatUtils.loadMessages(getMessageFile());
		
		/**
		 * Registering the listeners
		 */
		getServer().getPluginManager().registerEvents(new PlayerListeners(getDungeonManager(), getPartyData()), this);
		getServer().getPluginManager().registerEvents(new BossListener(this, getDungeonManager(), getPartyData()), this);
		getServer().getPluginManager().registerEvents(new WandListeners(getDungeonManager(), this), this);
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
				p.teleport(dungeon.getDungeonInfo().getFinishLocation());
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

	public File getMessageFile() {
		if (messageFile == null) {
			messageFile = new File(getDataFolder().getAbsolutePath(), "messages.yml");
		}
		return messageFile;
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
	 * Gets the instance of the {@link DungeonManager}
	 * 
	 * @return Instance of the DungeonManager
	 */
	public PartyData getPartyData() {
		if (partyData == null) {
			partyData = new PartyData();
		}
		return partyData;
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
