package me.scholtes.proceduraldungeons.party.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.scholtes.proceduraldungeons.party.Party;
import me.scholtes.proceduraldungeons.party.PartyData;
import me.scholtes.proceduraldungeons.utils.StringUtils;
import me.scholtes.proceduraldungeons.utils.Message;
import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.DungeonManager;

public class PartyCommand implements CommandExecutor {
	
	private final DungeonManager dungeonManager;
	private final PartyData partyData;
	
	public PartyCommand(DungeonManager dungeonManager, PartyData partyData) {
		this.partyData = partyData;
		this.dungeonManager = dungeonManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		/**
		 * Checks if the CommandSender is a Player
		 */
		if (!(sender instanceof Player)) {
			StringUtils.message(sender, StringUtils.getMessage(Message.NEED_TO_BE_PLAYER));
			return true;
		}
		
		Player player = (Player) sender;
		
		/**
		 * Checks if player has input any arguments
		 */
		if (args.length < 1) {
			StringUtils.message(player, StringUtils.getMessage(Message.PARTY_HELP));
			return true;
		}
		
		switch (args[0].toLowerCase()) {
			case "invite": {
				/**
				 * Checks if the player put in the right arguments
				 */
				if (args.length < 2) {
					StringUtils.message(player, StringUtils.getMessage(Message.PARTY_INVITE_INCORRECT));
					return true;
				}
	
				/**
				 * Gets the player's current party
				 */
				Party party = partyData.getPartyFromPlayer(player.getUniqueId());
				if (party == null) {
					party = partyData.createParty(player.getUniqueId());
				}
				
				/**
				 * Checks if the player is the party leader
				 */
				if (!party.getOwner().equals(player.getUniqueId())) {
					StringUtils.message(player, StringUtils.getMessage(Message.NOT_LEADER));
					return true;
				}
				
				/**
				 * Checks if the party has reached it's max member limit
				 */
				if (party.getMembers().size() >= ProceduralDungeons.getInstance().getConfig().getInt("party.max_members")) {
					StringUtils.message(player, StringUtils.getMessage(Message.PARTY_INVITE_MAXIMUM));
					return true;
				}

				/**
				 * Checks if the target is online
				 */
				Player toInvite = Bukkit.getPlayerExact(args[1]);
				if (toInvite == null) {
					StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_NOT_ONLINE), "{player}", args[1]));
					return true;
				}
				
				/**
				 * Checks if the target is already in a party
				 */
				if (partyData.getPartyFromPlayer(toInvite.getUniqueId()) != null) {
					StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_INVITE_ALREADY_IN), "{player}", toInvite.getName()));
					return true;
				}

				/**
				 * Checks if the player is in a dungeon
				 */
				if (dungeonManager.getDungeonFromPlayer(player.getUniqueId(), party) != null) {
					StringUtils.message(player, StringUtils.getMessage(Message.PARTY_INVITE_CANT_INVITE));
					return true;
				}

				/**
				 * Checks if the target is in a dungeon
				 */
				if (dungeonManager.getDungeonFromPlayer(toInvite.getUniqueId(), partyData.getPartyFromPlayer(toInvite.getUniqueId())) != null) {
					StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_INVITE_IN_DUNGEON), "{player}", toInvite.getName()));
					return true;
				}
				
				/**
				 * Checks if the target has already been invited
				 */
				if (partyData.getInvitations().containsKey(toInvite.getUniqueId()) && partyData.getInvitations().get(toInvite.getUniqueId()).contains(party)) {
					StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_INVITE_ALREADY_INVITED), "{player}", toInvite.getName()));
					return true;
				}
	
				/**
				 * Invites the target
				 */
				StringUtils.message(toInvite, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_INVITE_BEEN_INVITED), "{player}", player.getName()));
				party.messageMembers(StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_INVITE_INVITE_SENT), "{player}", toInvite.getName()));
				partyData.sendInvitation(party, toInvite.getUniqueId());
				return true;
			}
			
			case "join": {
				/**
				 * Gets the player's current party
				 */
				Party party = partyData.getPartyFromPlayer(player.getUniqueId());
				if (party != null) {
					StringUtils.message(player, StringUtils.getMessage(Message.PARTY_JOIN_ALREADY_IN));
					return true;
				}

				/**
				 * Checks if the player put in the right arguments
				 */
				if (args.length < 2) {
					StringUtils.message(player, StringUtils.getMessage(Message.PARTY_JOIN_INCORRECT));
					return true;
				}

				/**
				 * Checks if the target is online
				 */
				Player toJoin = Bukkit.getPlayerExact(args[1]);
				if (toJoin == null) {
					StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_NOT_ONLINE), "{player}", args[1]));
					return true;
				}
				
				/**
				 * Checks if the target has invited the player
				 */
				Party partyToJoin = partyData.getPartyFromPlayer(toJoin.getUniqueId());
				if (!partyData.getInvitations().containsKey(player.getUniqueId()) || partyToJoin == null || !partyData.getInvitations().get(player.getUniqueId()).contains(partyToJoin)) {
					StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_JOIN_NOT_INVITED), "{player}", toJoin.getName()));
					return true;
				}
				
				/**
				 * Checks if the player is in a dungeon
				 */
				if (dungeonManager.getDungeonFromPlayer(player.getUniqueId(), party) != null) {
					StringUtils.message(player, StringUtils.getMessage(Message.PARTY_JOIN_CANT_JOIN));
					return true;
				}

				/**
				 * Checks if the target is in a dungeon
				 */
				if (dungeonManager.getDungeonFromPlayer(toJoin.getUniqueId(), partyData.getPartyFromPlayer(toJoin.getUniqueId())) != null) {
					StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_JOIN_IN_DUNGEON), "{player}", toJoin.getName()));
					return true;
				}
				
				/**
				 * Checks if the party has reached it's max member limit
				 */
				if (partyToJoin.getMembers().size() >= ProceduralDungeons.getInstance().getConfig().getInt("party.max_members")) {
					StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_JOIN_FULL), "{player}", toJoin.getName()));
					return true;
				}

				/**
				 * Adds the player to the party
				 */
				StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_JOIN_JOIN_PARTY), "{player}", toJoin.getName()));
				partyToJoin.messageMembers(StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_JOIN_PLAYER_JOINED), "{player}", player.getName()));
				partyData.addPlayerToParty(partyToJoin, player.getUniqueId());
				
				return true;
			}
			
			case "leave": {
				/**
				 * Gets the player's current party
				 */
				Party party = partyData.getPartyFromPlayer(player.getUniqueId());
				if (party == null) {
					StringUtils.message(player, StringUtils.getMessage(Message.PARTY_NOT_IN));
					return true;
				}
				
				/**
				 * Removes the player from the party
				 */
				partyData.removePlayerFromParty(party, player.getUniqueId());
				StringUtils.message(player, StringUtils.getMessage(Message.PARTY_LEAVE_LEAVE));
				party.messageMembers(StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_PLAYER_LEFT), "{player}", player.getName()));
				return true;			
			}
			
			case "kick": {
				/**
				 * Gets the player's current party
				 */
				Party party = partyData.getPartyFromPlayer(player.getUniqueId());
				if (party == null) {
					StringUtils.message(player, StringUtils.getMessage(Message.PARTY_NOT_IN));
					return true;
				}

				/**
				 * Checks if the player is the party leader
				 */
				if (!party.getOwner().equals(player.getUniqueId())) {
					StringUtils.message(player, StringUtils.getMessage(Message.NOT_LEADER));
					return true;
				}

				/**
				 * Checks if the player put in the right arguments
				 */
				if (args.length < 2) {
					StringUtils.message(player, StringUtils.getMessage(Message.PARTY_KICK_INCORRECT));
					return true;
				}
	
				/**
				 * Checks if the target is online
				 */
				Player toKick = Bukkit.getPlayerExact(args[1]);
				if (toKick == null) {
					StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_NOT_ONLINE), "{player}", args[1]));
					return true;
				}

				/**
				 * Prevents player from kicking themselves from the party
				 */
				if (party.getOwner().equals(toKick.getUniqueId())) {
					StringUtils.message(player, StringUtils.getMessage(Message.PARTY_KICK_CANT_KICK_YOURSELF));
					return true;
				}
				
				/**
				 * Checks if the target is in the party
				 */
				if (!party.getMembers().contains(toKick.getUniqueId())) {
					StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_KICK_NOT_IN), "{player}", toKick.getName()));
					return true;
				}

				/**
				 * Removes the target from the party
				 */
				partyData.removePlayerFromParty(party, toKick.getUniqueId());
				StringUtils.message(toKick, StringUtils.getMessage(Message.PARTY_KICK_KICKED));
				party.messageMembers(StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_KICK_PLAYER_KICKED), "{player}", toKick.getName()));
				return true;
			}
			
			case "list": {
				/**
				 * Gets the player's current party
				 */
				Party party = partyData.getPartyFromPlayer(player.getUniqueId());
				if (party == null) {
					StringUtils.message(player, StringUtils.getMessage(Message.PARTY_NOT_IN));
					return true;
				}
	
				/**
				 * Prints out a list of all the party members (including leader)
				 */
				StringUtils.message(player, StringUtils.getMessage(Message.PARTY_LIST_HEADER));
				StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_LIST_MEMBER), "{player}", Bukkit.getPlayer(party.getOwner()).getName()));
				for (UUID member : party.getMembers()) {
					StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_LIST_MEMBER), "{player}", Bukkit.getPlayer(member).getName()));
				}
				StringUtils.message(player, StringUtils.getMessage(Message.PARTY_LIST_FOOTER));
				return true;
			}
			
			default: {
				/**
				 * Prints out a list of all commands if the player used an invalid command
				 */
				StringUtils.message(player, StringUtils.getMessage(Message.PARTY_HELP));
				return true;
			}
			
		}
	}

}
