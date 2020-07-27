package me.scholtes.proceduraldungeons.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;

public class WorldUtils {
	
	/**
	 * Pastes a schematic at the given location
	 * 
	 * @param file The file of the schematic
	 * @param worldName The name of the world 
	 * @param x The X position
	 * @param y The Y position
	 * @param z The Z position
	 */
	public static void pasteSchematic(File file, String worldName, double x, double y, double z) {
		Clipboard clipboard;

		ClipboardFormat format = ClipboardFormats.findByFile(file);
		try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
			clipboard = reader.read();
			try (EditSession editSession = new EditSessionBuilder(FaweAPI.getWorld(worldName)).fastmode(true).build()) {
			    Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(BlockVector3.at(x, y, z)).build();
			    try {
					Operations.complete(operation);
				} catch (WorldEditException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
