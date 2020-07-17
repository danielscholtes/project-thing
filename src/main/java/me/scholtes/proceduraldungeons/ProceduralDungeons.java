package me.scholtes.proceduraldungeons;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.world.SlimeWorld;

import java.io.IOException;
import java.util.Random;

public final class ProceduralDungeons extends JavaPlugin {

	private static final Random RANDOM = new Random();
	private static ProceduralDungeons instance = null;
	private DungeonManager dungeonManager = null;

	public void onEnable() {
		saveDefaultConfig();
		instance = this;

		if (getSlimePlugin() == null) {
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		Dungeon dungeon = new Dungeon(this, "dungeon1");
		System.out.println("Generating dungeon1");
		dungeon.generateDungeon();
	}

	public void onDisable() {
    	if (getSlimePlugin() == null) {
    		return;
    	}
    	
    	for (SlimeWorld world : getDungeonManager().getDungeonWorlds().values()) {
    		try {
    			Bukkit.unloadWorld(world.getName(), false);
				getSlimePlugin().getLoader("file").deleteWorld(world.getName());
			} catch (UnknownWorldException | IOException e) {
				e.printStackTrace();
			}
    	}
    	
    	getDungeonManager().getDungeonWorlds().clear();
    }

	public SlimePlugin getSlimePlugin() {
		return (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
	}

	public DungeonManager getDungeonManager() {
		if (dungeonManager == null) {
			dungeonManager = new DungeonManager();
		}
		return dungeonManager;
	}

	static Random getRandom() {
		return RANDOM;
	}

	static ProceduralDungeons getInstance() {
		return instance;
	}
}
