package me.galaxic.miniranks.managers;

import me.galaxic.miniranks.Miniranks;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class RankManager {

    private Miniranks miniranks;
    public RankManager(Miniranks plugin) {
        this.miniranks = plugin;
    }

    public boolean checkRank(String rank) {
        try {
            PreparedStatement ps = miniranks.getDatabase().getConnection().prepareStatement("SELECT * FROM " + miniranks.getRankTable() + " WHERE NAME = ?");
            ps.setString(1, rank);
            ResultSet rs = ps.executeQuery();
            // if the rank exists, return true
if          (rs.next()) {
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
                    miniranks.getNametagManager().setNameTags(player, rank, true, prefix);
                    miniranks.getNametagManager().newTag(player, rank);
                }
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




}
