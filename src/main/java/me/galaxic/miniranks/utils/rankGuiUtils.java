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

import java.util.ArrayList;
import java.util.Objects;

public class rankGuiUtils {

    public rankGuiUtils(Player player, int page, Miniranks miniranks) {

        // arraylist for online players
        ArrayList<Player> list = new ArrayList<Player>(player.getServer().getOnlinePlayers());

        // arraylist for all items in the inventory
        ArrayList<ItemStack> items = new ArrayList<>();

        Inventory players = Bukkit.createInventory(null, 45,   ChatColor.DARK_GREEN +
                ChatColor.translateAlternateColorCodes('&', "&lOnline Players - " + page));
        for (int i = 0; i < list.size(); i++) {
            int ping = list.get(i).getPing();

            ItemStack pHead = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) pHead.getItemMeta();
            assert meta != null;
            meta.setOwningPlayer(list.get(i));
            meta.setDisplayName(list.get(i).getDisplayName());

            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Ping: " + ChatColor.YELLOW + ping);
            lore.add(ChatColor.GRAY + "Rank: " + ChatColor.translateAlternateColorCodes('&',
                    miniranks.getRankManager().getRank(Objects.requireNonNull(list.get(i).getPlayer()))));
            lore.add(ChatColor.GREEN + "Click To Manage " + list.get(i).getDisplayName());
            meta.setLore(lore);

            pHead.setItemMeta(meta);
            players.setItem(i, pHead);
            items.add(pHead);
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
        right.setItemMeta(rightMeta);
        players.setItem(44, right);

        for (ItemStack is : pageUtil.getPageItems(items, page, 43)) {
            players.setItem(players.firstEmpty(), is);
        }

        player.openInventory(players);
    }

    /*
    * // arraylist for online players
        ArrayList<Player> list = new ArrayList<Player>(player.getServer().getOnlinePlayers());

        // arraylist for all items in the inventory
        ArrayList<ItemStack> items = new ArrayList<>();

        Inventory players = Bukkit.createInventory(null, 45,   ChatColor.DARK_GREEN +
                ChatColor.translateAlternateColorCodes('&', "&lOnline Players"));
        for (int i = 0; i < list.size(); i++) {
            int ping = list.get(i).getPing();

            ItemStack pHead = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) pHead.getItemMeta();
            assert meta != null;
            meta.setOwningPlayer(list.get(i));
            meta.setDisplayName(list.get(i).getDisplayName());

            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Ping: " + ChatColor.YELLOW + ping);
            lore.add(ChatColor.GRAY + "Rank: " + miniranks.getRankManager().getRank(Objects.requireNonNull(list.get(i).getPlayer())));
            lore.add(ChatColor.GREEN + "Click To Manage " + list.get(i).getDisplayName());
            meta.setLore(lore);

            pHead.setItemMeta(meta);
            players.setItem(i, pHead);

            items.add(pHead);
        }

        ItemStack backArrow = new ItemStack(Material.ARROW, 1);
        ItemMeta backArrowMeta = backArrow.getItemMeta();
        assert backArrowMeta != null;
        backArrowMeta.setDisplayName(ChatColor.RED + "Previous Page");
        backArrow.setItemMeta(backArrowMeta);
        players.setItem(36, backArrow);

        ItemStack nextArrow = new ItemStack(Material.ARROW, 1);
        ItemMeta nextArrowMeta = nextArrow.getItemMeta();
        assert nextArrowMeta != null;
        nextArrowMeta.setDisplayName(ChatColor.GREEN + "Next Page");
        nextArrow.setItemMeta(nextArrowMeta);
        players.setItem(44, nextArrow);

        player.openInventory(players);
    * */



}
