package me.galaxic.miniranks.listeners;

import me.galaxic.miniranks.Miniranks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class onJoin implements Listener {

    private Miniranks miniranks;

    public onJoin(Miniranks plugin) {
        this.miniranks = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        miniranks.getRankManager().fixTablist(player);

        // make sure the player has the right permissions even if they already have them
        String playerRank = miniranks.getRankManager().getRank(player);
        ArrayList<String> rankPerms = miniranks.getRankManager().getPerms(playerRank);
        if (rankPerms != null) {
            if (player.hasPlayedBefore()) {
                miniranks.getRankManager().addPlayerPerms(player.getUniqueId(), playerRank, rankPerms, false);
                System.out.println("-=-=-=-=-=-");
                System.out.println(miniranks.getRankManager().getPlayerPerms(player.getUniqueId()));
            } else {
                miniranks.getRankManager().addPlayerPerms(player.getUniqueId(), playerRank, rankPerms, true);
            }
        }

        // check if the player is in the database
        // if not, add them to the database
        // if they are, check if their rank is up to date
        // if not, update their rank
        try {
            PreparedStatement ps = miniranks.getDatabase().getConnection().prepareStatement("SELECT * FROM " + miniranks.getTable() + " WHERE UUID = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String rank = rs.getString("PLAYER_RANK");
                int id = rs.getInt("ID");
            } else if (miniranks.getDefaultRank() != null) {
                PreparedStatement ps2 = miniranks.getDatabase().getConnection().prepareStatement("INSERT INTO " + miniranks.getTable() + " (ID, UUID, PLAYER_RANK, NICK) VALUES (default, ?, ?, ?)");
                ps2.setString(1, uuid.toString());
                ps2.setString(2, miniranks.getDefaultRank());
                ps2.setString(3, null);
                ps2.executeUpdate();
            } else {
                PreparedStatement ps2 = miniranks.getDatabase().getConnection().prepareStatement("INSERT INTO " + miniranks.getTable() + " (ID, UUID, PLAYER_RANK, NICK) VALUES (default, ?, ?, ?)");
                ps2.setString(1, uuid.toString());
                ps2.setString(2, null);
                ps2.setString(3, null);
                ps2.executeUpdate();
            }


        } catch (SQLException e1) {
            throw new RuntimeException(e1);
        }
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        miniranks.getNametagManager().removeTag(player);
    }

}
