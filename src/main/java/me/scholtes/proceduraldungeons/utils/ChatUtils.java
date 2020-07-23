package me.scholtes.proceduraldungeons.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatUtils {
	
	// Regex Pattern for hex color codes
	private static final Pattern HEX_PATTERN = Pattern.compile("#<([A-Fa-f0-9]){6}>");
	
	/**
	 * Colorizes the given string
	 * 
	 * @param text The {@link String} to colorize
	 * @return Colorized version of the {@link String}
	 */
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
	
	/**
	 * Colorizes the given {@link List<String>}
	 * 
	 * @param list The {@link List<String>} to colorize
	 * @return Colorized version of the {@link List<String>}
	 */
	public static List<String> colorList(List<String> list) {
		List<String> colored = new ArrayList<String>();
		for (String s : list) {
			colored.add(color(s));
		}
		
		return colored;
	}
	
	/**
	 * Sends a message to the specified sender with colorized text
	 * 
	 * @param sender The {@link CommandSender} who should receive the message
	 * @param text {@link String} to colorize and send
	 */
	public static void message(CommandSender sender, String text) {
		sender.sendMessage(color(text));
	}

}
