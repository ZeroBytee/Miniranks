package me.galaxic.miniranks.tabCompleter;

import me.galaxic.miniranks.Miniranks;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class rankTab implements TabCompleter {

    private Miniranks miniranks;
    public rankTab(Miniranks plugin) {
        this.miniranks = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("create", "set", "delete"), new ArrayList<>());
        } else if (args.length == 2) {

            if (args[0].equalsIgnoreCase("set")) {
                List<String> names = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    names.add(player.getName());
                }
                return StringUtil.copyPartialMatches(args[1], names, new ArrayList<>());
            } else if (args[0].equalsIgnoreCase("create")) {
                return StringUtil.copyPartialMatches(args[1], Collections.singletonList("<rank>"), new ArrayList<>());
            } else if (args[0].equalsIgnoreCase("delete")) {
                List<String> ranks = miniranks.getRankManager().getRanks();
                return StringUtil.copyPartialMatches(args[1], ranks, new ArrayList<>());
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set")) {
                // get all the ranks from the database and for each rank, add it to the list
                List<String> ranks = miniranks.getRankManager().getRanks();
                return StringUtil.copyPartialMatches(args[2], ranks, new ArrayList<>());
            } else if (args[0].equalsIgnoreCase("create")) {
                return StringUtil.copyPartialMatches(args[2], Collections.singletonList("<prefix>"), new ArrayList<>());
            }
        }

        return new ArrayList<>();
    }
}
