package me.scholtes.proceduraldungeons.dungeon.tilesets;

import java.util.List;

import org.bukkit.Location;

import me.scholtes.proceduraldungeons.dungeon.rooms.RoomType;

public class TileVariation {
	
	private List<Location> chestLocations;
	private List<Location> mobLocations;
	
	/**
	 * Constructor for the {@link TileVariation}
	 * 
	 * @param tileSet The instance of the {@link TileSet} this {@link TileVariation} belongs to
	 * @param variation The name of this {@link TileVariation}
	 * @param roomType The {@link RoomType} of this {@link TileVariation}
	 */
	public TileVariation(TileSet tileSet, String variation, RoomType roomType) {
		
	}

	/**
	 * Gets a {@link List<Location>} of all possible chest {@link Location}s
	 * 
	 * @return A {@link List<Location>} of all possible chest {@link Location}s
	 */
	public List<Location> getChestLocations() {
		return chestLocations;
	}

	/**
	 * Gets a {@link List<Location>} of all possible mob {@link Location}s
	 * 
	 * @return A {@link List<Location>} of all possible mob {@link Location}s
	 */
	public List<Location> getMobLocations() {
		return mobLocations;
	}
	
}
