package me.scholtes.proceduraldungeons.dungeon.listeners;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.dungeon.DungeonManager;
import me.scholtes.proceduraldungeons.dungeon.rooms.RoomType;
import me.scholtes.proceduraldungeons.dungeon.tilesets.BossVariation;
import me.scholtes.proceduraldungeons.dungeon.tilesets.TileSet;
import me.scholtes.proceduraldungeons.dungeon.tilesets.TileVariation;
import me.scholtes.proceduraldungeons.dungeon.tilesets.Variation;
import me.scholtes.proceduraldungeons.nbt.NBT;
import me.scholtes.proceduraldungeons.utils.ChatUtils;

public class WandListeners implements Listener {
	
	private final DungeonManager dungeonManager;
	private final ProceduralDungeons plugin;
	
	public WandListeners(DungeonManager dungeonManager, ProceduralDungeons plugin) {
		this.plugin = plugin;
		this.dungeonManager = dungeonManager;
	}
	
	/**
	 * Whenever a player uses a Chest Wand, this method is called
	 * 
	 * @param event The {@link PlayerInteractEvent}
	 */
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onChestWandInteract(PlayerInteractEvent event) {
		if (event.getItem() == null) {
			return;
		}
		
		ItemStack wand = event.getItem();
		NBT nbt = NBT.get(wand);
		if (!nbt.hasKey("TileSet") || !nbt.hasKey("WandType") || !nbt.getString("WandType").equalsIgnoreCase("Chest")) {
			return;
		}
		
		event.setCancelled(true);
		
		/**
		 * Whenever a player left clicks a block it updates the paste location
		 */
		if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND) {
			ChatUtils.message(event.getPlayer(), "&aSet paste location");
			nbt.setInt("PasteLocX", event.getClickedBlock().getX());
			nbt.setInt("PasteLocY", event.getClickedBlock().getY());
			nbt.setInt("PasteLocZ", event.getClickedBlock().getZ());
			event.getPlayer().setItemInHand(nbt.apply(wand));
			if (!nbt.hasKey("ChestLocX")) {
				System.out.println("a");
				return;
			}

			ChatUtils.message(event.getPlayer(), "&aAdded the chest location to the tileset: " + nbt.getString("TileSet") + ", tile: " + nbt.getString("RoomType") + ", variation: " + nbt.getString("Variation"));
			handleChestWand(nbt);
			return;
		}

		/**
		 * Whenever a player right clicks a block it updates the chest location
		 */
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND) {
			ChatUtils.message(event.getPlayer(), "&aSet chest location");
			nbt.setInt("ChestLocX", event.getClickedBlock().getX());
			nbt.setInt("ChestLocY", event.getClickedBlock().getY());
			nbt.setInt("ChestLocZ", event.getClickedBlock().getZ());
			event.getPlayer().setItemInHand(nbt.apply(wand));
			if (!nbt.hasKey("PasteLocX")) {
				System.out.println("b");
				return;
			}

			ChatUtils.message(event.getPlayer(), "&aAdded the chest location to the tileset: " + nbt.getString("TileSet") + ", tile: " + nbt.getString("RoomType") + ", variation: " + nbt.getString("Variation"));
			handleChestWand(nbt);
			return;
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMobWandInteract(PlayerInteractEvent event) {
		if (event.getItem() == null) {
			return;
		}
		
		ItemStack wand = event.getItem();
		NBT nbt = NBT.get(wand);
		if (!nbt.hasKey("TileSet") || !nbt.hasKey("WandType") || !nbt.getString("WandType").equalsIgnoreCase("Mob")) {
			return;
		}
		
		event.setCancelled(true);

		/**
		 * Whenever a player left clicks a block it updates the paste location
		 */
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			ChatUtils.message(event.getPlayer(), "&aSet paste location");
			nbt.setInt("PasteLocX", event.getClickedBlock().getX());
			nbt.setInt("PasteLocY", event.getClickedBlock().getY());
			nbt.setInt("PasteLocZ", event.getClickedBlock().getZ());
			event.getPlayer().setItemInHand(nbt.apply(wand));
			if (!nbt.hasKey("MobLocX")) {
				return;
			}

			ChatUtils.message(event.getPlayer(), "&aAdded the mob location to the tileset: " + nbt.getString("TileSet") + ", tile: " + nbt.getString("RoomType") + ", variation: " + nbt.getString("Variation"));
			handleMobWand(nbt);
		}

		/**
		 * Whenever a player rights clicks a block it updates the mob location
		 */
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ChatUtils.message(event.getPlayer(), "&aSet mob location");
			nbt.setInt("MobLocX", event.getClickedBlock().getX());
			nbt.setInt("MobLocY", event.getClickedBlock().getY());
			nbt.setInt("MobLocZ", event.getClickedBlock().getZ());
			event.getPlayer().setItemInHand(nbt.apply(wand));
			if (!nbt.hasKey("PasteLocX")) {
				return;
			}

			ChatUtils.message(event.getPlayer(), "&aAdded the mob location to the tileset: " + nbt.getString("TileSet") + ", tile: " + nbt.getString("RoomType") + ", variation: " + nbt.getString("Variation"));
			handleMobWand(nbt);
		}
	}
	
	/**
	 * Prevents a player from breaking a block with a wand
	 * 
	 * @param event The {@link BlockBreakEvent}
	 */
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (event.getPlayer().getItemInHand() == null) {
			return;
		}
		
		ItemStack wand = event.getPlayer().getItemInHand();
		NBT nbt = NBT.get(wand);
		if (!nbt.hasKey("TileSet")) {
			return;
		}
		
		event.setCancelled(true);
	}
	
	/**
	 * Adds the new mob location to the variation
	 * 
	 * @param nbt The {@link NBT} of the wand
	 */
	private void handleMobWand(NBT nbt) {
		TileSet tileSet = dungeonManager.getTileSet(nbt.getString("TileSet"));
		RoomType roomType = RoomType.valueOf(nbt.getString("RoomType"));
		String variationName = nbt.getString("Variation");
		
		int diffX = nbt.getInt("MobLocX") - nbt.getInt("PasteLocX");
		int diffY = nbt.getInt("MobLocY") - nbt.getInt("PasteLocY");
		int diffZ = nbt.getInt("MobLocZ") - nbt.getInt("PasteLocZ");
		String location = String.valueOf(diffX) + ";" + String.valueOf(diffY) + ";" + String.valueOf(diffZ);
		
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			String path = ProceduralDungeons.getInstance().getDataFolder().getAbsolutePath() + File.separator + tileSet.getTileSetName() + File.separator + roomType.toString() + File.separator;
			File file = new File(path, "variations.yml");
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);

			List<String> locations = new ArrayList<String>();
			if (config.isSet("variations." + variationName  + ".mobs")) {
				locations = config.getStringList("variations." + variationName  + ".mobs");
			}
			locations.add(location);
			config.set("variations." + variationName  + ".mobs", locations);
			if (!config.isSet("variations." + variationName + ".chests")) {
				config.set("variations." + variationName + ".chests", new ArrayList<String>());
			}
			if (roomType == RoomType.BOSS) {
				if (!config.isSet("variations." + variationName + ".boss")) {
					config.set("variations." + variationName + ".boss", "0;0;0");
				}
			}
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for (Variation variation : tileSet.getVariations().get(RoomType.valueOf(nbt.getString("RoomType")))) {
				if (variation.getVariationName().equalsIgnoreCase(variationName)) {
					variation.getMobLocations().add(location);
					return;
				}
			}

			if (roomType == RoomType.BOSS) {
				dungeonManager.getTileSet(nbt.getString("TileSet")).getVariations().get(RoomType.valueOf(nbt.getString("RoomType"))).add(new BossVariation(tileSet, variationName, roomType));
				return;
			}
			dungeonManager.getTileSet(nbt.getString("TileSet")).getVariations().get(RoomType.valueOf(nbt.getString("RoomType"))).add(new TileVariation(tileSet, variationName, roomType));
		});
	}

	/**
	 * Adds the new chest location to the variation
	 * 
	 * @param nbt The {@link NBT} of the wand
	 */
	private void handleChestWand(NBT nbt) {
		TileSet tileSet = dungeonManager.getTileSet(nbt.getString("TileSet"));
		RoomType roomType = RoomType.valueOf(nbt.getString("RoomType"));
		String variationName = nbt.getString("Variation");
		
		int diffX = nbt.getInt("ChestLocX") - nbt.getInt("PasteLocX");
		int diffY = nbt.getInt("ChestLocY") - nbt.getInt("PasteLocY");
		int diffZ = nbt.getInt("ChestLocZ") - nbt.getInt("PasteLocZ");
		String location = String.valueOf(diffX) + ";" + String.valueOf(diffY) + ";" + String.valueOf(diffZ);
		
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			String path = ProceduralDungeons.getInstance().getDataFolder().getAbsolutePath() + File.separator + tileSet.getTileSetName() + File.separator + roomType.toString() + File.separator;
			File file = new File(path, "variations.yml");
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);

			List<String> locations = new ArrayList<String>();
			if (config.isSet("variations." + variationName  + ".chests")) {
				locations = config.getStringList("variations." + variationName  + ".chests");
			}
			locations.add(location);
			config.set("variations." + variationName  + ".chests", locations);
			if (!config.isSet("variations." + variationName + ".mobs")) {
				config.set("variations." + variationName + ".mobs", new ArrayList<String>());
			}
			if (roomType == RoomType.BOSS) {
				if (!config.isSet("variations." + variationName + ".boss")) {
					config.set("variations." + variationName + ".boss", "0;0;0");
				}
			}
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for (Variation variation : tileSet.getVariations().get(RoomType.valueOf(nbt.getString("RoomType")))) {
				if (variation.getVariationName().equalsIgnoreCase(variationName)) {
					variation.getChestLocations().add(location);
					return;
				}
			}

			if (roomType == RoomType.BOSS) {
				dungeonManager.getTileSet(nbt.getString("TileSet")).getVariations().get(RoomType.valueOf(nbt.getString("RoomType"))).add(new BossVariation(tileSet, variationName, roomType));
				return;
			}
			dungeonManager.getTileSet(nbt.getString("TileSet")).getVariations().get(RoomType.valueOf(nbt.getString("RoomType"))).add(new TileVariation(tileSet, variationName, roomType));
		});
	}

}
