package me.scholtes.proceduraldungeons.dungeon.commands;

import me.scholtes.proceduraldungeons.manager.UserManager;
import me.scholtes.proceduraldungeons.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VerifyCommand implements CommandExecutor {

    private final UserManager userManager;

    public VerifyCommand(UserManager userManager) {
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

        if (args.length < 1) {
            StringUtils.message(sender, "&cPlease specify a code");
            return true;
        }

        if (userManager.isCodeExpired(userID)) {
            StringUtils.message(sender, "&cYour code expired, please use /resend to receive a new code");
            return true;
        }

        if (userManager.verifyEmail(userID, args[0])) {
            player.sendTitle(StringUtils.color("&a&lVerification Successful"), "", 10, 40, 10);
            return true;
        }

        player.sendTitle(StringUtils.color("&c&lIncorrect Code"), "", 10, 40, 10);
        return true;
    }
}
