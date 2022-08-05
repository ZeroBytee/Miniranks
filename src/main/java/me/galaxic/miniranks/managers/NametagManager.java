package me.galaxic.miniranks.managers;

import me.galaxic.miniranks.Miniranks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.sql.SQLOutput;
import java.util.Objects;

public class NametagManager {

    private Miniranks miniranks;
    public NametagManager(Miniranks plugin) {
        this.miniranks = plugin;
    }

    public void setNameTags(Player player, String rank, boolean prefixEnabled, String prefix) {
        Scoreboard pscore = player.getScoreboard();
        player.setScoreboard(pscore);
        System.out.println("Setting name tags for " + player.getName() + " with rank " + rank + " and prefix enabled: " + prefixEnabled);
        if (pscore.getTeam(ChatColor.translateAlternateColorCodes('&', rank)) != null) {
            Team team = pscore.getTeam(ChatColor.translateAlternateColorCodes('&', rank));
            if (team != null) {
                team.unregister();
            }
            Team team1 = pscore.registerNewTeam(ChatColor.translateAlternateColorCodes('&', rank));
            if (prefixEnabled) {
                String rank2 = ChatColor.translateAlternateColorCodes('&', prefix);
                team1.setPrefix(ChatColor.WHITE + "[" + rank2 + ChatColor.WHITE + "] ");
                player.setPlayerListName(ChatColor.WHITE + "[" + rank2 + ChatColor.WHITE + "] " + player.getName());
                System.out.println("[Miniranks] " + player.getName() + " has a prefix of " + rank2);
            } else {
                System.out.println("Prefix is not enabled for this rank.");
            }

        } else {
            Team team1 = pscore.registerNewTeam(ChatColor.translateAlternateColorCodes('&', rank));
            if (prefixEnabled) {
                String rank2 = ChatColor.translateAlternateColorCodes('&', prefix);
                team1.setPrefix(ChatColor.WHITE + "[" + rank2 + ChatColor.WHITE + "] ");
                player.setPlayerListName(ChatColor.WHITE + "[" + rank2 + ChatColor.WHITE + "] " + player.getName());
                System.out.println("[Miniranks] " + player.getName() + " has a prefix of " + rank2);
            }else {
                System.out.println("Prefix is not enabled for this rank.");
            }
        }
    }

    public void newTag(Player player, String prefix) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.getScoreboard().getEntryTeam(player.getName()) != null) {
                String rank = ChatColor.translateAlternateColorCodes('&', prefix);
                Objects.requireNonNull(target.getScoreboard().getTeam(rank)).addEntry(player.getName());
                //player.setPlayerListName(ChatColor.WHITE + "[" + rank + ChatColor.WHITE + "] " + player.getName());
            }
        }
    }

    public void removeTag(Player player) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.getScoreboard().getEntryTeam(player.getName()) != null) {
                Objects.requireNonNull(target.getScoreboard().getTeam(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(player.getScoreboard().getEntryTeam(player.getName())).getName()))).removeEntry(player.getName());
            }
        }
    }


}
