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
import me.scholtes.proceduraldungeons.dungeon.DungeonManager;
import me.scholtes.proceduraldungeons.dungeon.rooms.RoomType;
import me.scholtes.proceduraldungeons.dungeon.tilesets.TileSet;
import me.scholtes.proceduraldungeons.nbt.NBT;
import me.scholtes.proceduraldungeons.party.Party;
import me.scholtes.proceduraldungeons.party.PartyData;
import me.scholtes.proceduraldungeons.utils.ChatUtils;
import me.scholtes.proceduraldungeons.utils.ItemUtils;
import me.scholtes.proceduraldungeons.utils.Message;

public class DungeonCommand implements CommandExecutor {
	
	private final ProceduralDungeons plugin;
	private final DungeonManager dungeonManager;
	private final PartyData partyData;
	
	/**
	 * The constructor of the {@link DungeonCommand}
	 * 
	 * @param plugin The instance of {@link ProceduralDungeons}
	 * @param dungeonManager The instance of {@link DungeonManager}
	 */
	public DungeonCommand(ProceduralDungeons plugin, DungeonManager dungeonManager, PartyData partyData) {
		this.plugin = plugin;
		this.dungeonManager = dungeonManager;
		this.partyData = partyData;
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
			ChatUtils.message(sender,  ChatUtils.getMessage(Message.DUNGEON_HELP));
			return true;
		}
		
		switch (args[0].toLowerCase()) {
			case "reload": {
				/**
				 * Checks if the player has permission
				 */
				if (!sender.hasPermission("proceduraldungeons.admin")) {
					ChatUtils.message(sender,  ChatUtils.getMessage(Message.RELOAD_NO_PERMISSION));
					return true;
				}

				ChatUtils.loadMessages(plugin.getMessageFile());
				ChatUtils.message(sender, ChatUtils.getMessage(Message.RELOAD_RELOADED));
				
				return true;
			}
			
			case "chestwand": {
				/**
				 * Checks if the CommandSender is a Player
				 */
				if (!(sender instanceof Player)) {
					ChatUtils.message(sender, ChatUtils.getMessage(Message.NEED_TO_BE_PLAYER));
					return true;
				}
				
				Player player = (Player) sender;
				
				/**
				 * Checks if the player has permission
				 */
				if (!sender.hasPermission("proceduraldungeons.admin")) {
					ChatUtils.message(sender,  ChatUtils.getMessage(Message.CHESTWAND_NO_PERMISSION));
					return true;
				}
				

				/**
				 * Checks if the player put in the right arguments
				 */
				if (args.length < 4) {
					ChatUtils.message(sender, ChatUtils.getMessage(Message.CHESTWAND_INCORRECT));
					return true;
				}
				
				/**
				 * Checks if the tileset exists
				 */
				if (dungeonManager.getTileSet(args[1]) == null) {
					ChatUtils.message(sender, ChatUtils.getMessage(Message.CHESTWAND_TILESET_NOT_EXIST));
					return true;
				}
				
				TileSet tileSet = dungeonManager.getTileSet(args[1]);
				
				/**
				 * Checks if the tile is valid, and if so gives the player the wand
				 */
				for (RoomType tile : RoomType.values()) {
					if (tile.toString().equalsIgnoreCase(args[2])) {
						ItemStack wand = ItemUtils.createItemStack(Material.BLAZE_ROD, 1, "&6Chest Wand", Arrays.asList("", "&7Left-Click the schematic pasting location and Right-Click", "&7the possible chest location to add it to the variation"));
						NBT nbt = NBT.get(wand);
						nbt.setString("WandType", "Chest");
						nbt.setString("TileSet", tileSet.getTileSetName());
						nbt.setString("RoomType", tile.toString());
						nbt.setString("Variation", args[3]);
						player.getInventory().addItem(nbt.apply(wand));
						ChatUtils.message(sender, ChatUtils.getMessage(Message.CHESTWAND_WAND_GIVEN));
						return true;
					}
				}

				ChatUtils.message(sender, ChatUtils.getMessage(Message.CHESTWAND_TILE_NOT_VALID));
				return true;
			}
			
			case "mobwand": {
				/**
				 * Checks if the CommandSender is a Player
				 */
				if (!(sender instanceof Player)) {
					ChatUtils.message(sender, ChatUtils.getMessage(Message.NEED_TO_BE_PLAYER));
					return true;
				}
				
				Player player = (Player) sender;
				
				/**
				 * Checks if the player has permission
				 */
				if (!sender.hasPermission("proceduraldungeons.admin")) {
					ChatUtils.message(sender,  ChatUtils.getMessage(Message.MOBWAND_NO_PERMISSION));
					return true;
				}

				/**
				 * Checks if the player put in the right arguments
				 */
				if (args.length < 4) {
					ChatUtils.message(sender, ChatUtils.getMessage(Message.MOBWAND_INCORRECT));
					return true;
				}

				/**
				 * Checks if the tileset exists
				 */
				if (dungeonManager.getTileSet(args[1]) == null) {
					ChatUtils.message(sender, ChatUtils.getMessage(Message.MOBWAND_TILESET_NOT_EXIST));
					return true;
				}
				
				TileSet tileSet = dungeonManager.getTileSet(args[1]);

				/**
				 * Checks if the tile is valid, and if so gives the player the wand
				 */
				for (RoomType tile : RoomType.values()) {
					if (tile.toString().equalsIgnoreCase(args[2])) {
						ItemStack wand = ItemUtils.createItemStack(Material.BLAZE_ROD, 1, "&6Mob Wand", Arrays.asList("", "&7Left-Click the schematic pasting location and Right-Click", "&7the possible mob location to add it to the variation"));
						NBT nbt = NBT.get(wand);
						nbt.setString("WandType", "Mob");
						nbt.setString("TileSet", tileSet.getTileSetName());
						nbt.setString("RoomType", tile.toString());
						nbt.setString("Variation", args[3]);
						player.getInventory().addItem(nbt.apply(wand));
						ChatUtils.message(sender, ChatUtils.getMessage(Message.MOBWAND_WAND_GIVEN));
						return true;
					}
				}

				ChatUtils.message(sender, ChatUtils.getMessage(Message.MOBWAND_TILE_NOT_VALID));
				return true;
			}
			
			case "join": {
				/**
				 * Checks if the CommandSender is a Player
				 */
				if (!(sender instanceof Player)) {
					ChatUtils.message(sender, ChatUtils.getMessage(Message.NEED_TO_BE_PLAYER));
					return true;
				}
				
				Player player = (Player) sender;
				
				/**
				 * Checks if the player put in the right arguments
				 */
				if (args.length < 2 || !args[0].equalsIgnoreCase("join")) {
					ChatUtils.message(player,  ChatUtils.getMessage(Message.DUNGEON_JOIN_INCORRECT));
					return true;
				}

				/**
				 * Checks if the specified dungeon name exists in the config
				 */
				if (!plugin.getConfig().isSet("dungeons." + args[1])) {
					ChatUtils.message(player,  ChatUtils.getMessage(Message.DUNGEON_JOIN_NOT_EXIST));
					return true;
				}

				/**
				 * Checks if the player has permission for the specified dungeon
				 */
				if (!player.hasPermission("dungeon." + args[1])) {
					ChatUtils.message(player,  ChatUtils.getMessage(Message.DUNGEON_JOIN_NO_PERM));
					return true;
				}
				
				/**
				 * Checks if the player is in a party and a party leader
				 */
				Party party = partyData.getPartyFromPlayer(player.getUniqueId());
				if (party != null && !party.getOwner().equals(player.getUniqueId())) {
					ChatUtils.message(player,  ChatUtils.getMessage(Message.NOT_LEADER));
					return true;
				}
				
				/**
				 * Checks if the player is in a dungeon
				 */
				
				if (dungeonManager.getDungeonFromPlayer(player.getUniqueId(), party) != null) {
					ChatUtils.message(player,  ChatUtils.getMessage(Message.DUNGEON_JOIN_ALREADY_IN));
					return true;
				}

				/**
				 * Makes the player join the dungeon
				 */
				if (party != null) {
					party.messageMembers(ChatUtils.getMessage(Message.DUNGEON_JOIN_GENERATING));
				} else {
					ChatUtils.message(player, ChatUtils.getMessage(Message.DUNGEON_JOIN_GENERATING));
				}
				dungeonManager.joinDungeon(player, args[1]);
				return true;
			}
			
			case "leave": {
				/**
				 * Checks if the CommandSender is a Player
				 */
				if (!(sender instanceof Player)) {
					ChatUtils.message(sender, ChatUtils.getMessage(Message.NEED_TO_BE_PLAYER));
					return true;
				}
				
				Player player = (Player) sender;
				
				/**
				 * Checks if the player is in a dungeon
				 */
				Party party = partyData.getPartyFromPlayer(player.getUniqueId());
				Dungeon dungeon = dungeonManager.getDungeonFromPlayer(player.getUniqueId(), party);
				if (dungeon == null) {
					ChatUtils.message(player, ChatUtils.getMessage(Message.DUNGEON_LEAVE_NOT_IN));
					return true;
				}

				/**
				 * Makes the player leave the dungeon
				 */
				if (party != null) {
					partyData.removePlayerFromParty(party, player.getUniqueId());
					ChatUtils.message(player, ChatUtils.getMessage(Message.DUNGEON_LEAVE_LEAVE));
					party.messageMembers(ChatUtils.replaceAll(ChatUtils.getMessage(Message.PARTY_PLAYER_LEFT), "{player}", player.getName()));
					return true;
				}
				ChatUtils.message(player, ChatUtils.getMessage(Message.DUNGEON_LEAVE_LEAVE));
				dungeonManager.removeDungeon(dungeon);
				return true;
			}
			
			default: {
				ChatUtils.message(sender, ChatUtils.getMessage(Message.DUNGEON_HELP));
				return true;
			}
				
		}
	}

}
