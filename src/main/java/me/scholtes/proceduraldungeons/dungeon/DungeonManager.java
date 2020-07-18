package me.scholtes.proceduraldungeons.dungeon;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class DungeonManager {

	private List<Dungeon> dungeons = new ArrayList<Dungeon>();
	
	public void joinDungeon(Player player) {
		
	}
	
	public List<Dungeon> getDungeons() {
		return dungeons;
	}
	
}
