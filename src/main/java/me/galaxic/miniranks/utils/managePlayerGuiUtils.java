package me.galaxic.miniranks.utils;

import me.galaxic.miniranks.Miniranks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class managePlayerGuiUtils {

    public managePlayerGuiUtils(Player player, Player target, int page, Miniranks miniranks) {

        Inventory gui = Bukkit.createInventory(null, 9, ChatColor.GREEN + "Change rank - " + target.getName());

        List<ItemStack> allItems = new ArrayList<>();
        ArrayList<String> ranks = miniranks.getRankManager().getRanks();

        for (int i = 0; i < ranks.size(); i++) {
            ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ranks.get(i)));
            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Rank: " + ChatColor.translateAlternateColorCodes('&', ranks.get(i)));
            String rank = ranks.get(i);
            try {
                PreparedStatement ps = miniranks.getDatabase().getConnection().prepareStatement("SELECT * FROM " + miniranks.getRankTable() + " WHERE NAME = ?");
                ps.setString(1, rank);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String prefix = rs.getString("PREFIX");
                    String rankName = rs.getString("NAME");

                    if (prefix != null) {
                        lore.add(ChatColor.GRAY + "Prefix: " + ChatColor.translateAlternateColorCodes('&', prefix));
                    } else {
                        lore.add(ChatColor.GRAY + "Prefix: " + ChatColor.RED + "None");
                    }
                    meta.setLore(lore);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            meta.setLocalizedName(rank + "");
            meta.setLore(lore);
            item.setItemMeta(meta);

            allItems.add(item);
        }


        ItemStack left;
        ItemMeta leftMeta;
        if (pageUtil.isPageValid(allItems, page - 1, 7)) {
            left = new ItemStack(Material.ARROW, 1);
            leftMeta = left.getItemMeta();
            assert leftMeta != null;
            leftMeta.setDisplayName(ChatColor.GREEN + "<< Previous Page");
        } else {
            left = new ItemStack(Material.BARRIER, 1);
            leftMeta = left.getItemMeta();
            assert leftMeta != null;
            leftMeta.setDisplayName(ChatColor.RED + "<< Previous Page");
        }
        leftMeta.setLocalizedName(page + "");
        left.setItemMeta(leftMeta);
        gui.setItem(0, left);

        ItemStack right;
        ItemMeta rightMeta;
        if (pageUtil.isPageValid(allItems, page + 1, 7)) {
            right = new ItemStack(Material.ARROW, 1);
            rightMeta = right.getItemMeta();
            assert rightMeta != null;
            rightMeta.setDisplayName(ChatColor.GREEN + "Next Page >>");
        } else {
            right = new ItemStack(Material.BARRIER, 1);
            rightMeta = right.getItemMeta();
            assert rightMeta != null;
            rightMeta.setDisplayName(ChatColor.RED + "Next Page >>");
        }
        rightMeta.setLocalizedName(target.getDisplayName() + "");
        right.setItemMeta(rightMeta);
        gui.setItem(8, right);

        for (ItemStack is : pageUtil.getPageItems(allItems, page, 7)) {
            gui.addItem(is);
        }

        player.openInventory(gui);
    }

}
