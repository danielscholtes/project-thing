package me.scholtes.proceduraldungeons.dungeon.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.Dungeon;
import me.scholtes.proceduraldungeons.dungeon.DungeonManager;
import me.scholtes.proceduraldungeons.party.Party;
import me.scholtes.proceduraldungeons.party.PartyData;
import me.scholtes.proceduraldungeons.utils.StringUtils;
import me.scholtes.proceduraldungeons.utils.Message;

public class PlayerListeners implements Listener {
	
	private final DungeonManager dungeonManager;
	private final PartyData partyData;
	
	/**
	 * Constructor for the {@link PlayerListeners}
	 * 
	 * @param dungeonManager The instance of the {@link DungeonManager}
	 * @param partyData The instance of the {@link PartyData}
	 */
	public PlayerListeners(DungeonManager dungeonManager, PartyData partyData) {
		this.dungeonManager = dungeonManager;
		this.partyData = partyData;
	}
	
	/**
	 * Prevents players from breaking disallowed blocks in dungeons
	 * 
	 * @param event The {@link BlockBreakEvent}
	 */
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Dungeon dungeon = dungeonManager.getDungeonFromPlayer(player.getUniqueId(), partyData.getPartyFromPlayer(player.getUniqueId()));
		if (dungeon == null || player.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		
		if (!dungeon.getDungeonInfo().getCanBreakBlocks().contains(block.getType())) {
			event.setCancelled(true);
		}
	}

	/**
	 * Prevents players from placing blocks in dungeons
	 * 
	 * @param event The {@link BlockPlaceEvent}
	 */
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Dungeon dungeon = dungeonManager.getDungeonFromPlayer(player.getUniqueId(), partyData.getPartyFromPlayer(player.getUniqueId()));
		if (dungeon == null || player.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		
		event.setBuild(false);
		event.setCancelled(true);
	}

	/**
	 * Prevents players from PvPing in dungeons
	 * 
	 * @param event The {@link EntityDamageByEntityEvent}
	 */
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		Player attacker = null;
		
		if (event.getDamager() instanceof Player) {
			attacker = (Player) event.getDamager();
		}
		if ((event.getDamager() instanceof Projectile) && (((Projectile) event.getDamager()).getShooter() instanceof Player)) {
			attacker = (Player) ((Projectile) event.getDamager()).getShooter();
		}
		
		if (attacker == null) {
			return;
		}
		
		Player victim = (Player) event.getEntity();
		
		Dungeon dungeonAttacker = dungeonManager.getDungeonFromPlayer(attacker.getUniqueId(), partyData.getPartyFromPlayer(attacker.getUniqueId()));
		Dungeon dungeonVictim = dungeonManager.getDungeonFromPlayer(victim.getUniqueId(), partyData.getPartyFromPlayer(victim.getUniqueId()));
		if ((dungeonVictim == null && dungeonAttacker == null) || attacker.getGameMode() == GameMode.CREATIVE || victim.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		
		event.setCancelled(true);
	}

	/**
	 * Handles players dying in dungeons
	 * 
	 * @param event The {@link EntityDamageEvent}
	 */
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDeath(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		Player victim = (Player) event.getEntity();
		
		Dungeon dungeon = dungeonManager.getDungeonFromPlayer(victim.getUniqueId(), partyData.getPartyFromPlayer(victim.getUniqueId()));
		if (dungeon == null) {
			return;
		}
		
		if (event.getFinalDamage() < victim.getHealth()) {
			return;
		}
		
		victim.setFoodLevel(20);
		victim.setHealth(victim.getMaxHealth());
		victim.teleport(dungeon.getSpawnPoint());
		event.setCancelled(true);

		Party party = partyData.getPartyFromPlayer(dungeon.getDungeonOwner());
		if (party != null) {
			party.messageMembers(StringUtils.replaceAll(StringUtils.getMessage(Message.DUNGEON_PLAYER_DIE_PARTY), "{player}", victim.getName()));
		} else {
			StringUtils.message(victim, StringUtils.getMessage(Message.DUNGEON_PLAYER_DIE));
		}
		
		dungeon.setTotalLives(dungeon.getTotalLives() - 1);
		if (dungeon.getTotalLives() > 0) {
			if (party != null) {
				party.messageMembers(StringUtils.replaceAll(StringUtils.getMessage(Message.DUNGEON_LIVES_LEFT), "{lives}",  String.valueOf(dungeon.getTotalLives())));
			} else {
				StringUtils.message(victim, StringUtils.replaceAll(StringUtils.getMessage(Message.DUNGEON_LIVES_LEFT), "{lives}", String.valueOf(dungeon.getTotalLives())));
			}
			return;
		}
		
		if (party != null) {
			party.messageMembers(StringUtils.replaceAll(StringUtils.getMessage(Message.DUNGEON_LOST_ALL_LIVES_PARTY), "{seconds}", String.valueOf(dungeon.getDungeonInfo().getTeleportNoLivesDelay())));
		} else {
			StringUtils.message(victim, StringUtils.replaceAll(StringUtils.getMessage(Message.DUNGEON_LOST_ALL_LIVES), "{seconds}", String.valueOf(dungeon.getDungeonInfo().getTeleportNoLivesDelay())));
		}
		
		Bukkit.getScheduler().runTaskLater(ProceduralDungeons.getInstance(), () -> dungeonManager.removeDungeon(dungeon), 20L * dungeon.getDungeonInfo().getTeleportNoLivesDelay());
		
	}

	/**
	 * Prevents players running disallowed commands in dungeons
	 * 
	 * @param event The {@link PlayerCommandPreprocessEvent}
	 */
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1).split(" ")[0];
		Dungeon dungeon = dungeonManager.getDungeonFromPlayer(player.getUniqueId(), partyData.getPartyFromPlayer(player.getUniqueId()));
		if (dungeon == null || player.getGameMode() == GameMode.CREATIVE || player.isOp()) {
			return;
		}
		
		for (String allowed : dungeon.getDungeonInfo().getAllowedCommands()) {
			if (allowed.startsWith(command)) {
				return;
			}
		}
		
		StringUtils.message(player, StringUtils.getMessage(Message.DUNGEON_CANT_USE_COMMAND));
		event.setCancelled(true);
	}

	/**
	 * Handles players leaving when in dungeons
	 * 
	 * @param event The {@link PlayerQuitEvent}
	 */
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		partyData.getInvitations().remove(player.getUniqueId());

		Party party = partyData.getPartyFromPlayer(player.getUniqueId());

		if (party == null || party.getMembers().size() == 0) {
			Dungeon dungeon = dungeonManager.getDungeonFromPlayer(player.getUniqueId(), party);
			if (dungeon == null) {
				return;
			}
			dungeonManager.removeDungeon(dungeon);
			return;
		}

		partyData.removePlayerFromParty(party, player.getUniqueId());
	}

	/**
	 * Handles players getting kicked when in dungeons
	 * 
	 * @param event The {@link PlayerKickEvent}
	 */
	@EventHandler
	public void onKick(PlayerKickEvent event) {
		Player player = event.getPlayer();

		partyData.getInvitations().remove(player.getUniqueId());

		Party party = partyData.getPartyFromPlayer(player.getUniqueId());

		if (party == null || party.getMembers().size() == 0) {
			Dungeon dungeon = dungeonManager.getDungeonFromPlayer(player.getUniqueId(), party);
			if (dungeon == null) {
				return;
			}
			dungeonManager.removeDungeon(dungeon);
			return;
		}

		partyData.removePlayerFromParty(party, player.getUniqueId());
	}

}
