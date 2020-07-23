package me.scholtes.proceduraldungeons.utils;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.scholtes.proceduraldungeons.ProceduralDungeons;

public class ItemUtils {
	
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
		meta.setDisplayName(ChatUtils.color(name));
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
		meta.setDisplayName(ChatUtils.color(name));
		meta.setLore(ChatUtils.colorList(lore));
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
		meta.setDisplayName(ChatUtils.color(name));
		meta.setLore(ChatUtils.colorList(lore));
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
