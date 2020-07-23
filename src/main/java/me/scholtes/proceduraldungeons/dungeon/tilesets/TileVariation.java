package me.scholtes.proceduraldungeons.dungeon.tilesets;

import java.util.List;

import org.bukkit.Location;

import me.scholtes.proceduraldungeons.dungeon.rooms.RoomType;

public class TileVariation {
	
	private List<Location> chestLocations;
	private List<Location> mobLocations;
	
	public TileVariation(TileSet tileSet, String variation, RoomType roomType) {
		
	}

	public List<Location> getChestLocations() {
		return chestLocations;
	}

	public List<Location> getMobLocations() {
		return mobLocations;
	}
	
}
