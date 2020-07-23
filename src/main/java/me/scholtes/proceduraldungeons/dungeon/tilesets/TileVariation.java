package me.scholtes.proceduraldungeons.dungeon.tilesets;

import me.scholtes.proceduraldungeons.dungeon.rooms.RoomType;

public class TileVariation {

	private final TileSet tileSet;
	private final String variation;
	private final RoomType roomType;
	
	public TileVariation(TileSet tileSet, String variation, RoomType roomType) {
		this.tileSet = tileSet;
		this.variation = variation;
		this.roomType = roomType;
	}
	
}
