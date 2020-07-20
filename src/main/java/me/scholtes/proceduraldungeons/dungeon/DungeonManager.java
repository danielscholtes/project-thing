package me.scholtes.proceduraldungeons.dungeon;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import me.scholtes.proceduraldungeons.ProceduralDungeons;

public class DungeonManager {

	private Map<UUID, Dungeon> dungeons = new ConcurrentHashMap<UUID, Dungeon>();
	
	public void joinDungeon(Player player, String dungeonName) {
		
		Dungeon dungeon = new Dungeon(ProceduralDungeons.getInstance(), dungeonName, player.getUniqueId());
		dungeons.put(player.getUniqueId(), dungeon);  
		dungeon.generateDungeon();
		
	}
	
	public Map<UUID, Dungeon> getDungeons() {
		return dungeons;
	}
	
}
