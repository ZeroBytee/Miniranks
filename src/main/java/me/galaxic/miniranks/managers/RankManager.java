package me.galaxic.miniranks.managers;

import me.galaxic.miniranks.Miniranks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class RankManager {

    private Miniranks miniranks;

    public RankManager(Miniranks plugin) {
        this.miniranks = plugin;
    }

    private HashMap<UUID, PermissionAttachment> perms = new HashMap<>();


    // RANK SYSTEM
    public boolean checkRank(String rank) {
        try {
            PreparedStatement ps = miniranks.getDatabase().getConnection().prepareStatement("SELECT * FROM " + miniranks.getRankTable() + " WHERE NAME = ?");
            ps.setString(1, rank);
            ResultSet rs = ps.executeQuery();
            // if the rank exists, return true
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void fixTablist(Player player) {
        try {
            PreparedStatement ps = miniranks.getDatabase().getConnection().prepareStatement("SELECT * FROM " + miniranks.getTable() + " WHERE UUID = ?");
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String rank = rs.getString("PLAYER_RANK");
                PreparedStatement psr = miniranks.getDatabase().getConnection().prepareStatement("SELECT * FROM " + miniranks.getRankTable() + " WHERE NAME = ?");
                psr.setString(1, rank);
                ResultSet rsr = psr.executeQuery();
                if (rsr.next()) {
                    String prefix = rsr.getString("PREFIX");
                    if (prefix != null) {
                        miniranks.getNametagManager().setNameTags(player, rank, true, prefix);
                        miniranks.getNametagManager().newTag(player, rank);
                    }
                }


            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getRank(Player player) {
        UUID uuid = player.getUniqueId();
        try {
            PreparedStatement ps = miniranks.getDatabase().getConnection().prepareStatement("SELECT * FROM " + miniranks.getTable() + " WHERE UUID = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String rank = rs.getString("PLAYER_RANK");
                return rank;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;

    }

    // return all ranks from the database
    public ArrayList<String> getRanks() {
        try {
            PreparedStatement ps = miniranks.getDatabase().getConnection().prepareStatement("SELECT * FROM " + miniranks.getRankTable());
            ResultSet rs = ps.executeQuery();
            ArrayList<String> ranksList = new ArrayList<String>();
            while (rs.next()) {
                String rank = rs.getString("NAME");
                ranksList.add(rank);
            }
            return ranksList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setRank(Player player, String rank) {
        // CHANGE THE PLAYER'S RANK TO THE GIVEN RANK
        UUID uuid = player.getUniqueId();
        try {
            PreparedStatement ps = miniranks.getDatabase().getConnection().prepareStatement("UPDATE " + miniranks.getTable() + " SET PLAYER_RANK = ? WHERE UUID = ?");
            ps.setString(1, rank);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();

            // get the prefix for the rank
            PreparedStatement psp = miniranks.getDatabase().getConnection().prepareStatement("SELECT * FROM " + miniranks.getRankTable() + " WHERE NAME = ?");
            psp.setString(1, rank);
            ResultSet rsp = psp.executeQuery();
            if (rsp.next()) {
                String prefix = rsp.getString("PREFIX");
                if (prefix != null) {
                    miniranks.getNametagManager().removeTag(player);
                    miniranks.getNametagManager().setNameTags(player, rank, true, prefix);
                    miniranks.getNametagManager().newTag(player, rank);
                } else {
                    miniranks.getNametagManager().removeTag(player);
                }
            }

            // update the player's permissions
            for (String perm : getPerms(rank)) {
                Permission p = Bukkit.getPluginManager().getPermission(perm);
                addPlayerPerm2(player.getUniqueId(), rank, p, !player.hasPlayedBefore());
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateRank(String rank, String newRank, String newPrefix) {

        try {
            PreparedStatement ps = miniranks.getDatabase().getConnection().prepareStatement("UPDATE " + miniranks.getRankTable() + " SET NAME = ? WHERE NAME = ?");
            ps.setString(1, newRank);
            ps.setString(2, rank);
            ps.executeUpdate();

            PreparedStatement psp = miniranks.getDatabase().getConnection().prepareStatement("UPDATE " + miniranks.getRankTable() + " SET PREFIX = ? WHERE NAME = ?");
            psp.setString(1, newPrefix);
            psp.setString(2, newRank);
            psp.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            if(getRank(target).equals(rank)) {
                setRank(target, newRank);
            }
        }
    }

    public int getRankId(String rank) {
        try {
            PreparedStatement ps = miniranks.getDatabase().getConnection().prepareStatement("SELECT ID FROM " + miniranks.getRankTable() + " WHERE NAME = ?");
            ps.setString(1, rank);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("ID");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }



    // PERMS SYSTEM
    public void addPerms(ArrayList<String> perms, String rank) {
        // get the current perms for the rank
        // add the new perms to the current perms
        // update the perms into the database
        try {
            PreparedStatement ps = miniranks.getDatabase().getConnection().prepareStatement("SELECT * FROM " + miniranks.getRankTable() + " WHERE NAME = ?");
            ps.setString(1, rank);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String currentPerms = rs.getString("PERMS");
                if (currentPerms != null) {
                    String perms_filter1 = currentPerms.replace("[", "");
                    String perms_filter2 = perms_filter1.replace("]", "");
                    String perms_filter3 = perms_filter2.replace(" ", "");
                    ArrayList<String> currentPermsList = new ArrayList<String>(Arrays.asList(perms_filter3.split(",")));
                    currentPermsList.addAll(perms);
                }
            }
            PreparedStatement ps2 = miniranks.getDatabase().getConnection().prepareStatement("UPDATE " + miniranks.getRankTable() + " SET PERMS = ? WHERE NAME = ?");
            ps2.setString(1, perms.toString());
            ps2.setString(2, rank);
            ps2.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removePerms(ArrayList<String> perms, String rank) {
        // get the current perms for the rank
        // remove the new perms from the current perms
        // update the perms into the database
        try {
            PreparedStatement ps = miniranks.getDatabase().getConnection().prepareStatement("SELECT * FROM " + miniranks.getRankTable() + " WHERE NAME = ?");
            ps.setString(1, rank);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String currentPerms = rs.getString("PERMS");
                if (currentPerms != null) {
                    String perms_filter1 = currentPerms.replace("[", "");
                    String perms_filter2 = perms_filter1.replace("]", "");
                    String perms_filter3 = perms_filter2.replace(" ", "");
                    ArrayList<String> currentPermsList = new ArrayList<String>(Arrays.asList(perms_filter3.split(",")));
                    currentPermsList.removeAll(perms);
                }
            }
            PreparedStatement ps2 = miniranks.getDatabase().getConnection().prepareStatement("UPDATE " + miniranks.getRankTable() + " SET PERMS = ? WHERE NAME = ?");
            ps2.setString(1, perms.toString());
            ps2.setString(2, rank);
            ps2.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addPerm(String perm, String rank) {
        // get the current perms for the rank
        // add the new perms to the current perms
        // update the perms into the database
        String perms = null;
        try {
            PreparedStatement ps = miniranks.getDatabase().getConnection().prepareStatement("SELECT * FROM " + miniranks.getRankTable() + " WHERE NAME = ?");
            ps.setString(1, rank);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String currentPerms = rs.getString("PERMS");
                if (currentPerms != null) {
                    String perms_filter1 = currentPerms.replace("[", "");
                    String perms_filter2 = perms_filter1.replace("]", "");
                    String perms_filter3 = perms_filter2.replace(" ", "");
                    ArrayList<String> currentPermsList = new ArrayList<String>(Arrays.asList(perms_filter3.split(",")));
                    currentPermsList.add(perm);
                    perms = currentPermsList.toString();
                } else {

                    ArrayList<String> currentPermsList = new ArrayList<String>();
                    currentPermsList.add(perm);
                    perms = currentPermsList.toString();
                }
            }
            if (perms != null) {
                PreparedStatement ps2 = miniranks.getDatabase().getConnection().prepareStatement("UPDATE " + miniranks.getRankTable() + " SET PERMS = ? WHERE NAME = ?");
                ps2.setString(1, perms);
                ps2.setString(2, rank);
                ps2.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removePerm(String perm, String rank) {
        // get the current perms for the rank
        // add the new perms to the current perms
        // update the perms into the database
        String perms = null;
        try {
            PreparedStatement ps = miniranks.getDatabase().getConnection().prepareStatement("SELECT * FROM " + miniranks.getRankTable() + " WHERE NAME = ?");
            ps.setString(1, rank);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String currentPerms = rs.getString("PERMS");
                if (currentPerms != null) {
                    String perms_filter1 = currentPerms.replace("[", "");
                    String perms_filter2 = perms_filter1.replace("]", "");
                    String perms_filter3 = perms_filter2.replace(" ", "");
                    ArrayList<String> currentPermsList = new ArrayList<String>(Arrays.asList(perms_filter3.split(",")));
                    currentPermsList.remove(perm);
                    perms = currentPermsList.toString();
                }
            }
            if (perms != null) {
                PreparedStatement ps2 = miniranks.getDatabase().getConnection().prepareStatement("UPDATE " + miniranks.getRankTable() + " SET PERMS = ? WHERE NAME = ?");
                ps2.setString(1, perms);
                ps2.setString(2, rank);
                ps2.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<String> getPerms(String rank) {
        // get the perms from the rank table
        try {
            PreparedStatement ps = miniranks.getDatabase().getConnection().prepareStatement("SELECT * FROM " + miniranks.getRankTable() + " WHERE NAME = ?");
            ps.setString(1, rank);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String perms = rs.getString("PERMS");
                if (perms != null) {
                    String permsString_raw = perms.replace("[", "").replace("]", "");
                    String permsString = permsString_raw.replace(" ", "");
                    ArrayList<String> permsList = new ArrayList<String>(Arrays.asList(permsString.split(",")));
                    return permsList;

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    // PLAYER PERMS SYSTEM
    public void addPlayerPerms(UUID uuid, String rank, ArrayList<String> newPerms, boolean isFirstJoin) {
        if (Bukkit.getOfflinePlayer(uuid).isOnline() && !isFirstJoin) {
            ArrayList<Permission> permsToAdd = new ArrayList<Permission>();
            for (String perm : newPerms) {
                Permission p = new Permission(perm);
                permsToAdd.add(p);
            }
            Player player = Bukkit.getPlayer(uuid);
            PermissionAttachment attachment;
            if (perms.containsKey(uuid)) {
                attachment = perms.get(uuid);
            } else {
                assert player != null;
                attachment = player.addAttachment(miniranks);
                perms.put(uuid, attachment);
            }
            for (Permission perm : permsToAdd) {
                assert player != null;
                if (!player.hasPermission(perm)) {
                    attachment.setPermission(perm, true);
                }
            }
        }
    }

    public ArrayList<PermissionAttachmentInfo> getPlayerPerms(UUID uuid) {
        // get the perms from the player
        ArrayList<PermissionAttachmentInfo> perms = new ArrayList<PermissionAttachmentInfo>();
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
                perms.add(perm);
            }
            return perms;
        }
        return null;

    }

    public void removePlayerPerms(UUID uuid, String rank, ArrayList<String> newPerms) {
        if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
            ArrayList<Permission> permsToRemove = new ArrayList<Permission>();
            for (String perm : newPerms) {
                Permission permission = Bukkit.getServer().getPluginManager().getPermission(perm);
                permsToRemove.add(permission);
            }
            Player player = Bukkit.getPlayer(uuid);
            PermissionAttachment attachment;
            if (perms.containsKey(uuid)) {
                attachment = perms.get(uuid);
            } else {
                assert player != null;
                attachment = player.addAttachment(miniranks);
                perms.put(uuid, attachment);
            }
            for (Permission perm : permsToRemove) {
                assert player != null;
                if (player.hasPermission(perm)) {
                    attachment.setPermission(perm, false);
                }
            }
        }
    }

    public void addPlayerStringPerm(UUID uuid, String rank, String perm, boolean isFirstJoin) {
        if (Bukkit.getOfflinePlayer(uuid).isOnline() && !isFirstJoin) {
            Player player = Bukkit.getPlayer(uuid);
            PermissionAttachment attachment;
            if (perms.containsKey(uuid)) {
                attachment = perms.get(uuid);
            } else {
                assert player != null;
                attachment = player.addAttachment(miniranks);
                perms.put(uuid, attachment);
            }
            assert player != null;
            Permission perm2 = Bukkit.getServer().getPluginManager().getPermission(perm);
            if (!player.hasPermission(perm2)) {
                attachment.setPermission(perm2, true);
            }
        }
    }

    public void addPlayerPerm2(UUID uuid, String rank, Permission perm, boolean isFirstJoin) {
        if (Bukkit.getOfflinePlayer(uuid).isOnline() && !isFirstJoin) {
            Player player = Bukkit.getPlayer(uuid);
            PermissionAttachment attachment;
            if (perms.containsKey(uuid)) {
                attachment = perms.get(uuid);
            } else {
                assert player != null;
                attachment = player.addAttachment(miniranks);
                perms.put(uuid, attachment);
            }
            assert player != null;
            attachment.setPermission(perm, true);
        }
    }

    public void removePlayerPerm(UUID uuid, String rank, String perm) {
        if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
            Player player = Bukkit.getPlayer(uuid);
            PermissionAttachment attachment;
            if (perms.containsKey(uuid)) {
                attachment = perms.get(uuid);
            } else {
                assert player != null;
                attachment = player.addAttachment(miniranks);
                perms.put(uuid, attachment);
            }
            assert player != null;
            if (player.hasPermission(perm)) {
                attachment.setPermission(perm, false);
            }
        }
    }

    // MISC
    public void fixPerms(UUID uuid) {
        perms.remove(uuid);
    }


}