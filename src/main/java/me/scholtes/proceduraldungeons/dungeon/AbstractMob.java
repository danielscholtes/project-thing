package me.scholtes.proceduraldungeons.dungeon;

public abstract class AbstractMob {

	private final String name;
	
	public AbstractMob(final String name) {
		this.name = name;
	}

	/**
	 * Gets the name of the {@link AbstractMob}
	 * 
	 * @return Name of the {@link AbstractMob}
	 */
	public String getName() {
		return name;
	}
	
}
