package me.galaxic.miniranks.listeners;

import me.galaxic.miniranks.Miniranks;
import me.galaxic.miniranks.utils.permsGuiUtils;
import me.galaxic.miniranks.utils.rankGuiUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import me.galaxic.miniranks.utils.managePlayerGuiUtils;
import me.galaxic.miniranks.utils.pluginsGuiUtils;
import org.bukkit.permissions.Permission;

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

        } else if (e.getInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains(ChatColor.GREEN + "Change rank")) {
            if (e.getCurrentItem().getType().equals(Material.GREEN_STAINED_GLASS_PANE)) {
                String rank = Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getLocalizedName();
                Player player = (Player) e.getWhoClicked();
                String targetString = Objects.requireNonNull(Objects.requireNonNull(e.getInventory().getItem(8)).getItemMeta()).getLocalizedName();
                Player target = player.getServer().getPlayer(targetString);
                assert target != null;
                miniranks.getRankManager().setRank(target, rank);
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "You have changed " + target.getName() + "'s rank to " +
                        ChatColor.translateAlternateColorCodes('&', rank));
                target.sendMessage(ChatColor.GREEN + "Your rank has been changed to " + ChatColor.translateAlternateColorCodes('&', rank));
            }

            int page = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(e.getInventory().getItem(0)).getItemMeta()).getLocalizedName());
            String targetString = Objects.requireNonNull(Objects.requireNonNull(e.getInventory().getItem(8)).getItemMeta()).getLocalizedName();
            Player player = (Player) e.getWhoClicked();
            Player target = player.getServer().getPlayer(targetString);
            if (e.getRawSlot() == 0 && e.getCurrentItem().getType().equals(Material.ARROW)) {
                assert target != null;
                new managePlayerGuiUtils(player, target, page - 1, miniranks);
            } else if (e.getRawSlot() == 8 && e.getCurrentItem().getType().equals(Material.ARROW)) {
                assert target != null;
                new managePlayerGuiUtils(player, target, page + 1, miniranks);
            }


            e.setCancelled(true);
        } else if (e.getInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains(ChatColor.DARK_GREEN + "Change Perms") &&
                !e.getView().getTitle().contains("-")) {
            if (e.getCurrentItem().getType().equals(Material.GREEN_STAINED_GLASS_PANE)) {
                // get plugin name from item and open the next gui
                String plugin = ChatColor.stripColor(Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName());
                String rank = Objects.requireNonNull(Objects.requireNonNull(e.getInventory().getItem(44)).getItemMeta()).getLocalizedName();
                Player player = (Player) e.getWhoClicked();
                new permsGuiUtils(player, rank, plugin,1, miniranks);
            }

            int page = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(e.getInventory().getItem(36)).getItemMeta()).getLocalizedName());
            Player player = (Player) e.getWhoClicked();
            String rank = Objects.requireNonNull(Objects.requireNonNull(e.getInventory().getItem(44)).getItemMeta()).getLocalizedName();
            if (e.getRawSlot() == 36 && e.getCurrentItem().getType().equals(Material.ARROW)) {
                new pluginsGuiUtils(player, rank, page - 1, miniranks);
            } else if (e.getRawSlot() == 44 && e.getCurrentItem().getType().equals(Material.ARROW)) {
                new pluginsGuiUtils(player, rank, page + 1, miniranks);
            }



            e.setCancelled(true);
        }else if (e.getInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains(ChatColor.DARK_GREEN + "Change Perms - ")) {
            if (e.getCurrentItem().getType().equals(Material.GREEN_STAINED_GLASS_PANE)) {
                // remove perm
                Player player = (Player) e.getWhoClicked();
               if (player.hasPermission("miniranks.rank.perms.manage")) {
                   String perm = Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getLocalizedName();
                   String rank = Objects.requireNonNull(Objects.requireNonNull(e.getInventory().getItem(44)).getItemMeta()).getLocalizedName();

                   player.sendMessage(ChatColor.GREEN + "Successfully removed " + '"' + perm + '"' + " to " + rank);
                   miniranks.getRankManager().removePerm(perm, rank);
               } else {
                     player.sendMessage(ChatColor.RED + "You do not have permission to remove perms");
               }

            } else if (e.getCurrentItem().getType().equals(Material.RED_STAINED_GLASS_PANE)) {
                // add perm
                Player player = (Player) e.getWhoClicked();
                if (player.hasPermission("miniranks.rank.perms.manage")) {
                    String perm = Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getLocalizedName();
                    String rank = Objects.requireNonNull(Objects.requireNonNull(e.getInventory().getItem(44)).getItemMeta()).getLocalizedName();
                    miniranks.getRankManager().addPerm(perm, rank);
                    player.sendMessage(ChatColor.GREEN + "Successfully added " + '"' + perm + '"' + " to " + rank);
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to add perms");
                }
            }

            int page = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(e.getInventory().getItem(36)).getItemMeta()).getLocalizedName());
            Player player = (Player) e.getWhoClicked();
            String rank = Objects.requireNonNull(Objects.requireNonNull(e.getInventory().getItem(44)).getItemMeta()).getLocalizedName();
            String plugin = e.getView().getTitle().replace(ChatColor.DARK_GREEN + "Change Perms - ", "");
            plugin = ChatColor.stripColor(plugin);
            if (e.getRawSlot() == 36 && e.getCurrentItem().getType().equals(Material.ARROW)) {
                new permsGuiUtils(player, rank, plugin, page - 1, miniranks);
            } else if (e.getRawSlot() == 44 && e.getCurrentItem().getType().equals(Material.ARROW)) {
                new permsGuiUtils(player, rank, plugin, page + 1, miniranks);
            }

            e.setCancelled(true);
        }
    }
}
