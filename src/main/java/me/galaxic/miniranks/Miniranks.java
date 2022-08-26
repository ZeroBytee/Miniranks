package me.galaxic.miniranks;

import me.galaxic.miniranks.commands.playersCommand;
import me.galaxic.miniranks.commands.rankCommand;
import me.galaxic.miniranks.listeners.GuiListener;
import me.galaxic.miniranks.listeners.chatListener;
import me.galaxic.miniranks.listeners.onJoin;
import me.galaxic.miniranks.managers.NametagManager;
import me.galaxic.miniranks.managers.RankManager;
import me.galaxic.miniranks.tabCompleter.rankTab;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public final class Miniranks extends JavaPlugin {

    private Database database;
    private RankManager rankManager;
    private NametagManager nametagManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        database = new Database(this);
        rankManager = new RankManager(this);
        nametagManager = new NametagManager(this);

        try {
            database.connect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (database.isConnected()) {
            System.out.println("[MiniRanks Debug] Connected to database");
            database.createTable();
        } else {
            System.out.println("[MiniRanks Debug] Couldn't connect to database");
        }
        loadCommands();
        Metrics metrics = new Metrics(this, 16048);

        // loop to make sure the database doesn't disconnect
        int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                // select * from table where id = 1
                try {
                    PreparedStatement ps = getDatabase().getConnection().prepareStatement("SELECT * FROM " + getTable() + " WHERE ID = 1");
                    ResultSet rs = ps.executeQuery();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0L, 240L);
    }

    public Database getDatabase() {
        return database;
    }
    public RankManager getRankManager() {
        return rankManager;
    }
    public NametagManager getNametagManager() {
        return nametagManager;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        database.disconnect();
    }

    public void loadCommands() {
        // register join event
        getServer().getPluginManager().registerEvents(new onJoin(this), this);
        getServer().getPluginManager().registerEvents(new chatListener(this), this);
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        // register commands
        Objects.requireNonNull(getCommand("miniranks")).setExecutor(new rankCommand(this));
        // tab complete for rank command
        Objects.requireNonNull(getCommand("miniranks")).setTabCompleter(new rankTab(this));


        Objects.requireNonNull(getCommand("players")).setExecutor(new playersCommand(this));
    }

    // Database getters
    private final String HOST = getConfig().getString("database-settings.host");
    private final String USER = getConfig().getString("database-settings.username");
    private final String PASSWORD = getConfig().getString("database-settings.password");
    private final String DATABASE = getConfig().getString("database-settings.database");
    private final int PORT = getConfig().getInt("database-settings.port");
    private final String TABLE = getConfig().getString("database-settings.table");
    private final String RANK_TABLE = getConfig().getString("database-settings.rank-table");
    private final String defaultRank = getConfig().getString("rank-settings.default-rank");

    public String getDATABASE() {
        return DATABASE;
    }
    public String getHost() {
        return HOST;
    }
    public String getUser() {
        return USER;
    }
    public String getPassword() {
        return PASSWORD;
    }
    public int getPort() {
        return PORT;
    }
    public String getTable() {
        return TABLE;
    }
    public String getRankTable() {
        return RANK_TABLE;
    }
    public String getDefaultRank() {
        return defaultRank;
    }


}



