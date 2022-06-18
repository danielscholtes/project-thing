package me.scholtes.proceduraldungeons.dungeon.commands;

import me.scholtes.proceduraldungeons.manager.UserManager;
import me.scholtes.proceduraldungeons.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StatisticsCommand implements CommandExecutor {

    private final UserManager userManager;

    public StatisticsCommand(UserManager userManager) {
        this.userManager = userManager;
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

        int id = userManager.getID(player.getUniqueId());
        if (!userManager.isVerified(id)) {
            StringUtils.message(sender, "&cYou aren't verified");
            return true;
        }


        StringUtils.message(sender, "&a" + userManager.getUsernameID(id) + "'s Statistics");
        StringUtils.message(sender, "&7Games Played - " + userManager.getGamesPlayed(id));
        StringUtils.message(sender, "&7Games Won - " + userManager.getGamesWon(id));
        StringUtils.message(sender, "&7Total Kills - " + userManager.getTotalKills(id));
        return true;
    }
}
