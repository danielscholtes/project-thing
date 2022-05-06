package me.scholtes.proceduraldungeons.generator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.List;
import java.util.Random;

public class VoidGenerator extends ChunkGenerator {


	/*
	 * IDK i found this on google because I couldn't be bothered to make it
	 */
	
	public List<BlockPopulator> getDefaultPopulators(World world) {
		return List.of();
	}
	
	public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
		return createChunkData(world);
	}

	public boolean canSpawn(World world, int x, int z) {
		return true;
	}

	public byte[] generate(World world, Random rand, int chunkx, int chunkz) {
		return new byte[32768];
	}

	public Location getFixedSpawnLocation(World world, Random random) {
		return new Location(world, 0, 128, 0);
	}

}
