package me.scholtes.proceduraldungeons.dungeon.commands;

import me.scholtes.proceduraldungeons.dungeon.Dungeon;
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

public class LogoutCommand implements CommandExecutor {

    private final UserManager userManager;
    private final PartyData partyData;
    private final DungeonManager dungeonManager;

    public LogoutCommand(UserManager userManager, PartyData partyData, DungeonManager dungeonManager) {
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
            StringUtils.message(sender, "&cYou aren't logged in");
            return true;
        }

        player.sendTitle(StringUtils.color("&a&lLogged out"), "Goodbye!", 10, 40, 10);

        partyData.getInvitations().remove(player.getUniqueId());

        Party party = partyData.getPartyFromPlayer(player.getUniqueId());

        if (party == null || party.getMembers().size() == 0) {
            if (party != null && party.getMembers().size() == 0) {
                partyData.disbandParty(party);
            }
            userManager.logout(player.getUniqueId());
            Dungeon dungeon = dungeonManager.getDungeonFromPlayer(player.getUniqueId(), party);
            if (dungeon == null) {
                return true;
            }
            dungeonManager.removeDungeon(dungeon);
            return true;
        }

        partyData.removePlayerFromParty(party, player.getUniqueId());
        userManager.logout(player.getUniqueId());
        return true;
    }

}
