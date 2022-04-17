package me.scholtes.proceduraldungeons.dungeon.commands;

import me.scholtes.proceduraldungeons.dungeon.manager.UserManager;
import me.scholtes.proceduraldungeons.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AuthCommand implements CommandExecutor {

    private final UserManager userManager;

    public AuthCommand(UserManager userManager) {
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
            StringUtils.message(sender, "&cPlease specify a code");
            return true;
        }

        if (!userManager.hasAuthCode(player.getUniqueId())) {
            StringUtils.message(sender, "&cYour code expired, try login again for a new code");
            return true;
        }

        if (userManager.authenticate(player.getUniqueId(), args[0])) {
            player.sendTitle(StringUtils.color("&a&lLogged in"), "Welcome back!", 10, 40, 10);
            return true;
        }

        player.sendTitle(StringUtils.color("&c&lIncorrect Code"), "", 10, 40, 10);
        return true;
    }
}
