package me.scholtes.proceduraldungeons.dungeon.commands;

import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.Dungeon;
import me.scholtes.proceduraldungeons.dungeon.rooms.RoomType;
import me.scholtes.proceduraldungeons.dungeon.tilesets.TileSet;
import me.scholtes.proceduraldungeons.nbt.NBT;
import me.scholtes.proceduraldungeons.party.Party;
import me.scholtes.proceduraldungeons.utils.StringUtils;
import me.scholtes.proceduraldungeons.utils.ItemUtils;
import me.scholtes.proceduraldungeons.utils.Message;

public class DungeonCommand implements CommandExecutor {
	
	private final ProceduralDungeons plugin;
	
	/**
	 * The constructor of the {@link DungeonCommand}
	 * 
	 * @param plugin The instance of {@link ProceduralDungeons}
	 */
	public DungeonCommand(ProceduralDungeons plugin) {
		this.plugin = plugin;
	}

	/**
	 * Code is executed whenever the command is run
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		/**
		 * Checks if player has any arguments
		 */
		if (args.length < 1) {
			StringUtils.message(sender,  StringUtils.getMessage(Message.DUNGEON_HELP));
			return true;
		}
		
		switch (args[0].toLowerCase()) {
			case "reload": {
				/**
				 * Checks if the player has permission
				 */
				if (!sender.hasPermission("proceduraldungeons.admin")) {
					StringUtils.message(sender,  StringUtils.getMessage(Message.RELOAD_NO_PERMISSION));
					return true;
				}

				/**
				 * Reloads all dungeon information and messages
				 */
				StringUtils.loadMessages(plugin.getMessageFile());
				plugin.reloadConfig();
				plugin.getDungeonManager().reloadDungeons();
				StringUtils.message(sender, StringUtils.getMessage(Message.RELOAD_RELOADED));
				
				return true;
			}
			
			case "chestwand": {
				/**
				 * Checks if the CommandSender is a Player
				 */
				if (!(sender instanceof Player)) {
					StringUtils.message(sender, StringUtils.getMessage(Message.NEED_TO_BE_PLAYER));
					return true;
				}
				
				Player player = (Player) sender;
				
				/**
				 * Checks if the player has permission
				 */
				if (!sender.hasPermission("proceduraldungeons.admin")) {
					StringUtils.message(sender,  StringUtils.getMessage(Message.CHESTWAND_NO_PERMISSION));
					return true;
				}
				

				/**
				 * Checks if the player put in the right arguments
				 */
				if (args.length < 4) {
					StringUtils.message(sender, StringUtils.getMessage(Message.CHESTWAND_INCORRECT));
					return true;
				}
				
				/**
				 * Checks if the tileset exists
				 */
				if (plugin.getDungeonManager().getTileSet(args[1]) == null) {
					StringUtils.message(sender, StringUtils.getMessage(Message.CHESTWAND_TILESET_NOT_EXIST));
					return true;
				}
				
				TileSet tileSet = plugin.getDungeonManager().getTileSet(args[1]);
				
				/**
				 * Checks if the tile is valid, and if so gives the player the wand
				 */
				for (RoomType tile : RoomType.values()) {
					if (tile.toString().equalsIgnoreCase(args[2])) {
						ItemStack wand = ItemUtils.createItemStack(Material.BLAZE_ROD, 1, "&6Chest Wand", Arrays.asList("", "&7Left-Click the schematic pasting location and Right-Click", "&7the possible chest location to add it to the variation"), null);
						NBT nbt = NBT.get(wand);
						nbt.setString("WandType", "Chest");
						nbt.setString("TileSet", tileSet.getTileSetName());
						nbt.setString("RoomType", tile.toString());
						nbt.setString("Variation", args[3]);
						player.getInventory().addItem(nbt.apply(wand));
						StringUtils.message(sender, StringUtils.getMessage(Message.CHESTWAND_WAND_GIVEN));
						return true;
					}
				}

				StringUtils.message(sender, StringUtils.getMessage(Message.CHESTWAND_TILE_NOT_VALID));
				return true;
			}
			
			case "mobwand": {
				/**
				 * Checks if the CommandSender is a Player
				 */
				if (!(sender instanceof Player)) {
					StringUtils.message(sender, StringUtils.getMessage(Message.NEED_TO_BE_PLAYER));
					return true;
				}
				
				Player player = (Player) sender;
				
				/**
				 * Checks if the player has permission
				 */
				if (!sender.hasPermission("proceduraldungeons.admin")) {
					StringUtils.message(sender,  StringUtils.getMessage(Message.MOBWAND_NO_PERMISSION));
					return true;
				}

				/**
				 * Checks if the player put in the right arguments
				 */
				if (args.length < 4) {
					StringUtils.message(sender, StringUtils.getMessage(Message.MOBWAND_INCORRECT));
					return true;
				}

				/**
				 * Checks if the tileset exists
				 */
				if (plugin.getDungeonManager().getTileSet(args[1]) == null) {
					StringUtils.message(sender, StringUtils.getMessage(Message.MOBWAND_TILESET_NOT_EXIST));
					return true;
				}
				
				TileSet tileSet = plugin.getDungeonManager().getTileSet(args[1]);

				/**
				 * Checks if the tile is valid, and if so gives the player the wand
				 */
				for (RoomType tile : RoomType.values()) {
					if (tile.toString().equalsIgnoreCase(args[2])) {
						ItemStack wand = ItemUtils.createItemStack(Material.BLAZE_ROD, 1, "&6Mob Wand", Arrays.asList("", "&7Left-Click the schematic pasting location and Right-Click", "&7the possible mob location to add it to the variation"), null);
						NBT nbt = NBT.get(wand);
						nbt.setString("WandType", "Mob");
						nbt.setString("TileSet", tileSet.getTileSetName());
						nbt.setString("RoomType", tile.toString());
						nbt.setString("Variation", args[3]);
						player.getInventory().addItem(nbt.apply(wand));
						StringUtils.message(sender, StringUtils.getMessage(Message.MOBWAND_WAND_GIVEN));
						return true;
					}
				}

				StringUtils.message(sender, StringUtils.getMessage(Message.MOBWAND_TILE_NOT_VALID));
				return true;
			}
			
			case "join": {
				/**
				 * Checks if the CommandSender is a Player
				 */
				if (!(sender instanceof Player)) {
					StringUtils.message(sender, StringUtils.getMessage(Message.NEED_TO_BE_PLAYER));
					return true;
				}
				
				Player player = (Player) sender;
				
				/**
				 * Checks if the player put in the right arguments
				 */
				if (args.length < 2 || !args[0].equalsIgnoreCase("join")) {
					StringUtils.message(player,  StringUtils.getMessage(Message.DUNGEON_JOIN_INCORRECT));
					return true;
				}

				/**
				 * Checks if the specified dungeon name exists in the config
				 */
				if (!plugin.getConfig().isSet("dungeons." + args[1])) {
					StringUtils.message(player,  StringUtils.getMessage(Message.DUNGEON_JOIN_NOT_EXIST));
					return true;
				}

				/**
				 * Checks if the player has permission for the specified dungeon
				 */
				if (!player.hasPermission("dungeon." + args[1])) {
					StringUtils.message(player,  StringUtils.getMessage(Message.DUNGEON_JOIN_NO_PERM));
					return true;
				}
				
				/**
				 * Checks if the player is in a party and a party leader
				 */
				Party party = plugin.getPartyData().getPartyFromPlayer(player.getUniqueId());
				if (party != null && !party.getOwner().equals(player.getUniqueId())) {
					StringUtils.message(player,  StringUtils.getMessage(Message.NOT_LEADER));
					return true;
				}
				
				/**
				 * Checks if the player is in a dungeon
				 */
				
				if (plugin.getDungeonManager().getDungeonFromPlayer(player.getUniqueId(), party) != null) {
					StringUtils.message(player,  StringUtils.getMessage(Message.DUNGEON_JOIN_ALREADY_IN));
					return true;
				}

				/**
				 * Makes the player join the dungeon
				 */
				if (party != null) {
					party.messageMembers(StringUtils.getMessage(Message.DUNGEON_JOIN_GENERATING));
				} else {
					StringUtils.message(player, StringUtils.getMessage(Message.DUNGEON_JOIN_GENERATING));
				}
				plugin.getDungeonManager().joinDungeon(player, args[1]);
				return true;
			}
			
			case "leave": {
				/**
				 * Checks if the CommandSender is a Player
				 */
				if (!(sender instanceof Player)) {
					StringUtils.message(sender, StringUtils.getMessage(Message.NEED_TO_BE_PLAYER));
					return true;
				}
				
				Player player = (Player) sender;
				
				/**
				 * Checks if the player is in a dungeon
				 */
				Party party = plugin.getPartyData().getPartyFromPlayer(player.getUniqueId());
				Dungeon dungeon = plugin.getDungeonManager().getDungeonFromPlayer(player.getUniqueId(), party);
				if (dungeon == null) {
					StringUtils.message(player, StringUtils.getMessage(Message.DUNGEON_LEAVE_NOT_IN));
					return true;
				}

				/**
				 * Makes the player leave the dungeon
				 */
				if (party != null) {
					plugin.getPartyData().removePlayerFromParty(party, player.getUniqueId());
					StringUtils.message(player, StringUtils.getMessage(Message.DUNGEON_LEAVE_LEAVE));
					party.messageMembers(StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_PLAYER_LEFT), "{player}", player.getName()));
					return true;
				}
				StringUtils.message(player, StringUtils.getMessage(Message.DUNGEON_LEAVE_LEAVE));
				plugin.getDungeonManager().removeDungeon(dungeon);
				return true;
			}
			
			default: {
				StringUtils.message(sender, StringUtils.getMessage(Message.DUNGEON_HELP));
				return true;
			}
				
		}
	}

}
