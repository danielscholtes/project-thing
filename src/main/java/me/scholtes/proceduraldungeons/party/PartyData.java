package me.scholtes.proceduraldungeons.party;

import java.util.*;

import me.scholtes.proceduraldungeons.dungeon.manager.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.Dungeon;
import me.scholtes.proceduraldungeons.utils.StringUtils;
import me.scholtes.proceduraldungeons.utils.Message;

public class PartyData {
	
	private final Map<UUID, Party> parties;
	private final Map<UUID, List<Party>> invitations;
	private final Map<UUID, List<Integer>> invitationTasks;
	private final UserManager userManager;

	public PartyData(UserManager userManager) {
		this.parties = new HashMap<>();
		this.invitations = new HashMap<>();
		this.invitationTasks = new HashMap<>();
		this.userManager = userManager;
	}

	/**
	 * Gets the {@link Party} from the specified player {@link UUID}
	 * 
	 * @param uuid Player {@link UUID}
	 * @return The {@link Party} if there is one, otherwise {@link null}
	 */
	public Party getPartyFromPlayer(UUID uuid) {
		if (parties.isEmpty() || !parties.containsKey(uuid)) {
			return null;
		}
		return parties.get(uuid);
	}
	
	/**
	 * Creates a {@link Party} with the specified owner and returns it
	 * 
	 * @param owner The {@link UUID} of the party owner
	 * @return The created {@link Party}
	 */
	public Party createParty(UUID owner) {
		Party party = new Party(owner);
		parties.put(owner, party);
		return party;
	}

	/**
	 * Adds the specified player to the specified {@link Party}
	 * 
	 * @param party The {@link Party} to add the player to
	 * @param player The {@link UUID} of the player to add
	 */
	public void addPlayerToParty(Party party, UUID player) {
		parties.put(player, party);
		party.getMembers().add(player);
		invitations.remove(player);
		if (invitationTasks.containsKey(player)) {
			for (int task : invitationTasks.get(player)) {
				Bukkit.getScheduler().cancelTask(task);
			}
			invitationTasks.remove(player);
		}
	}
	
	/**
	 * Removes the specified player from the {@link Party}
	 * 
	 * @param party The {@link Party}
	 * @param playerUUID The player to remove's {@link UUID}
	 */
	public void removePlayerFromParty(Party party, UUID playerUUID) {
		if (party == null) {
			return;
		}
		Dungeon dungeon = ProceduralDungeons.getInstance().getDungeonManager().getDungeonFromPlayer(party.getOwner(), party);
		if (dungeon != null) {
			if (playerUUID.equals(party.getOwner())) {
				ProceduralDungeons.getInstance().getDungeonManager().getDungeons().remove(playerUUID);
				ProceduralDungeons.getInstance().getDungeonManager().getDungeons().put(party.getMembers().get(0), dungeon);
				dungeon.setDungeonOwner(party.getMembers().get(0));
			}

			if (dungeon.getTotalLives() > dungeon.getDungeonInfo().getLivesPerPlayer()) {
				dungeon.setTotalLives(dungeon.getTotalLives() - 3);
				List<String> message = StringUtils.replaceAll(StringUtils.getMessage(Message.DUNGEON_PLAYER_LEAVE), "{player}", userManager.getUsernameID(userManager.getID(playerUUID)));
				List<String> newMessage = StringUtils.replaceAll(message, "{lives}", String.valueOf(dungeon.getTotalLives()));
				for (UUID uuid : party.getMembers()) {
					if (uuid.equals(playerUUID)) {
						continue;
					}
					StringUtils.message(Bukkit.getPlayer(uuid), newMessage);
					StringUtils.message(Bukkit.getPlayer(uuid), StringUtils.replaceAll(StringUtils.getMessage(Message.DUNGEON_LIVES_LEFT), "{lives}", String.valueOf(dungeon.getTotalLives())));
				}
				if (!playerUUID.equals(party.getOwner())) {
					StringUtils.message(Bukkit.getPlayer(party.getOwner()), newMessage);
					StringUtils.message(Bukkit.getPlayer(party.getOwner()), StringUtils.replaceAll(StringUtils.getMessage(Message.DUNGEON_LIVES_LEFT), "{lives}", String.valueOf(dungeon.getTotalLives())));
				}
			}
			Bukkit.getPlayer(playerUUID).teleport(dungeon.getDungeonInfo().getFinishLocation());
			Bukkit.getPlayer(playerUUID).setGameMode(dungeon.getDungeonInfo().getLeaveGameMode());
		}


		if (party.getMembers().size() > 0) {
			if (playerUUID.equals(party.getOwner())) {
				party.setOwner(party.getMembers().get(0));
				party.getMembers().remove(0);
			} else {
				party.getMembers().remove(playerUUID);
			}
		}

		parties.remove(playerUUID);

		if (party.getMembers().size() == 0) {
			disbandParty(party);
		}
	}

	public void disbandParty(Party party) {
		parties.remove(party.getOwner());
		for (UUID uuid : party.getMembers()) {
			parties.remove(uuid);
		}
		party.messageMembers(StringUtils.getMessage(Message.PARTY_DISBANDED));
	}
	
	/**
	 * Gets a {@link Map<UUID, List<Party>>} of all the {@link Party} invitations of all
	 * the players
	 * 
	 * @return {@link Map<UUID, List<Party>>} of all the {@link Party} invitations
	 */
	public Map<UUID, List<Party>> getInvitations() {
		return invitations;
	}
	
	/**
	 * Sends a {@link Party} invitation to the specified player
	 * 
	 * @param party The {@link Party} to invite the player to
	 * @param toSend The player to send the invitation to's {@link UUID}
	 */
	public void sendInvitation(Party party, UUID toSend) {
		if (invitations.containsKey(toSend)) {
			invitations.get(toSend).add(party);
		} else {
			List<Party> parties = new ArrayList<>();
			parties.add(party);
			invitations.put(toSend, parties);
		}
		
		int taskID = new BukkitRunnable() {
			@Override
			public void run() {
				if (invitations.containsKey(toSend) && invitations.get(toSend).contains(party)) {
					invitations.get(toSend).remove(party);
					if (invitations.get(toSend).isEmpty()) {
						invitations.remove(toSend);
					}
				}
				
				int id = this.getTaskId();
				if (invitationTasks.containsKey(toSend) && invitationTasks.get(toSend).contains(id)) {
					invitationTasks.get(toSend).remove((Integer) id);
					if (invitationTasks.get(toSend).isEmpty()) {
						invitationTasks.remove(toSend);
					}
				}
			}
		}.runTaskLaterAsynchronously(ProceduralDungeons.getInstance(), 20L * 60 * 2).getTaskId();

		if (invitationTasks.containsKey(toSend)) {
			invitationTasks.get(toSend).add(taskID);
		} else {
			List<Integer> tasks = new ArrayList<>();
			tasks.add(taskID);
			invitationTasks.put(toSend, tasks);
		}
	}

}
