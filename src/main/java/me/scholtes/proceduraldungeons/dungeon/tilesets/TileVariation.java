package me.scholtes.proceduraldungeons.dungeon.tilesets;

import me.scholtes.proceduraldungeons.dungeon.rooms.RoomType;

public class TileVariation extends Variation {
	
	/**
	 * Constructor for the {@link TileVariation}
	 * 
	 * @param tileSet The instance of the {@link TileSet} this {@link TileVariation} belongs to
	 * @param variation The name of this {@link TileVariation}
	 * @param roomType The {@link RoomType} of this {@link TileVariation}
	 */
	public TileVariation(TileSet tileSet, String variation, RoomType roomType) {
		super(tileSet, variation, roomType);
	}
	
}
