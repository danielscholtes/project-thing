package me.scholtes.proceduraldungeons.dungeon.commands;

import me.scholtes.proceduraldungeons.manager.UserManager;
import me.scholtes.proceduraldungeons.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LoginCommand implements CommandExecutor {

    private final UserManager userManager;

    public LoginCommand(UserManager userManager) {
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

        if (args.length < 1) {
            StringUtils.message(sender, "&cPlease specify a username/email");
            return true;
        }

        if (args.length < 2) {
            StringUtils.message(sender, "&cPlease specify a password");
            return true;
        }

        if (userManager.isLoggedIn(args[0])) {
            StringUtils.message(sender, "&cSomeone is already logged into this account");
            return true;
        }

        if (userManager.loginAttempt(player.getUniqueId(), args[0], args[1])) {
            player.sendTitle(StringUtils.color("&a&lVerify Login"), "You were sent a code. Please type \"/auth <code>\"", 10, 40, 10);
            return true;
        }
        StringUtils.message(sender, "&cUsername/email does not match password");
        return true;
    }

}
