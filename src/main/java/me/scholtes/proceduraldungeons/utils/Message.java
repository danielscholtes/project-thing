package me.scholtes.proceduraldungeons.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum Message {
	
	NEED_TO_BE_PLAYER("&cYou need to be a player!", "commands.need_to_be_player"),
	NOT_LEADER("&cYou need to be the party leader!", "commands.dungeon.join.not_leader"),
	RELOAD_NO_PERMISSION("&cNo permission", "commands.reload.no_permission"),
	RELOAD_RELOADED("&aReloaded messages", "commands.reload.reloaded"),
	CHESTWAND_NO_PERMISSION("&cNo permission", "commands.chestwand.no_permission"),
	CHESTWAND_INCORRECT("&c/dungeon chestwand <tileset> <tile> <variation>", "commands.chestwand.incorrect"),
	CHESTWAND_TILESET_NOT_EXIST("&cThat tileset doesn't exist", "commands.chestwand.tileset_not_exist"),
	CHESTWAND_TILE_NOT_VALID("&cThat's not a valid tile", "commands.chestwand.tile_not_valid"),
	CHESTWAND_WAND_GIVEN("&aYou were given the wand!", "commands.chestwand.wand_given"),
	MOBWAND_NO_PERMISSION("&cNo permission", "commands.mobwand.no_permission"),
	MOBWAND_INCORRECT("&c/dungeon MOBWAND <tileset> <tile> <variation>", "commands.mobwand.incorrect"),
	MOBWAND_TILESET_NOT_EXIST("&cThat tileset doesn't exist", "commands.mobwand.tileset_not_exist"),
	MOBWAND_TILE_NOT_VALID("&cThat's not a valid tile", "commands.mobwand.tile_not_valid"),
	MOBWAND_WAND_GIVEN("&aYou were given the wand!", "commands.mobwand.wand_given"),
	DUNGEON_HELP("&c/dungeon join <dungeon-name>;&c/dungeon leave", "commands.dungeon.help"),
	DUNGEON_JOIN_INCORRECT("&cTo join a dungeon use /dungeon join <dungeon-name>", "commands.dungeon.join.incorrect"),
	DUNGEON_JOIN_NOT_EXIST("&cThat dungeon doesn't exist!", "commands.dungeon.join.not_exist"),
	DUNGEON_JOIN_NO_PERM("&cYou don't have permission for that dungeon", "commands.dungeon.join.no_permission"),
	DUNGEON_JOIN_ALREADY_IN("&cYou're already in a dungeon!", "commands.dungeon.join.already_in_dungeon"),
	DUNGEON_JOIN_GENERATING("&aGenerating dungeon...", "commands.dungeon.join.generating"),
	DUNGEON_JOIN_GENERATED("&aDungeon generated! Teleporting...", "commands.dungeon.join.generated"),
	DUNGEON_LEAVE_NOT_IN("&cYou aren't in a dungeon", "commands.dungeon.leave.not_in_dungeon"),
	DUNGEON_LEAVE_LEAVE("&aYou have left the dungeon", "commands.dungeon.leave.leave_dungeon"),
	PARTY_NOT_ONLINE("&c{player} is not online!", "commands.party.not_online"),
	PARTY_NOT_IN("&cYou aren't in a party!", "commands.party.not_in"),
	PARTY_DISBANDED("&aThe party has been disbanded!", "commands.party.disbanded"),
	PARTY_PLAYER_LEFT("&a{player} has left the party!", "commands.party.player_left"),
	PARTY_HELP("&a- /party invite <player>;&a- /party join <player>;&a- /party leave;&a- /party kick <player>;&a- /party list", "commands.party.help"),
	PARTY_INVITE_INCORRECT("&c/party invite <player>", "commands.party.invite.incorrect"),
	PARTY_INVITE_MAXIMUM("&cYou have the maximum amount of members in your party", "commands.party.invite.maximum"),
	PARTY_INVITE_ALREADY_IN("&c{player} is already in a party!", "commands.party.invite.already_in_party"),
	PARTY_INVITE_CANT_INVITE("&cYou can't invite someone to a party when you're in a dungeon!", "commands.party.invite.cant_invite"),
	PARTY_INVITE_IN_DUNGEON("&c{player} is already in a dungeon!", "commands.party.invite.in_dungeon"),
	PARTY_INVITE_ALREADY_INVITED("&cYou already invited {player} to your party!", "commands.party.invite.already_invited"),
	PARTY_INVITE_BEEN_INVITED("&aYou have been invited to {player}'s party!", "commands.party.invite.been_invited"),
	PARTY_INVITE_INVITE_SENT("&a{player} has been invited to the party!", "commands.party.invite.invite_sent"),
	PARTY_JOIN_ALREADY_IN("&cYou're already in a party!", "commands.party.join.already_in"),
	PARTY_JOIN_INCORRECT("&c/party join <player>", "commands.party.join.incorrect"),
	PARTY_JOIN_NOT_INVITED("&c{player} hasn't invited you to a party!", "commands.party.join.not_invited"),
	PARTY_JOIN_CANT_JOIN("&cYou can't join a party when you're in a dungeon!", "commands.party.join.cant_join"),
	PARTY_JOIN_IN_DUNGEON("&c{player} is already in a dungeon!", "commands.party.join.in_dungeon"),
	PARTY_JOIN_FULL("&c{player}'s party is full!", "commands.party.join.full"),
	PARTY_JOIN_JOIN_PARTY("&aYou have joined {player}'s party!", "commands.party.join.join_party"),
	PARTY_JOIN_PLAYER_JOINED("&a{player} has joined the party!", "commands.party.join.player_joined"),
	PARTY_LEAVE_LEAVE("&aYou have left the party", "commands.party.leave.leave"),
	PARTY_KICK_INCORRECT("&c/party kick <player>\"", "commands.party.kick.incorrect"),
	PARTY_KICK_CANT_KICK_YOURSELF("&cYou can't kick yourself", "commands.party.kick.cant_kick_yourself"),
	PARTY_KICK_NOT_IN("&c{player} isn't in your party!", "commands.party.kick.not_in_party"),
	PARTY_KICK_KICKED("&aYou have been kicked from the party", "commands.party.kick.kicked"),
	PARTY_KICK_PLAYER_KICKED("&a{player} has been kicked from the party!", "commands.party.kick.player_kicked"),
	PARTY_LIST_HEADER("&2----- Party List -----", "commands.party.list.header"),
	PARTY_LIST_MEMBER("&a{player}", "commands.party.list.member"),
	PARTY_LIST_FOOTER("&2--------------------", "commands.party.list.footer"),
	DUNGEON_COMPLETED("&aYou completed the dungeon! You will be teleported out in {seconds} seconds!", "dungeon.completed"),
	DUNGEON_LIVES_LEFT("&aYou have {lives} lives!", "dungeon.lives_left"),
	DUNGEON_PLAYER_LEAVE("&aYou have lost {lives} lives because {player} has left the party!", "dungeon.player_leave"),
	DUNGEON_PLAYER_DIE_PARTY("&a{player} has died!", "dungeon.player_die_party"),
	DUNGEON_PLAYER_DIE("&aYou died!", "dungeon.player_die"),
	DUNGEON_LOST_ALL_LIVES_PARTY("&aYour party has lost all its lives! You will be teleported out in {seconds} seconds!", "dungeon.lost_all_lives_party"),
	DUNGEON_LOST_ALL_LIVES("&aYou have lost all your lives! You will be teleported out in {seconds} seconds!", "dungeon.lost_all_lives"),
	DUNGEON_CANT_USE_COMMAND("&cYou can't use this command in a dungeon!", "dungeon.cant_use_command");
	
	private final List<String> defaultMessage;
	private final String path;
	
	/**
	 * Constructor for the {@link Message}
	 *
	 * @param message The message
	 * @param path The path
	 */
	Message(String message, String path) {
		this.defaultMessage = new ArrayList<>();
		Collections.addAll(this.defaultMessage, message.split(";"));
		this.path = path;
	}
	
	/**
	 * Gets the {@link List<String>} associated to this {@link Message}
	 * 
	 * @return {@link List<String>} associated to this {@link Message}
	 */
	public List<String> getDefaultMessage() {
		return defaultMessage;
	}
	
	/**
	 * Gets the path associated to this {@link Message}
	 * 
	 * @return Path associated to this {@link Message}
	 */
	public String getPath() {
		return path;
	}
	
}
