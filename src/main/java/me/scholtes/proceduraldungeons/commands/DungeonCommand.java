package me.scholtes.proceduraldungeons.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.DungeonManager;
import me.scholtes.proceduraldungeons.utils.ChatUtils;

public class DungeonCommand implements CommandExecutor {
	
	private final ProceduralDungeons plugin;
	private final DungeonManager dungeonManager;
	
	/**
	 * The constructor of the {@link DungeonCommand}
	 * 
	 * @param plugin The instance of {@link ProceduralDungeons}
	 * @param dungeonManager The instance of {@link DungeonManager}
	 */
	public DungeonCommand(final ProceduralDungeons plugin, final DungeonManager dungeonManager) {
		this.plugin = plugin;
		this.dungeonManager = dungeonManager;
	}

	/**
	 * Code is executed whenever the command is run
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		/*
		 * Checks if the CommandSender is a Player
		 */
		if (!(sender instanceof Player)) {
			ChatUtils.message(sender, "&cYou need to be a player!");
			return true;
		}
		
		Player player = (Player) sender;
		
		/*
		 * Checks if the player put in the right arguments
		 */
		if (args.length < 2 || !args[0].equalsIgnoreCase("join")) {
			ChatUtils.message(player, "&cTo join a dungeon use /dungeon join <dungeon-name>");
			return true;
		}
		
		/*
		 * Checks if the specified dungeon name exists in the config
		 */
		if (!plugin.getConfig().isSet("dungeons." + args[1])) {
			ChatUtils.message(player, "&cThat dungeon doesn't exist!");
			return true;
		}

		/*
		 * Makes the player join the dungeon
		 */
		ChatUtils.message(player, "&aGenerating dungeon...");
		dungeonManager.joinDungeon(player, args[1]);
		
		return true;
	}

}
