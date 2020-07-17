package me.scholtes.proceduraldungeons;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.grinderwolf.swm.api.world.SlimeWorld;

public class DungeonManager {

	private Map<Dungeon, SlimeWorld> dungeonWorlds = new ConcurrentHashMap<Dungeon, SlimeWorld>();
	
	public Map<Dungeon, SlimeWorld> getDungeonWorlds() {
		return dungeonWorlds;
	}
	
}
