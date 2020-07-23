package me.scholtes.proceduraldungeons.dungeon.tilesets;

import java.util.List;
import java.util.Map;

import me.scholtes.proceduraldungeons.dungeon.rooms.RoomType;

public class TileSet {
	
	private Map<RoomType, List<TileVariation>> tileVariations;
	private final String tileSetName;
	
	public TileSet(String tileSetName) {
		this.tileSetName = tileSetName;
		
	}

	public Map<RoomType, List<TileVariation>> getTileVariations() {
		return tileVariations;
	}

}
