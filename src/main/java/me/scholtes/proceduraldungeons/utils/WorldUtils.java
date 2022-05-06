package me.scholtes.proceduraldungeons.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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
		FileInputStream input = null;
		try {

			input = new FileInputStream(file);
			Clipboard clipboard;
			Location location = new Location(Bukkit.getWorld(worldName), x, y, z);

			ClipboardFormat format = ClipboardFormats.findByFile(file);
			try (ClipboardReader reader = format.getReader(input)) {
				clipboard = reader.read();
				try (EditSession editSession = com.sk89q.worldedit.WorldEdit.getInstance().getEditSessionFactory()
						.getEditSession(new BukkitWorld(location.getWorld()), -1, WorldEditPlugin.getInstance().wrapCommandSender(Bukkit.getConsoleSender()))) {
					Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(BlockVector3.at(x, y, z)).build();
					try {
						Operations.complete(operation);
					} catch (WorldEditException e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
