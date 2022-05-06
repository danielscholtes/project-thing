package me.scholtes.proceduraldungeons;

import me.scholtes.proceduraldungeons.dungeon.commands.*;
import me.scholtes.proceduraldungeons.dungeon.manager.SQLManager;
import me.scholtes.proceduraldungeons.dungeon.manager.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.scholtes.proceduraldungeons.dungeon.DungeonManager;
import me.scholtes.proceduraldungeons.dungeon.listeners.BossListener;
import me.scholtes.proceduraldungeons.dungeon.listeners.PlayerListeners;
import me.scholtes.proceduraldungeons.dungeon.listeners.WandListeners;
import me.scholtes.proceduraldungeons.party.PartyData;
import me.scholtes.proceduraldungeons.party.commands.PartyCommand;
import me.scholtes.proceduraldungeons.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import java.util.stream.Stream;

public final class ProceduralDungeons extends JavaPlugin {

	private static ProceduralDungeons instance = null;
	private DungeonManager dungeonManager = null;
	private SQLManager sqlManager = null;
	private UserManager userManager = null;
	private PartyData partyData = null;
	private File messageFile = null;

	/**
	 * Run when the plugin is enabled
	 */
	public void onEnable() {
		saveDefaultConfig();
		instance = this;

		// Registering the commands
		getCommand("dungeon").setExecutor(new DungeonCommand(this));
		getCommand("party").setExecutor(new PartyCommand(getDungeonManager(), getPartyData(), getUserManager()));
		getCommand("auth").setExecutor(new AuthCommand(getUserManager()));
		getCommand("verify").setExecutor(new VerifyCommand(getUserManager()));
		getCommand("login").setExecutor(new LoginCommand(getUserManager()));
		getCommand("register").setExecutor(new RegisterCommand(getUserManager()));
		getCommand("logout").setExecutor(new LogoutCommand(getUserManager(), getPartyData(), getDungeonManager()));
		getCommand("statistics").setExecutor(new StatisticsCommand(getUserManager()));
		getCommand("help").setExecutor(new HelpCommand(getUserManager(), getPartyData(), getDungeonManager()));
		getCommand("resend").setExecutor(new ResendCommand(getUserManager()));
		
		getDungeonManager().reloadDungeons();
		StringUtils.loadMessages(getMessageFile());
		
		// Registering the listeners
		getServer().getPluginManager().registerEvents(new PlayerListeners(getDungeonManager(), getPartyData(), getUserManager()), this);
		getServer().getPluginManager().registerEvents(new BossListener(this), this);
		getServer().getPluginManager().registerEvents(new WandListeners(getDungeonManager(), this), this);
		
		// Gets rid of any unremoved worlds from a potential crash

		String[] loc = getConfig().getString("crash_location").split(";");
		Location crashLocation = new Location(Bukkit.getWorld(loc[3]), Double.parseDouble(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]));
		for (World world : Bukkit.getWorlds()) {
			if (world == null) {
				continue;
			}
			
			if (!world.getName().startsWith("Dungeon-")) {
				continue;
			}

			for (Player p : world.getPlayers()) {
				p.teleport(crashLocation);
			}

			Bukkit.getServer().unloadWorld(world, false);

			// Deletes the world files of the dungeon world
			try (Stream<Path> files = Files.walk(world.getWorldFolder().toPath())) {
				files.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Run when the plugin is disabled
	 */
	public void onDisable() {
		getDungeonManager().clearDungeons();
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
			dungeonManager = new DungeonManager(getUserManager());
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
			partyData = new PartyData(getUserManager());
		}
		return partyData;
	}

	public SQLManager getSqlManager() {
		if (sqlManager == null) {
			sqlManager = new SQLManager();
		}
		return sqlManager;
	}

	public UserManager getUserManager() {
		if (userManager == null) {
			userManager = new UserManager(getSqlManager());
		}
		return userManager;
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
