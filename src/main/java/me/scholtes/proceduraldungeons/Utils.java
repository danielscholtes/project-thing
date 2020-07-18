package me.scholtes.proceduraldungeons;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.scholtes.proceduraldungeons.dungeon.floors.Floor;
import me.scholtes.proceduraldungeons.dungeon.rooms.Direction;
import me.scholtes.proceduraldungeons.dungeon.rooms.Room;

public class Utils {
    
	public static String checkDirection(final Room room, final Floor dungeon, String roomTypeString, final Direction direction, final Direction opposite, final boolean checkNoRoom) {
		if (roomTypeString.contains(direction.toString())) {
			Room adjacentRoom = dungeon.getRooms().get((room.getX() + direction.getX()) + "_" + (room.getY() + direction.getY()));
			if ((checkNoRoom && adjacentRoom == null) || (adjacentRoom != null && !adjacentRoom.getRoomType().toString().contains(opposite.toString()))) {
				if (direction == Direction.NORTH) {
					roomTypeString = roomTypeString.replaceAll(direction.toString() + "_", "");
				} else {
					roomTypeString = roomTypeString.replaceAll("_" + direction.toString(), "");
				}
				roomTypeString = roomTypeString.replaceAll(direction.toString(), "");
			}
		}
		return roomTypeString;
	}
	

	private static final Pattern HEX_PATTERN = Pattern.compile("#<([A-Fa-f0-9]){6}>");
	
	public static String color(String text) {
		Matcher matcher = HEX_PATTERN.matcher(text);

	    while (matcher.find()) {
	        String hexString = matcher.group();

	        hexString = "#" + hexString.substring(2, hexString.length() - 1);

	        final net.md_5.bungee.api.ChatColor hex = net.md_5.bungee.api.ChatColor.of(hexString);
	        final String before = text.substring(0, matcher.start());
	        final String after = text.substring(matcher.end());

	        text = before + hex + after;
	        matcher = HEX_PATTERN.matcher(text);
	    }

        return ChatColor.translateAlternateColorCodes('&', text);
	}
	
	public static void message(CommandSender sender, String text) {
		sender.sendMessage(color(text));
	}
	
}
