package me.galaxic.miniranks.utils;

import me.galaxic.miniranks.Miniranks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Objects;

public class permsGuiUtils {

    public permsGuiUtils(Player player, String rank, String pluginName,  int page, Miniranks miniranks) {

        // arraylist for all items in the inventory
        ArrayList<ItemStack> items = new ArrayList<>();

        Inventory players = Bukkit.createInventory(null, 45,   ChatColor.DARK_GREEN +
                ChatColor.translateAlternateColorCodes('&', "Change Perms - " + pluginName));


        int p = 0;

        ArrayList<String> perms = miniranks.getRankManager().getPerms(rank);

        // add a red stained glass pane to the inventory if the rank doesn't have the permission else a green stained glass pane
        for (Permission permission : Objects.requireNonNull(Bukkit.getPluginManager().getPlugin(pluginName)).getDescription().getPermissions()) {

            if (perms != null && perms.contains(permission.getName())) {
                ItemStack pluginItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
                ItemMeta pluginMeta = pluginItem.getItemMeta();
                assert pluginMeta != null;
                pluginMeta.setDisplayName(ChatColor.GREEN + permission.getName());
                ArrayList<String> lore = new ArrayList<>();
                lore.add(ChatColor.RED + "Click to remove permission");
                pluginMeta.setLore(lore);
                pluginMeta.setLocalizedName(permission.getName() + "");
                pluginItem.setItemMeta(pluginMeta);

                items.add(pluginItem);
            } else {
                ItemStack pluginItem = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
                ItemMeta pluginMeta = pluginItem.getItemMeta();
                assert pluginMeta != null;
                pluginMeta.setDisplayName(ChatColor.RED + permission.getName());
                ArrayList<String> lore = new ArrayList<>();
                lore.add(ChatColor.GREEN + "Click to add permission");
                pluginMeta.setLore(lore);
                pluginMeta.setLocalizedName(permission.getName() + "");
                pluginItem.setItemMeta(pluginMeta);

                items.add(pluginItem);
            }
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
