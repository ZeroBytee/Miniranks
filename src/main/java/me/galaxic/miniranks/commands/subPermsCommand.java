package me.galaxic.miniranks.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class subPermsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("test")) {
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("admin")) {
                    if (args[1].equalsIgnoreCase("message")) {
                        if (player.hasPermission("test.admin.message") || player.hasPermission("test.admin.*")) {
                            // message code
                        }
                    } else if (args[1].equalsIgnoreCase("teleport")) {
                        if (player.hasPermission("test.admin.teleport") || player.hasPermission("test.admin.*")) {
                            // teleport code
                        }
                    }
                }
            }
        }


        return false;
    }
}
