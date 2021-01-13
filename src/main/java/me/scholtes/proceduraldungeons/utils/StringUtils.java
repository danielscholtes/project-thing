package me.scholtes.proceduraldungeons.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class StringUtils {
	
	// Regex Pattern for hex color codes
	private static final Pattern HEX_PATTERN = Pattern.compile("#<([A-Fa-f0-9]){6}>");
	private static final Map<Message, List<String>> messages = new HashMap<>();
	
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
	public static List<String> color(List<String> list) {
		List<String> colored = new ArrayList<>();
		for (String s : list) {
			colored.add(color(s));
		}
		
		return colored;
	}

	
	/**
	 * Sends a message to the specified sender with colorized text
	 * 
	 * @param sender The {@link CommandSender} who should receive the message
	 * @param message {@link String} to colorize and send
	 */
	public static void message(CommandSender sender, String message) {
		sender.sendMessage(color(message));
	}
	/**
	 * Sends a message to the specified sender with colorized text
	 * 
	 * @param sender The {@link CommandSender} who should receive the message
	 * @param message {@link List<>} to colorize and send
	 */
	public static void message(CommandSender sender, List<String> message) {
		for (String text : message) {
			sender.sendMessage(color(text));
		}
	}
	
	public static List<String> replaceAll(List<String> message, String from, String to) {
		List<String> newMessage = new ArrayList<>();
		for (String s : message) {
			newMessage.add(s.replace(from, to));
		}
		return newMessage;
	}
	
	/**
	 * Loads all the messages from a {@link File}
	 * 
	 * @param file The {@link File} to load the message from
	 */
	public static void loadMessages(File file) {
		messages.clear();
		
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for (Message message : Message.values()) {
			setMessage(file, message);
		}
	}
	
	/**
	 * Gets a {@link List<String>} from the {@link Message}
	 * 
	 * @param message The {@link Message}
	 * @return {@link List<String>} from the {@link Message}
	 */
	public static List<String> getMessage(Message message) {
		return messages.get(message);
	}
	
	/**
	 * Sets the message in the {@link File}
	 * 
	 * @param file The {@link File}
	 * @param message The {@link Message} to set
	 */
	private static void setMessage(File file, Message message) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		if (!config.isSet(message.getPath())) {
			config.set(message.getPath(), message.getDefaultMessage());
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		messages.put(message, config.getStringList(message.getPath()));
	}

}
