package me.scholtes.proceduraldungeons.party.commands;

import java.util.UUID;

import me.scholtes.proceduraldungeons.dungeon.manager.UserManager;
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
	private final UserManager userManager;
	
	public PartyCommand(DungeonManager dungeonManager, PartyData partyData, UserManager userManager) {
		this.partyData = partyData;
		this.dungeonManager = dungeonManager;
		this.userManager = userManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		// Checks if the CommandSender is a Player
		if (!(sender instanceof Player)) {
			StringUtils.message(sender, StringUtils.getMessage(Message.NEED_TO_BE_PLAYER));
			return true;
		}
		
		Player player = (Player) sender;

		if (!userManager.isLoggedIn(player.getUniqueId())) {
			StringUtils.message(sender, "&cYou aren't logged in");
			return true;
		}

		if (!userManager.isVerified(userManager.getID(player.getUniqueId()))) {
			StringUtils.message(sender, "&cYou aren't verified");
			return true;
		}

		// Checks if player has input any arguments
		if (args.length < 1) {
			StringUtils.message(player, StringUtils.getMessage(Message.PARTY_HELP));
			return true;
		}

		switch (args[0].toLowerCase()) {
			case "invite" -> {
				// Checks if the player put in the right arguments
				if (args.length < 2) {
					StringUtils.message(player, StringUtils.getMessage(Message.PARTY_INVITE_INCORRECT));
					return true;
				}

				// Gets the player's current party
				Party party = partyData.getPartyFromPlayer(player.getUniqueId());
				if (party == null) {
					party = partyData.createParty(player.getUniqueId());
				}

				/// Checks if the player is the party leader
				if (!party.getOwner().equals(player.getUniqueId())) {
					StringUtils.message(player, StringUtils.getMessage(Message.NOT_LEADER));
					return true;
				}

				/// Checks if the party has reached it's max member limit
				if (party.getMembers().size() >= ProceduralDungeons.getInstance().getConfig().getInt("party.max_members")) {
					StringUtils.message(player, StringUtils.getMessage(Message.PARTY_INVITE_MAXIMUM));
					return true;
				}

				/// Checks if the target is online
				if (!userManager.isLoggedIn(args[1])) {
					StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_NOT_ONLINE), "{player}", args[1]));
					return true;
				}

				int id = userManager.getUserIDUsername(args[1]);
				String username = userManager.getUsernameID(id);
				Player toInvite = Bukkit.getPlayer(userManager.getUUID(id));
				// Checks if the target is already in a party
				if (partyData.getPartyFromPlayer(toInvite.getUniqueId()) != null) {
					StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_INVITE_ALREADY_IN), "{player}", username));
					return true;
				}

				// Checks if the player is in a dungeon
				if (dungeonManager.getDungeonFromPlayer(player.getUniqueId(), party) != null) {
					StringUtils.message(player, StringUtils.getMessage(Message.PARTY_INVITE_CANT_INVITE));
					return true;
				}

				// Checks if the target is in a dungeon
				if (dungeonManager.getDungeonFromPlayer(toInvite.getUniqueId(), partyData.getPartyFromPlayer(toInvite.getUniqueId())) != null) {
					StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_INVITE_IN_DUNGEON), "{player}", username));
					return true;
				}

				// Checks if the target has already been invited
				if (partyData.getInvitations().containsKey(toInvite.getUniqueId()) && partyData.getInvitations().get(toInvite.getUniqueId()).contains(party)) {
					StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_INVITE_ALREADY_INVITED), "{player}", username));
					return true;
				}

				// Invites the target
				StringUtils.message(toInvite, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_INVITE_BEEN_INVITED), "{player}", userManager.getUsernameID(userManager.getID(player.getUniqueId()))));
				party.messageMembers(StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_INVITE_INVITE_SENT), "{player}", username));
				partyData.sendInvitation(party, toInvite.getUniqueId());
				return true;
			}
			case "join" -> {
				// Checks if the player put in the right arguments
				if (args.length < 2) {
					StringUtils.message(player, StringUtils.getMessage(Message.PARTY_JOIN_INCORRECT));
					return true;
				}

				// Gets the player's current party
				Party party = partyData.getPartyFromPlayer(player.getUniqueId());
				if (party != null) {
					StringUtils.message(player, StringUtils.getMessage(Message.PARTY_JOIN_ALREADY_IN));
					return true;
				}

				// Checks if the target is online
				if (!userManager.isLoggedIn(args[1])) {
					StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_NOT_ONLINE), "{player}", args[1]));
					return true;
				}

				// Checks if the target has invited the player
				int id = userManager.getUserIDUsername(args[1]);
				String username = userManager.getUsernameID(id);
				Player toJoin = Bukkit.getPlayer(userManager.getUUID(id));

				Party partyToJoin = partyData.getPartyFromPlayer(toJoin.getUniqueId());
				if (!partyData.getInvitations().containsKey(player.getUniqueId()) || partyToJoin == null || !partyData.getInvitations().get(player.getUniqueId()).contains(partyToJoin)) {
					StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_JOIN_NOT_INVITED), "{player}", username));
					return true;
				}

				// Checks if the player is in a dungeon
				if (dungeonManager.getDungeonFromPlayer(player.getUniqueId(), party) != null) {
					StringUtils.message(player, StringUtils.getMessage(Message.PARTY_JOIN_CANT_JOIN));
					return true;
				}

				// Checks if the target is in a dungeon
				if (dungeonManager.getDungeonFromPlayer(toJoin.getUniqueId(), partyData.getPartyFromPlayer(toJoin.getUniqueId())) != null) {
					StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_JOIN_IN_DUNGEON), "{player}", username));
					return true;
				}

				// Checks if the party has reached it's max member limit
				if (partyToJoin.getMembers().size() >= ProceduralDungeons.getInstance().getConfig().getInt("party.max_members")) {
					StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_JOIN_FULL), "{player}", username));
					return true;
				}

				// Adds the player to the party
				StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_JOIN_JOIN_PARTY), "{player}", username));
				partyToJoin.messageMembers(StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_JOIN_PLAYER_JOINED), "{player}", userManager.getUsernameID(userManager.getID(player.getUniqueId()))));
				partyData.addPlayerToParty(partyToJoin, player.getUniqueId());

				return true;
			}
			case "leave" -> {
				// Gets the player's current party
				Party party = partyData.getPartyFromPlayer(player.getUniqueId());
				if (party == null) {
					StringUtils.message(player, StringUtils.getMessage(Message.PARTY_NOT_IN));
					return true;
				}

				// Removes the player from the party
				partyData.removePlayerFromParty(party, player.getUniqueId());
				StringUtils.message(player, StringUtils.getMessage(Message.PARTY_LEAVE_LEAVE));
				party.messageMembers(StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_PLAYER_LEFT), "{player}", userManager.getUsernameID(userManager.getID(player.getUniqueId()))));
				return true;
			}
			case "kick" -> {
				// Gets the player's current party
				Party party = partyData.getPartyFromPlayer(player.getUniqueId());
				if (party == null) {
					StringUtils.message(player, StringUtils.getMessage(Message.PARTY_NOT_IN));
					return true;
				}

				// Checks if the player is the party leader
				if (!party.getOwner().equals(player.getUniqueId())) {
					StringUtils.message(player, StringUtils.getMessage(Message.NOT_LEADER));
					return true;
				}

				// Checks if the player put in the right arguments
				if (args.length < 2) {
					StringUtils.message(player, StringUtils.getMessage(Message.PARTY_KICK_INCORRECT));
					return true;
				}

				// Checks if the target is online
				if (!userManager.isLoggedIn(args[1])) {
					StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_NOT_ONLINE), "{player}", args[1]));
					return true;
				}

				int id = userManager.getUserIDUsername(args[1]);
				String username = userManager.getUsernameID(id);
				Player toKick = Bukkit.getPlayer(userManager.getUUID(id));
				// Prevents player from kicking themselves from the party
				if (party.getOwner().equals(toKick.getUniqueId())) {
					StringUtils.message(player, StringUtils.getMessage(Message.PARTY_KICK_CANT_KICK_YOURSELF));
					return true;
				}

				// Checks if the target is in the party
				if (!party.getMembers().contains(toKick.getUniqueId())) {
					StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_KICK_NOT_IN), "{player}", username));
					return true;
				}

				// Removes the target from the party
				partyData.removePlayerFromParty(party, toKick.getUniqueId());
				StringUtils.message(toKick, StringUtils.getMessage(Message.PARTY_KICK_KICKED));
				party.messageMembers(StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_KICK_PLAYER_KICKED), "{player}", username));
				return true;
			}
			case "list" -> {
				// Gets the player's current party
				Party party = partyData.getPartyFromPlayer(player.getUniqueId());
				if (party == null) {
					StringUtils.message(player, StringUtils.getMessage(Message.PARTY_NOT_IN));
					return true;
				}

				// Prints out a list of all the party members (including leader)
				StringUtils.message(player, StringUtils.getMessage(Message.PARTY_LIST_HEADER));
				StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_LIST_MEMBER), "{player}", userManager.getUsernameID(userManager.getID(party.getOwner()))));
				for (UUID member : party.getMembers()) {
					StringUtils.message(player, StringUtils.replaceAll(StringUtils.getMessage(Message.PARTY_LIST_MEMBER), "{player}", userManager.getUsernameID(userManager.getID(member))));
				}
				StringUtils.message(player, StringUtils.getMessage(Message.PARTY_LIST_FOOTER));
				return true;
			}
			default -> {
				// Prints out a list of all commands if the player used an invalid command
				StringUtils.message(player, StringUtils.getMessage(Message.PARTY_HELP));
				return true;
			}
		}
	}

}
