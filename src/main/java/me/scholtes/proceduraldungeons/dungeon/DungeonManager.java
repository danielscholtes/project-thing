package me.scholtes.proceduraldungeons.dungeon;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.tilesets.TileSet;

public class DungeonManager {

	private Map<UUID, Dungeon> dungeons = new ConcurrentHashMap<UUID, Dungeon>();
	private Map<String, DungeonInfo> dungeonInfo = new ConcurrentHashMap<String, DungeonInfo>();
	private Map<String, TileSet> tileSets = new ConcurrentHashMap<String, TileSet>();
	
	public void loadDungeonInfo() {
		Bukkit.getScheduler().runTaskAsynchronously(ProceduralDungeons.getInstance(), new Runnable() {
			@Override
			public void run() {
				dungeonInfo.clear();
				
				for (String dungeon : ProceduralDungeons.getInstance().getConfig().getConfigurationSection("dungeons").getKeys(false)) {
					dungeonInfo.put(dungeon, new DungeonInfo(dungeon, getInstance()));
				}
			}
		});
	}
	public void loadTileSets() {
		Bukkit.getScheduler().runTaskAsynchronously(ProceduralDungeons.getInstance(), new Runnable() {
			@Override
			public void run() {
				tileSets.clear();
				
				for (String tileSet : ProceduralDungeons.getInstance().getConfig().getConfigurationSection("tile_sets").getKeys(false)) {
					tileSets.put(tileSet, new TileSet(tileSet));
				}
			}
		});
	}
	
	/**
	 * Makes the {@link Player} join a new instance of a {@link Dungeon}
	 * 
	 * @param player The player to join (party leader)
	 * @param dungeonName The name of the dungeon
	 */
	public void joinDungeon(Player player, String dungeonName) {
		Dungeon dungeon = new Dungeon(ProceduralDungeons.getInstance(), getDungeonInfo(dungeonName), player.getUniqueId());
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
	
	public DungeonInfo getDungeonInfo(String dungeonName) {
		return dungeonInfo.get(dungeonName);
	}
	
	public TileSet getTileSet(String tileSet) {
		return tileSets.get(tileSet);
	}
	
	private DungeonManager getInstance() {
		return this;
	}
	
}
