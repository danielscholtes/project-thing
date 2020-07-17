package me.scholtes.proceduraldungeons;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.scholtes.proceduraldungeons.dungeon.Dungeon;
import me.scholtes.proceduraldungeons.dungeon.DungeonManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

//import com.grinderwolf.swm.api.SlimePlugin;
//import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
//import com.grinderwolf.swm.api.world.SlimeWorld;

import java.util.Random;
import java.util.stream.Stream;

public final class ProceduralDungeons extends JavaPlugin {

	private static final Random RANDOM = new Random();
	private static ProceduralDungeons instance = null;
	private DungeonManager dungeonManager = null;

	public void onEnable() {
		saveDefaultConfig();
		instance = this;

		/*if (getSlimePlugin() == null) {
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}*/

		Dungeon dungeon = new Dungeon(this, "dungeon1");
		getDungeonManager().getDungeons().add(dungeon);
		System.out.println("Generating dungeon1");
		dungeon.generateDungeon();
	}

	public void onDisable() {
    	/*if (getSlimePlugin() == null) {
    		return;
    	}*/
    	
    	for (Dungeon dungeon : getDungeonManager().getDungeons()) {
    		if (dungeon.getWorld() == null) {
    			continue;
    		}
    		
    		for (Player p : getServer().getWorlds().get(0).getPlayers()) {
    			p.teleport(getServer().getWorlds().get(0).getSpawnLocation());
    		}
            
			getServer().unloadWorld(dungeon.getWorld(), false);
			
			try (Stream<Path> files = Files.walk(dungeon.getWorld().getWorldFolder().toPath())) {
	            files.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
	        } catch (IOException e) {
				e.printStackTrace();
			}
			

    		/*try {
    			Bukkit.unloadWorld(dungeon.getWorld().getName(), false);
				getSlimePlugin().getLoader("file").deleteWorld(dungeon.getWorld().getName());
			} catch (UnknownWorldException | IOException e) {
				e.printStackTrace();
			}*/ 
    	}
    	
    	getDungeonManager().getDungeons().clear();
    }

	/*public SlimePlugin getSlimePlugin() {
		return (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
	}*/

	public DungeonManager getDungeonManager() {
		if (dungeonManager == null) {
			dungeonManager = new DungeonManager();
		}
		return dungeonManager;
	}

	public static Random getRandom() {
		return RANDOM;
	}

	public static ProceduralDungeons getInstance() {
		return instance;
	}
}
