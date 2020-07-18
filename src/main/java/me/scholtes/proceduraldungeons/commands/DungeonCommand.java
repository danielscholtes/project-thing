package me.scholtes.proceduraldungeons.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.Utils;
import me.scholtes.proceduraldungeons.dungeon.DungeonManager;

public class DungeonCommand implements CommandExecutor {
	
	private final ProceduralDungeons plugin;
	private final DungeonManager dungeonManager;
	
	public DungeonCommand(final ProceduralDungeons plugin, final DungeonManager dungeonManager) {
		this.plugin = plugin;
		this.dungeonManager = dungeonManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			Utils.message(sender, "&cYou need to be a player!");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (args.length < 2 || args[0].equalsIgnoreCase("join")) {
			Utils.message(sender, "&cTo join a dungeon use /dungeon join <dungeon-name>");
			return true;
		}
		
		if (!plugin.getConfig().isSet("dungeons." + args[1])) {
			Utils.message(sender, "&cThat dungeon doesn't exist!");
			return true;
		}
		
		
		
		return true;
	}

}
