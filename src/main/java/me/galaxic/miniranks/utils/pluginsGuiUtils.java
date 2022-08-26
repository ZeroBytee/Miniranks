package me.galaxic.miniranks.utils;

import me.galaxic.miniranks.Miniranks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Objects;

public class pluginsGuiUtils {

    public pluginsGuiUtils(Player player, String rank,  int page, Miniranks miniranks) {

        // arraylist for all items in the inventory
        ArrayList<ItemStack> items = new ArrayList<>();

        Inventory players = Bukkit.createInventory(null, 45,   ChatColor.DARK_GREEN +
                ChatColor.translateAlternateColorCodes('&', "Change Perms"));


        int p = 0;
        // for each plugin in the server add a green stained glass pane to the inventory with the plugin name
        for (Plugin plugin : player.getServer().getPluginManager().getPlugins()) {
            ItemStack pluginItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
            ItemMeta pluginMeta = pluginItem.getItemMeta();
            assert pluginMeta != null;
            pluginMeta.setDisplayName(ChatColor.GREEN + plugin.getName());

            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.GREEN + "Click to change permissions");
            pluginMeta.setLore(lore);
            pluginItem.setItemMeta(pluginMeta);

            items.add(pluginItem);
            p++;
        }

        ItemStack left;
        ItemMeta leftMeta;
        if (pageUtil.isPageValid(items, page - 1, 43)) {
            left = new ItemStack(Material.ARROW);
            leftMeta = left.getItemMeta();
            leftMeta.setDisplayName(ChatColor.GREEN + "<< Previous Page");

        } else {
            left = new ItemStack(Material.BARRIER);
            leftMeta = left.getItemMeta();
            leftMeta.setDisplayName(ChatColor.RED + "<< Previous Page");
        }
        leftMeta.setLocalizedName(page + "");
        left.setItemMeta(leftMeta);
        players.setItem(36, left);

        ItemStack right;
        ItemMeta rightMeta;
        if (pageUtil.isPageValid(items, page + 1, 43)) {
            right = new ItemStack(Material.ARROW);
            rightMeta = right.getItemMeta();
            rightMeta.setDisplayName(ChatColor.GREEN + "Next Page >>");
        } else {
            right = new ItemStack(Material.BARRIER);
            rightMeta = right.getItemMeta();
            rightMeta.setDisplayName(ChatColor.RED + "Next Page >>");
        }
        rightMeta.setLocalizedName(rank + "");
        right.setItemMeta(rightMeta);
        players.setItem(44, right);

        for (ItemStack is : pageUtil.getPageItems(items, page, 43)) {
            players.setItem(players.firstEmpty(), is);
        }

        player.openInventory(players);
    }


}


// for (int i = 0; i < list.size(); i++) {
//            int ping = list.get(i).getPing();
//
//            ItemStack pHead = new ItemStack(Material.PLAYER_HEAD, 1);
//            SkullMeta meta = (SkullMeta) pHead.getItemMeta();
//            assert meta != null;
//            meta.setOwningPlayer(list.get(i));
//            meta.setDisplayName(list.get(i).getDisplayName());
//
//            ArrayList<String> lore = new ArrayList<>();
//            lore.add(ChatColor.GRAY + "Ping: " + ChatColor.YELLOW + ping);
//            lore.add(ChatColor.GRAY + "Rank: " + ChatColor.translateAlternateColorCodes('&',
//                    miniranks.getRankManager().getRank(Objects.requireNonNull(list.get(i).getPlayer()))));
//            lore.add(ChatColor.GREEN + "Click To Manage " + list.get(i).getDisplayName());
//            meta.setLore(lore);
//
//            pHead.setItemMeta(meta);
//            players.setItem(i, pHead);
//        }
