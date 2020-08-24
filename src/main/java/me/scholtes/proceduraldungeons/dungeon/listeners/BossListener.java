package me.scholtes.proceduraldungeons.dungeon.listeners;

import java.util.UUID;

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
import me.scholtes.proceduraldungeons.utils.ChatUtils;
import me.scholtes.proceduraldungeons.utils.Message;

public class BossListener implements Listener {

	private final DungeonManager dungeonManager;
	private final PartyData partyData;
	private final ProceduralDungeons plugin;

	/**
	 * Constructor for the {@link PlayerListeners}
	 * 
	 * @param plugin The instance of {@link ProceduralDungeons}
	 * @param dungeonManager The instance of the {@link DungeonManager}
	 * @param partyData The instance of the {@link PartyData}
	 */
	public BossListener(ProceduralDungeons plugin, DungeonManager dungeonManager, PartyData partyData) {
		this.plugin = plugin;
		this.dungeonManager = dungeonManager;
		this.partyData = partyData;
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
		if (event.getKiller() != null && event.getKiller() instanceof Player && dungeonManager.getDungeonFromPlayer(((Player) event.getKiller()).getUniqueId(), partyData.getPartyFromPlayer(((Player) event.getKiller()).getUniqueId())) != null) {
			Player player = (Player) event.getKiller();
			dungeon = dungeonManager.getDungeonFromPlayer(player.getUniqueId(), partyData.getPartyFromPlayer(player.getUniqueId()));
		} else {
			dungeon = dungeonManager.getDungeonFromID(UUID.fromString(event.getEntity().getWorld().getName().replaceAll("Dungeon-", "")));
		}
		if (dungeon == null) {
			return;
		}
		
		if (dungeon.getBossID().equals(event.getMob().getUniqueId())) {
			Party party = partyData.getPartyFromPlayer(dungeon.getPlayer());
			if (party != null) {
				party.messageMembers(ChatUtils.replaceAll(ChatUtils.getMessage(Message.DUNGEON_COMPLETED), "{seconds}", String.valueOf(dungeon.getDungeonInfo().getTeleportCompleteDelay())));
			} else {
				ChatUtils.message(Bukkit.getPlayer(dungeon.getPlayer()), ChatUtils.replaceAll(ChatUtils.getMessage(Message.DUNGEON_COMPLETED), "{seconds}", String.valueOf(dungeon.getDungeonInfo().getTeleportCompleteDelay())));
			}
			
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				dungeonManager.removeDungeon(dungeon);
			}, 20L * dungeon.getDungeonInfo().getTeleportCompleteDelay());
		}
	}
	
}
