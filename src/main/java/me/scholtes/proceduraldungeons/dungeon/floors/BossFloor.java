package me.scholtes.proceduraldungeons.dungeon.floors;

import java.util.ArrayList;
import java.util.List;

import me.scholtes.proceduraldungeons.dungeon.AbstractMob;
import org.bukkit.Bukkit;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.Boss;
import me.scholtes.proceduraldungeons.dungeon.DungeonInfo;
import me.scholtes.proceduraldungeons.dungeon.DungeonManager;

public class BossFloor extends AbstractFloorInfo{

	private final List<Boss> bosses;
	
	/**
	 * Constructor for the {@link BossFloor}
	 * 
	 * @param dungeonInfo The instance of the {@link DungeonInfo} this {@link BossFloor} belongs to
	 * @param dungeonManager The instance of the {@link DungeonManager}
	 * @param floor The floor number
	 */
	public BossFloor(DungeonInfo dungeonInfo, DungeonManager dungeonManager, String floor) {
		super(dungeonInfo, dungeonManager, floor);
		bosses = new ArrayList<>();
		
		// Loads in all the information about this BossFloor
		Bukkit.getScheduler().runTaskAsynchronously(ProceduralDungeons.getInstance(), () -> {
			
			for (String boss : dungeonInfo.getConfig().getStringList("floors.boss.bosses")) {
				bosses.add(new Boss(boss));
			}
		});
	}

	/**
	 * Gets a {@link List<AbstractMob>} of all the bosses this {@link BossFloor} has
	 * 
	 * @return A {@link List<AbstractMob>} of all the bosses
	 */
	public List<Boss> getBosses() {
		return bosses;
	}

}