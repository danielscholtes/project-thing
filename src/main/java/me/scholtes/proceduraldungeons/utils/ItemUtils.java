package me.scholtes.proceduraldungeons.utils;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {
	
	/**
	 * Creates an {@link ItemStack} with the specified parameters
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
		if (name != null) {
			meta.setDisplayName(StringUtils.color(name));	
		}
		if (lore != null) {
			meta.setLore(StringUtils.color(lore));
		}
		item.setItemMeta(meta);
		
		if (enchants != null) {
			for (String enchant : enchants) {
				String[] split = enchant.split(";");
				Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(split[0].toLowerCase()));
				int level = Integer.parseInt(split[1]);
				item.addUnsafeEnchantment(enchantment, level);
			}
		}
		
		return item;
	}
	
}
