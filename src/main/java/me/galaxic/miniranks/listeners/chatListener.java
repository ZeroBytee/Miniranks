package me.galaxic.miniranks.listeners;

import me.galaxic.miniranks.Miniranks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class chatListener implements Listener {

    private Miniranks miniranks;
    public chatListener(Miniranks plugin) {
        this.miniranks = plugin;
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String message = e.getMessage();

        Bukkit.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.translateAlternateColorCodes('&',
                miniranks.getRankManager().getRank(player)) + ChatColor.WHITE + "] " + player.getName() + ": " + message);
        e.setCancelled(true);
    }

}
