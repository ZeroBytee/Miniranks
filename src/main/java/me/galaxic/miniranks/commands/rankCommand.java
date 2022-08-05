package me.galaxic.miniranks.commands;

import me.galaxic.miniranks.Miniranks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import me.galaxic.miniranks.utils.pageUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class rankCommand implements CommandExecutor {

    private Miniranks miniranks;
    public rankCommand(Miniranks plugin) {
        this.miniranks = plugin;
    }

    // TODO: make another table that stores all the ranks and their permissions
    // TODO: if a player is given a rank, the plugin will check if that rank actually exists
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("rank")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args[0].equalsIgnoreCase("give")) {
                    if (args.length == 3) {
                        String playerTarget = args[1];
                        String rank = args[2];
                        if (playerTarget != null) {
                            if (player.hasPermission("miniranks.give")) {
                                if (player.getServer().getPlayer(playerTarget) != null) {
                                    Player target = player.getServer().getPlayer(playerTarget);
                                    UUID tUUID = target.getUniqueId();
                                    // change the players rank inside the database
                                    if (miniranks.getRankManager().checkRank(rank)) {
                                        try {
                                            PreparedStatement ps = miniranks.getDatabase().getConnection().prepareStatement("UPDATE " + miniranks.getTable() + " SET PLAYER_RANK = ? WHERE UUID = ?");
                                            ps.setString(1, rank);
                                            ps.setString(2, tUUID.toString());
                                            ps.executeUpdate();
                                            player.sendMessage(ChatColor.GREEN + "Successfully changed " + target.getName() + "'s rank to " + ChatColor.translateAlternateColorCodes('&', rank));
                                            target.sendMessage(ChatColor.GREEN + "Your rank has been changed to " + ChatColor.translateAlternateColorCodes('&', rank));
                                            miniranks.getRankManager().fixTablist(target);
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.RED + "That rank does not exist");
                                    }

                                }
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "/rank give <player> <rank>");
                    }


                } else if (args[0].equalsIgnoreCase("create")) {
                    if (args.length == 3) {
                        if (player.hasPermission("miniranks.create")) {
                            String rank = args[1];
                            String prefix = args[2];
                            if (prefix != null) {
                                if (!miniranks.getRankManager().checkRank(rank)) {
                                    try {
                                        PreparedStatement ps = miniranks.getDatabase().getConnection().prepareStatement("INSERT INTO " + miniranks.getRankTable() + " (ID, NAME, PREFIX) VALUES (default, ?, ?)");
                                        ps.setString(1, rank);
                                        ps.setString(2, prefix);
                                        ps.executeUpdate();
                                        player.sendMessage(ChatColor.GREEN + "Successfully created rank " + ChatColor.translateAlternateColorCodes('&', rank));
                                    } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "That rank already exists");
                                }
                            }
                        }
                    } else if (args.length == 2) {
                        if (player.hasPermission("miniranks.create")) {
                            String rank = args[1];
                            if (!miniranks.getRankManager().checkRank(rank)) {
                                try {
                                    PreparedStatement ps = miniranks.getDatabase().getConnection().prepareStatement("INSERT INTO " + miniranks.getRankTable() + " (ID, NAME) VALUES (default, ?)");
                                    ps.setString(1, rank);
                                    ps.executeUpdate();
                                    player.sendMessage(ChatColor.GREEN + "Successfully created rank " + ChatColor.translateAlternateColorCodes('&', rank));
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "That rank already exists");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "/rank create <rank> <prefix>");
                        }
                    }

                } else if (args[0].equalsIgnoreCase("list2")) {
                    // make a GUI with all the ranks and their prefixes
                    // for each rank add a glass pane with the color of the rank and the prefix in the lore
                    Inventory inv = Bukkit.createInventory(null, 18, "Ranks");
                    for (String rank : miniranks.getRankManager().getRanks()) {

                        ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
                        ItemMeta meta = item.getItemMeta();
                        assert meta != null;
                        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', rank));

                        try {
                            PreparedStatement ps = miniranks.getDatabase().getConnection().prepareStatement("SELECT * FROM " + miniranks.getRankTable() + " WHERE NAME = ?");
                            ps.setString(1, rank);
                            ResultSet rs = ps.executeQuery();
                            if (rs.next()) {
                                String prefix = rs.getString("PREFIX");
                                String rankName = rs.getString("NAME");

                                ArrayList<String> lore = new ArrayList<>();
                                lore.add(ChatColor.GRAY + "Rank: " + ChatColor.translateAlternateColorCodes('&', rankName));
                                if (prefix != null) {
                                    lore.add("Prefix: " + ChatColor.translateAlternateColorCodes('&', prefix));
                                } else {
                                    lore.add(ChatColor.GRAY + "Prefix: " + ChatColor.RED + "None");
                                }
                                meta.setLore(lore);
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        item.setItemMeta(meta);
                        inv.setItem(inv.firstEmpty(), item);
                    }

                    player.openInventory(inv);

                } else if (args[0].equalsIgnoreCase("delete")) {

                }
            }
        }
        return false;
    }



}
