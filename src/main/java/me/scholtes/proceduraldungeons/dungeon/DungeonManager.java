package me.scholtes.proceduraldungeons.dungeon;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import me.scholtes.proceduraldungeons.ProceduralDungeons;

public class DungeonManager {

	private Map<UUID, Dungeon> dungeons = new ConcurrentHashMap<UUID, Dungeon>();
	
	/**
	 * Makes the {@link Player} join a new instance of a {@link Dungeon}
	 * 
	 * @param player The player to join (party leader)
	 * @param dungeonName The name of the dungeon
	 */
	public void joinDungeon(Player player, String dungeonName) {
		Dungeon dungeon = new Dungeon(ProceduralDungeons.getInstance(), dungeonName, player.getUniqueId());
		dungeons.put(player.getUniqueId(), dungeon);  
		dungeon.generateDungeon();	
	}
	
	/**
	 * Gets a {@link Map<UUID, Dungeon>} with all the solo players and/or party leaders 
	 * currently in a dungeon
	 * 
	 * @return A {@link Map<UUID, Dungeon>} with all players in a dungeon
	 */
	public Map<UUID, Dungeon> getDungeons() {
		return dungeons;
	}
	
}
