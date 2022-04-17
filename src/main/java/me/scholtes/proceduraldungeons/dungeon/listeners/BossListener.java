package me.scholtes.proceduraldungeons.dungeon.listeners;

import java.util.UUID;

import me.scholtes.proceduraldungeons.dungeon.Boss;
import me.scholtes.proceduraldungeons.dungeon.manager.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.Dungeon;
import me.scholtes.proceduraldungeons.dungeon.DungeonManager;
import me.scholtes.proceduraldungeons.party.Party;
import me.scholtes.proceduraldungeons.party.PartyData;
import me.scholtes.proceduraldungeons.utils.StringUtils;
import me.scholtes.proceduraldungeons.utils.Message;

public class BossListener implements Listener {

	private final ProceduralDungeons plugin;

	/**
	 * Constructor for the {@link PlayerListeners}
	 * 
	 * @param plugin The instance of {@link ProceduralDungeons}
	 */
	public BossListener(ProceduralDungeons plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Handles players killing the {@link Boss} in dungeons
	 * 
	 * @param event The {@link MythicMobDeathEvent}
	 */
	@EventHandler
	public void onDeath(MythicMobDeathEvent event) {
		if ((event.getKiller() == null || !(event.getKiller() instanceof Player)) && !event.getEntity().getWorld().getName().startsWith("Dungeon-")) {
			return;
		}
		
		final Dungeon dungeon;
		if (event.getKiller() != null && event.getKiller() instanceof Player && plugin.getDungeonManager().getDungeonFromPlayer(event.getKiller().getUniqueId(),
				plugin.getPartyData().getPartyFromPlayer(event.getKiller().getUniqueId())) != null) {
			Player player = (Player) event.getKiller();
			dungeon = plugin.getDungeonManager().getDungeonFromPlayer(player.getUniqueId(), plugin.getPartyData().getPartyFromPlayer(player.getUniqueId()));
		} else {
			dungeon = plugin.getDungeonManager().getDungeonFromID(UUID.fromString(event.getEntity().getWorld().getName().replaceAll("Dungeon-", "")));
		}
		if (dungeon == null) {
			return;
		}
		
		if (dungeon.getBossID().equals(event.getMob().getUniqueId())) {
			Party party = plugin.getPartyData().getPartyFromPlayer(dungeon.getDungeonOwner());
			if (party != null) {
				plugin.getUserManager().incrementGamesWon(plugin.getUserManager().getID(party.getOwner()));
				for (UUID member : party.getMembers()) {
					plugin.getUserManager().incrementGamesWon(plugin.getUserManager().getID(member));
				}
				party.messageMembers(StringUtils.replaceAll(StringUtils.getMessage(Message.DUNGEON_COMPLETED), "{seconds}", String.valueOf(dungeon.getDungeonInfo().getTeleportCompleteDelay())));
			} else {
				plugin.getUserManager().incrementGamesWon(plugin.getUserManager().getID(dungeon.getDungeonOwner()));
				StringUtils.message(Bukkit.getPlayer(dungeon.getDungeonOwner()), StringUtils.replaceAll(StringUtils.getMessage(Message.DUNGEON_COMPLETED), "{seconds}", String.valueOf(dungeon.getDungeonInfo().getTeleportCompleteDelay())));
			}
			
			Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getDungeonManager().removeDungeon(dungeon), 20L * dungeon.getDungeonInfo().getTeleportCompleteDelay());
		}
	}
	
}
