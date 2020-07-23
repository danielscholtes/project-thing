package me.scholtes.proceduraldungeons;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.scholtes.proceduraldungeons.dungeon.floors.Floor;
import me.scholtes.proceduraldungeons.dungeon.rooms.Direction;
import me.scholtes.proceduraldungeons.dungeon.rooms.Room;

public class Utils {
    
	/**
	 * Checks if the door to a room is valid in the specified {@link Direction}
	 * 
	 * @param room The {@link Room} to check the directions for
	 * @param dungeon Instance of the {@link Floor} that the {@link Room} belongs to
	 * @param roomTypeString A {@link String} that represent the doors the {@link Room} has
	 * @param direction The {@link Direction} to check if adjacent position has a room
	 * @param opposite The {@link Direction} the adjacent room has to have for the door to be valid
	 * @param checkNoRoom A {@link boolean} to see if it should check if the adjacent room is null
	 * @return
	 */
	public static String checkDirection(Room room, Floor floor, String roomTypeString, Direction direction, Direction opposite, boolean checkNoRoom) {
		if (roomTypeString.contains(direction.toString())) {
			Room adjacentRoom = floor.getRooms().get((room.getX() + direction.getX()) + "_" + (room.getY() + direction.getY()));
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
	
	/**
	 * Creates an {@link ItemStack} with the specified paramaters
	 * 
	 * @param material The {@link Material} of the {@link ItemStack}
	 * @param amount The amount of the {@link ItemStack}
	 * @return An {@link ItemStack}
	 */
	public static ItemStack createItemStack(Material material, int amount) {
		return new ItemStack(material, amount);
	}
	
	/**
	 * Creates an {@link ItemStack} with the specified paramaters
	 * 
	 * @param material The {@link Material} of the {@link ItemStack}
	 * @param amount The amount of the {@link ItemStack}
	 * @param name The name of the {@link ItemStack}
	 * @return An {@link ItemStack}
	 */
	public static ItemStack createItemStack(Material material, int amount, String name) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(color(name));
		item.setItemMeta(meta);
		return item;
	}
	
	/**
	 * Creates an {@link ItemStack} with the specified paramaters
	 * 
	 * @param material The {@link Material} of the {@link ItemStack}
	 * @param amount The amount of the {@link ItemStack}
	 * @param name The name of the {@link ItemStack}
	 * @param lore The lore of the {@link ItemStack}
	 * @return An {@link ItemStack}
	 */
	public static ItemStack createItemStack(Material material, int amount, String name, List<String> lore) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(color(name));
		meta.setLore(colorList(lore));
		item.setItemMeta(meta);
		return item;
	}
	
	/**
	 * Creates an {@link ItemStack} with the specified paramaters
	 * 
	 * @param material The {@link Material} of the {@link ItemStack}
	 * @param amount The amount of the {@link ItemStack}
	 * @param name The name of the {@link ItemStack}
	 * @param lore The lore of the {@link ItemStack}
	 * @param enchants The enchantments of the {@link ItemStack}
	 * @return An {@link ItemStack}
	 */
	public static ItemStack createItemStack(Material material, int amount, String name, List<String> lore, List<String> enchants) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(color(name));
		meta.setLore(colorList(lore));
		item.setItemMeta(meta);
		
		for (String enchant : enchants) {
			String[] split = enchant.split(";");
			Enchantment enchantment = Enchantment.getByKey(new NamespacedKey(ProceduralDungeons.getInstance(), split[0]));
			int level = Integer.valueOf(split[1]);
			item.addEnchantment(enchantment, level);
		}
		
		return item;
	}
	
}
