package me.scholtes.proceduraldungeons.dungeon.commands;

import me.scholtes.proceduraldungeons.dungeon.manager.UserManager;
import me.scholtes.proceduraldungeons.utils.Message;
import me.scholtes.proceduraldungeons.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class RegisterCommand  implements CommandExecutor {

    private final UserManager userManager;

    public RegisterCommand(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            StringUtils.message(sender, "&cYou need to be a player to run this command");
            return true;
        }

        Player player = (Player) sender;

        if (userManager.isLoggedIn(player.getUniqueId())) {
            StringUtils.message(sender, "&cYou are already logged in");
            return true;
        }

        if (args.length <= 1) {
            StringUtils.message(sender, "&cPlease specify a username");
            return true;
        }

        if (args.length < 2) {
            StringUtils.message(sender, "&cPlease specify an email address");
            return true;
        }

        if (args.length < 3) {
            StringUtils.message(sender, "&cPlease specify a password");
            return true;
        }

        if (args[0].length() > 24) {
            StringUtils.message(sender, "&cUsernames can't be longer than 24 characters");
            return true;
        }

        if (!args[0].matches("^[a-zA-Z0-9_]+$")) {
            StringUtils.message(sender, "&cUsernames must not contain special characters");
            return true;
        }

        if (!UserManager.isEmail(args[1])) {
            StringUtils.message(sender, "&cNot a valid email address");
            return true;
        }

        if (userManager.userExists(args[0])) {
            StringUtils.message(sender, "&cAn account with that username already exists");
            return true;
        }

        if (userManager.emailExists(args[1])) {
            StringUtils.message(sender, "&cAn account with that email already exists");
            return true;
        }

        player.sendTitle(StringUtils.color("&a&lVerify email"), StringUtils.color("&fYou were sent a code. Please type \"/verify <code>\""), 10, 40, 10);
        userManager.register(player.getUniqueId(), args[0], args[1], args[2]);

        return true;
    }
}
