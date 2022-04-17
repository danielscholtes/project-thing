package me.scholtes.proceduraldungeons.dungeon.commands;

import me.scholtes.proceduraldungeons.dungeon.manager.UserManager;
import me.scholtes.proceduraldungeons.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ResendCommand implements CommandExecutor {

    private final UserManager userManager;

    public ResendCommand(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            StringUtils.message(sender, "&cYou need to be a player to run this command");
            return true;
        }

        Player player = (Player) sender;
        int userID = userManager.getID(player.getUniqueId());

        if (!userManager.isLoggedIn(player.getUniqueId())) {
            StringUtils.message(sender, "&cYou are not logged in");
            return true;
        }

        if (userManager.isVerified(userID)) {
            StringUtils.message(sender, "&cYou are already verified");
            return true;
        }

        player.sendTitle(StringUtils.color("&a&lVerify email"), StringUtils.color("&fYou were sent a code. Please type \"/verify <code>\""), 10, 40, 10);
        userManager.sendEmailVerification(userID, userManager.getEmailUserID(userID));
        return true;
    }
}
