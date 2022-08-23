package me.galaxic.miniranks.commands;

import me.galaxic.miniranks.Miniranks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.galaxic.miniranks.utils.rankGuiUtils;

public class playersCommand implements CommandExecutor {

    private Miniranks miniranks;
    public playersCommand(Miniranks plugin) {
        this.miniranks = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("players")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("miniranks.players")) {
                    new rankGuiUtils(player, 1, miniranks);
                }
            }

        }
        return false;
    }
}
