package me.galaxic.miniranks.listeners;

import me.galaxic.miniranks.Miniranks;
import me.galaxic.miniranks.utils.rankGuiUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import me.galaxic.miniranks.utils.managePlayerGuiUtils;

import java.util.Objects;

public class GuiListener implements Listener {

    private Miniranks miniranks;
    public GuiListener(Miniranks plugin) {
        this.miniranks = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        if (e.getInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains(ChatColor.DARK_GREEN +
                ChatColor.translateAlternateColorCodes('&', "&lOnline Players"))) {

            int page = Integer.parseInt(e.getInventory().getItem(36).getItemMeta().getLocalizedName());
            if (e.getRawSlot() == 36 && e.getCurrentItem().getType().equals(Material.ARROW)) {
                Player player = (Player) e.getWhoClicked();
                new rankGuiUtils(player, page - 1, miniranks);
            } else if (e.getRawSlot() == 44 && e.getCurrentItem().getType().equals(Material.ARROW)) {
                Player player = (Player) e.getWhoClicked();
                new rankGuiUtils(player, page + 1, miniranks);
            }

            if (e.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {
                String name = ChatColor.stripColor(Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName());
                Player player = (Player) e.getWhoClicked();
                Player target = player.getServer().getPlayer(name);
                assert target != null;
                new managePlayerGuiUtils(player, target, 1, miniranks);

            }

            e.setCancelled(true);

        } else if (e.getInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains(ChatColor.GREEN + "" + ChatColor.BOLD + "Change player rank")) {
            if (e.getCurrentItem().getType().equals(Material.GREEN_STAINED_GLASS_PANE)) {
                String rank = ChatColor.stripColor(Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName());
                Player player = (Player) e.getWhoClicked();
                System.out.println(player);
                Player target = player.getServer().getPlayer(ChatColor.stripColor(e.getView().getTitle().replace("Change player rank - ", "")));
                assert target != null;
                miniranks.getRankManager().setRank(target, rank);
                player.closeInventory();
                target.sendMessage(ChatColor.GREEN + "Your rank has been changed to " + rank);
                player.sendMessage(ChatColor.GREEN + "You have changed " + target.getName() + "'s rank to " + rank);
            }

            int page = Integer.parseInt(e.getInventory().getItem(0).getItemMeta().getLocalizedName());
            if (e.getRawSlot() == 36 && e.getCurrentItem().getType().equals(Material.ARROW)) {
                Player player = (Player) e.getWhoClicked();
                new rankGuiUtils(player, page - 1, miniranks);
            } else if (e.getRawSlot() == 44 && e.getCurrentItem().getType().equals(Material.ARROW)) {
                Player player = (Player) e.getWhoClicked();
                new rankGuiUtils(player, page + 1, miniranks);
            }


            e.setCancelled(true);
        }

    }


}
