package me.scholtes.proceduraldungeons.dungeon;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import me.scholtes.proceduraldungeons.manager.UserManager;
import me.scholtes.proceduraldungeons.dungeon.tilesets.TileSet;
import me.scholtes.proceduraldungeons.party.Party;
import me.scholtes.proceduraldungeons.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class DungeonManager {

	private final Map<UUID, Dungeon> dungeons;
	private final Map<String, DungeonInfo> dungeonInfo;
	private final Map<String, TileSet> tileSets;
	private final Map<String, ItemStack> items;
	private final UserManager userManager;

	public DungeonManager(UserManager userManager) {
		dungeons = new HashMap<>();
		dungeonInfo = new HashMap<>();
		tileSets = new HashMap<>();
		items = new HashMap<>();
		this.userManager = userManager;
	}

	/**
	 * Loads all the {@link ItemStack} and puts it into a {@link Map<>}
	 */
	public void loadItems() {
		Bukkit.getScheduler().runTaskAsynchronously(ProceduralDungeons.getInstance(), () -> {
			dungeonInfo.clear();
			
			String path = ProceduralDungeons.getInstance().getDataFolder().getAbsolutePath() + File.separator;
			File file = new File(path, "items.yml");
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			
			for (String item : config.getConfigurationSection("").getKeys(false)) {
				Material material = Material.valueOf(config.getString(item + ".material").toUpperCase());
				int amount = config.getInt(item + ".amount");
				String name = config.getString(item + ".name");
				List<String> lore = config.getStringList(item + ".lore");
				List<String> enchants = config.getStringList(item + ".enchants");
				items.put(item, ItemUtils.createItemStack(material, amount, name, lore, enchants));
			}
		});
	}
	
	/**
	 * Gets the {@link Dungeon} from the specified {@link UUID}
	 * 
	 * @param uuid The {@link UUID} of the {@link Dungeon}
	 * @return The {@link Dungeon} if one is found, otherwise it will return {@link null}
	 */
	public Dungeon getDungeonFromID(UUID uuid) {
		for (Dungeon dungeon : dungeons.values()) {
			if (dungeon.getDungeonID().equals(uuid)) {
				return dungeon;
			}
		}
		return null;
	}
	
	/**
	 * Gets the {@link Dungeon} from a player {@link UUID} or {@link Party}
	 * 
	 * @param uuid The player {@link UUID}
	 * @param party The {@link Party}
	 * @return The {@link Dungeon} if one is found, otherwise it will return {@link null}
	 */
	public Dungeon getDungeonFromPlayer(UUID uuid, Party party) {
		if ((party == null && !getDungeons().containsKey(uuid)) || (party != null && !getDungeons().containsKey(party.getOwner()))) {
			return null;
		}
		
		if (party == null) {
			return getDungeons().get(uuid);
		} else {
			return getDungeons().get(party.getOwner());
		}
	}
	
	/**
	 * Loads all the {@link DungeonInfo} and puts it into a {@link Map<>}
	 */
	public void loadDungeonInfo() {
		Bukkit.getScheduler().runTaskAsynchronously(ProceduralDungeons.getInstance(), () -> {
			dungeonInfo.clear();
			
			for (String dungeon : ProceduralDungeons.getInstance().getConfig().getConfigurationSection("dungeons").getKeys(false)) {
				dungeonInfo.put(dungeon, new DungeonInfo(dungeon, ProceduralDungeons.getInstance().getDungeonManager()));
			}
		});
	}
	
	/**
	 * Loads all the {@link TileSet} and puts it into a {@link Map<>}
	 */
	public void loadTileSets() {
		Bukkit.getScheduler().runTaskAsynchronously(ProceduralDungeons.getInstance(), () -> {
			tileSets.clear();
			
			for (String tileSet : ProceduralDungeons.getInstance().getConfig().getConfigurationSection("tile_sets").getKeys(false)) {
				tileSets.put(tileSet, new TileSet(tileSet));
			}
		});
	}
	
	/**
	 * Makes the {@link Player} join a new instance of a {@link Dungeon}
	 * 
	 * @param player The player to join (party leader)
	 * @param dungeonName The name of the dungeon
	 */
	public void joinDungeon(Player player, String dungeonName) {
		Dungeon dungeon = new Dungeon(ProceduralDungeons.getInstance(), getDungeonInfo(dungeonName), player.getUniqueId());
		dungeons.put(player.getUniqueId(), dungeon);
		dungeon.generateDungeon();
	}
	
	/**
	 * Removes the {@link Dungeon} specified
	 * 
	 * @param dungeon The {@link Dungeon} to remove
	 */
	public void removeDungeon(Dungeon dungeon) {
		for (Player p : dungeon.getWorld().getPlayers()) {
			p.teleport(dungeon.getDungeonInfo().getFinishLocation());
			p.setGameMode(dungeon.getDungeonInfo().getLeaveGameMode());
			if (!dungeon.getDungeonInfo().getLeaveResourcePack().equalsIgnoreCase("none")) {
				Bukkit.getScheduler().runTaskLater(ProceduralDungeons.getInstance(), () -> {
					p.setResourcePack(dungeon.getDungeonInfo().getLeaveResourcePack());
				}, 10L);
			}
		}
		
		Party party = ProceduralDungeons.getInstance().getPartyData().getPartyFromPlayer(dungeon.getDungeonOwner());
		if (party != null) {
			for (UUID uuid : party.getMembers()) {
				Player bukkitPlayer = Bukkit.getPlayer(uuid);
				if (bukkitPlayer == null) {
					continue;
				}
				bukkitPlayer.teleport(dungeon.getDungeonInfo().getFinishLocation());
				bukkitPlayer.setGameMode(dungeon.getDungeonInfo().getLeaveGameMode());
			}
			Bukkit.getPlayer(party.getOwner()).teleport(dungeon.getDungeonInfo().getFinishLocation());
			Bukkit.getPlayer(party.getOwner()).setGameMode(dungeon.getDungeonInfo().getLeaveGameMode());
		}

		Bukkit.getServer().unloadWorld(dungeon.getWorld(), false);

		// Deletes the world files of the dungeon world
		try (Stream<Path> files = Files.walk(dungeon.getWorld().getWorldFolder().toPath())) {
			files.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		dungeons.remove(dungeon.getDungeonOwner());

		for (String cmd : dungeon.getDungeonInfo().getLeaveCommands()) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("\\{player}", Bukkit.getPlayer(dungeon.getDungeonOwner()).getName()));
		}
		
	}
	
	/**
	 * Gets rid of all active dungeons
	 */
	public void clearDungeons() {
		for (Dungeon dungeon : getDungeons().values()) {
			if (dungeon.getWorld() == null) {
				continue;
			}

			for (Player p : dungeon.getWorld().getPlayers()) {
				p.teleport(dungeon.getDungeonInfo().getFinishLocation());
			}

			Bukkit.getServer().unloadWorld(dungeon.getWorld(), false);

			// Deletes the world files of the dungeon world
			try (Stream<Path> files = Files.walk(dungeon.getWorld().getWorldFolder().toPath())) {
				files.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		getDungeons().clear();
	}
	
	/**
	 * Clears all dungeon information and loads it again with updated information
	 */
	public void reloadDungeons() {
		// Gets rid of all active dungeons
		clearDungeons();
		
		// Clears all information
		tileSets.clear();
		dungeonInfo.clear();
		items.clear();
		
		// Loads all the updated information
		loadItems();
		loadDungeonInfo();
		loadTileSets();
	}
	
	/**
	 * Gets a {@link Map<>} with all the solo players and/or party leaders
	 * currently in a dungeon
	 * 
	 * @return A {@link Map<>} with all players in a dungeon
	 */
	public Map<UUID, Dungeon> getDungeons() {
		return dungeons;
	}
	
	/**
	 * Gets the {@link ItemStack} for the specified item name
	 * 
	 * @param item The name of the item
	 * @return {@link ItemStack} for the specified item name
	 */
	public ItemStack getItem(String item) {
		return items.get(item);
	}
	
	/**
	 * Gets the {@link DungeonInfo} for the specified dungeon name
	 * 
	 * @param dungeonName The name of the dungeon
	 * @return {@link DungeonInfo} for the specified dungeon name
	 */
	public DungeonInfo getDungeonInfo(String dungeonName) {
		return dungeonInfo.get(dungeonName);
	}

	/**
	 * Gets the {@link TileSet} for the specified tileset name
	 * 
	 * @param tileSet The name of the tileset
	 * @return {@link TileSet} for the specified tileset name
	 */
	public TileSet getTileSet(String tileSet) {
		return tileSets.get(tileSet);
	}

	public Set<String> getDungeonNames() {
		return dungeonInfo.keySet();
	}
	
}
