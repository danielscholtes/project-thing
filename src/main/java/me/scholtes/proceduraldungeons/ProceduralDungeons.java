package me.scholtes.proceduraldungeons;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ProceduralDungeons extends JavaPlugin {

	private static Random random = new Random();
	private static ProceduralDungeons instance = null;
	
	public void onEnable() {
		instance = this;
		Dungeon dungeon = new Dungeon(this);
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			@Override
			public void run() {
				dungeon.generateDungeon();
			}
		}, 10L);
		
	}
	
	public static Random getRandom() {
		return random;
	}
	
	public static ProceduralDungeons getInstance() {
		return instance;
	}

}
