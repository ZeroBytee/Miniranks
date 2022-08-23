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
import me.galaxic.miniranks.utils.pluginsGuiUtils;
import me.galaxic.miniranks.utils.permsGuiUtils;

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
        if (command.getName().equalsIgnoreCase("miniranks")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 0) {
                    player.sendMessage(ChatColor.RED + "wrong command format, try /rank help");
                } else {
                    if (args[0].equalsIgnoreCase("set")) {
                        if (args.length == 3) {
                            String playerTarget = args[1];
                            String rank = args[2];
                            if (playerTarget != null) {
                                if (player.hasPermission("miniranks.rank.set")) {
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
                                } else {
                                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                                }
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "/rank give <player> <rank>");
                        }


                    } else if (args[0].equalsIgnoreCase("create")) {
                        if (args.length == 3) {
                            if (player.hasPermission("miniranks.rank.create")) {
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
                            } else {
                                player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                            }
                        } else if (args.length == 2) {
                            if (player.hasPermission("miniranks.rank.create")) {
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

                    } else if (args[0].equalsIgnoreCase("delete")) {
                        if (player.hasPermission("miniranks.rank.delete")) {
                            String rank = args[1];
                            if (miniranks.getRankManager().checkRank(rank)) {
                                // delete the rank from the database
                                try {
                                    PreparedStatement ps = miniranks.getDatabase().getConnection().prepareStatement("DELETE FROM " + miniranks.getRankTable() + " WHERE NAME = ?");
                                    ps.setString(1, rank);
                                    ps.executeUpdate();
                                    player.sendMessage(ChatColor.GREEN + "Successfully deleted rank " + ChatColor.translateAlternateColorCodes('&', rank));
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "This rank does not exist");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                        }

                    } else if (args[0].equalsIgnoreCase("perms")) {
                        if (args.length == 2) {
                            String rank = args[1];
                            new pluginsGuiUtils(player, rank, 1, miniranks);
                        }
                    } else if (args[0].equalsIgnoreCase("help")) {
                        player.sendMessage(ChatColor.GREEN + "Miniranks Help");
                        player.sendMessage(ChatColor.GREEN + "/rank set <player> <rank>");
                        player.sendMessage(ChatColor.GREEN + "/rank create <rank> <prefix>");
                        player.sendMessage(ChatColor.GREEN + "/rank delete <rank>");
                        player.sendMessage(ChatColor.GREEN + "/rank perms <rank>");
                        player.sendMessage(ChatColor.GREEN + "/players");
                    } else {
                        player.sendMessage(ChatColor.RED + "wrong command format, try /rank help");
                    }
                }

            }
        }
        return false;
    }



}
