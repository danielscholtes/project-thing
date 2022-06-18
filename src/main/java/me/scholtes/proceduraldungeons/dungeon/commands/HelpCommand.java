package me.scholtes.proceduraldungeons.dungeon.commands;

import me.scholtes.proceduraldungeons.dungeon.DungeonManager;
import me.scholtes.proceduraldungeons.manager.UserManager;
import me.scholtes.proceduraldungeons.party.Party;
import me.scholtes.proceduraldungeons.party.PartyData;
import me.scholtes.proceduraldungeons.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HelpCommand implements CommandExecutor {

    private final UserManager userManager;
    private final PartyData partyData;
    private final DungeonManager dungeonManager;

    public HelpCommand(UserManager userManager, PartyData partyData, DungeonManager dungeonManager) {
        this.userManager = userManager;
        this.partyData = partyData;
        this.dungeonManager = dungeonManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            StringUtils.message(sender, "&cYou need to be a player to run this command");
            return true;
        }

        Player player = (Player) sender;

        if (!userManager.isLoggedIn(player.getUniqueId())) {
            StringUtils.message(sender, "&aHelp Menu");
            StringUtils.message(sender, "&7/register <username> <email> <password>");
            StringUtils.message(sender, "&7/login <username/email> <password>");
            if (userManager.hasAuthCode(player.getUniqueId())) {
                StringUtils.message(sender, "&7/auth <code>");
            }
            return true;
        }

        if (!userManager.isVerified(userManager.getID(player.getUniqueId()))) {
            StringUtils.message(sender, "&aHelp Menu");
            StringUtils.message(sender, "&7/verify <code>");
            StringUtils.message(sender, "&7/resend");
            return true;
        }

        StringUtils.message(sender, "&aHelp Menu");
        StringUtils.message(sender, "&7/dungeon list");
        Party party = partyData.getPartyFromPlayer(player.getUniqueId());
        if (dungeonManager.getDungeonFromPlayer(player.getUniqueId(), party) == null) {
            StringUtils.message(sender, "&7/dungeon join <dungeon>");
        } else {
            StringUtils.message(sender, "&7/dungeon leave");
        }

        if (party == null) {
            StringUtils.message(sender, "&7/party invite <username>");
            StringUtils.message(sender, "&7/party join <username>");
        } else {
            if (party.getOwner().equals(player.getUniqueId())) {
                StringUtils.message(sender, "&7/party invite <username>");
                StringUtils.message(sender, "&7/party kick <username>");
            }
            StringUtils.message(sender, "&7/party leave");
            StringUtils.message(sender, "&7/party list");
        }

        StringUtils.message(sender, "&7/statistics");
        StringUtils.message(sender, "&7/logout");
        return true;
    }

}
