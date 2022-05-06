package me.scholtes.proceduraldungeons.dungeon;

public class Mob extends AbstractMob {

	private final double chance;
	private final int minMobs;
	private final int maxMobs;
	
	public Mob(final String name, final double chance, final int minMobs, final int maxMobs) {
		super(name);
		this.chance = chance;
		this.minMobs = minMobs;
		this.maxMobs = maxMobs;
	}

	/**
	 * Gets the spawn chance of the {@link Mob}
	 * 
	 * @return Spawn chance of the {@link Mob}
	 */
	public double getChance() {
		return chance;
	}

	/**
	 * Gets the minimum amount that can spawn of the {@link Mob}
	 * 
	 * @return Minimum amount that can spawn of the {@link Mob}
	 */
	public int getMinMobs() {
		return minMobs;
	}


	/**
	 * Gets the maximum amount that can spawn of the {@link Mob}
	 * 
	 * @return Maximum amount that can spawn of the {@link Mob}
	 */
	public int getMaxMobs() {
		return maxMobs;
	}
	
}
