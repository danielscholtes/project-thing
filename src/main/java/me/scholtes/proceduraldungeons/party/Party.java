package me.scholtes.proceduraldungeons.party;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import me.scholtes.proceduraldungeons.utils.StringUtils;

public class Party {
	
	private List<UUID> members = new ArrayList<UUID>();
	private UUID owner;
	
	/**
	 * Constructor for the {@link Party}
	 * 
	 * @param owner The {@link Party} leader
	 */
	public Party(UUID owner) {
		this.owner = owner;
	}
	
	/**
	 * Gets the {@link Party} leader
	 * 
	 * @return The {@link Party} leader
	 */
	public UUID getOwner() {
		return this.owner;
	}
	
	/**
	 * Sets the {@link Party} leader
	 * 
	 * @return The {@link Party} leader
	 */
	public void setOwner(UUID newOwner) {
		this.owner = newOwner;
	}
	
	/**
	 * Sets the {@link Party} members (excluding leader)
	 * 
	 * @return The {@link Party} members (excluding leader)
	 */
	public List<UUID> getMembers() {
		return members;
	}
	
	/**
	 * Messages all the {@link Party} members (including leader)
	 * 
	 * @param message The message to send
	 */
	public void messageMembers(String message) {
		for (UUID uuid : this.members) {
			if (Bukkit.getPlayer(uuid) != null) {
				StringUtils.message(Bukkit.getPlayer(uuid), message);
			}
		}
		if (Bukkit.getPlayer(owner) != null) {
			StringUtils.message(Bukkit.getPlayer(owner), message);
		}
	}
	
	/**
	 * Messages all the {@link Party} members (including leader)
	 * 
	 * @param message The message to send
	 */
	public void messageMembers(List<String> message) {
		for (UUID uuid : this.members) {
			if (Bukkit.getPlayer(uuid) != null) {
				StringUtils.message(Bukkit.getPlayer(uuid), message);
			}
		}
		if (Bukkit.getPlayer(owner) != null) {
			StringUtils.message(Bukkit.getPlayer(owner), message);
		}
	}

}
