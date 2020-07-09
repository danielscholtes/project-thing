package me.scholtes.proceduraldungeons;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.SplittableRandom;

public final class ProceduralDungeons extends JavaPlugin {

    private static final SplittableRandom RANDOM = new SplittableRandom();
    private static ProceduralDungeons instance = null;

    static SplittableRandom getRandom() {
        return RANDOM;
    }

    static ProceduralDungeons getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        Dungeon dungeon = new Dungeon(this);
        new BukkitRunnable() {
            @Override
            public void run() {
                dungeon.generateDungeon();
            }
        }.runTaskLater(this, 10L);
    }

}
