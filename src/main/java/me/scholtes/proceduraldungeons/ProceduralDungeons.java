package me.scholtes.proceduraldungeons;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class ProceduralDungeons extends JavaPlugin {

    private static final Random RANDOM = new Random();
    private static ProceduralDungeons instance = null;

    static Random getRandom() {
        return RANDOM;
    }

    static ProceduralDungeons getInstance() {
        return instance;
    }

    public void onEnable() {
    	saveDefaultConfig();
        instance = this;
        Dungeon dungeon = new Dungeon(this, "dungeon1");
        System.out.println("Generating dungeon1");
        dungeon.generateDungeon();
    }

}
