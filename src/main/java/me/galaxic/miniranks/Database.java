package me.galaxic.miniranks;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import me.galaxic.miniranks.Miniranks;

public class Database {

    private Miniranks miniranks;

    public Database(Miniranks plugin) {
        this.miniranks = plugin;
    }

    private Connection connection;
    public void connect() throws SQLException {
        // Connect to the database
        connection = DriverManager.getConnection("jdbc:mysql://" + miniranks.getHost() + ":" + miniranks.getPort() + "/" + miniranks.getDATABASE() + "?useSSL=false",
                miniranks.getUser(),
                miniranks.getPassword());
    }

    public boolean isConnected() {
        return connection != null;
    }

    public Connection getConnection() {
        return connection;
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // create table if it doesn't exist
    public void createTable() {
        // create the table if it doesn't exist
        // `ID` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
        // `UUID` varchar(36) COLLATE utf8_unicode_ci NOT NULL UNIQUE KEY,
        // `RANK` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
        // `NICK` varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
        // `OP` tinyint(1) NOT NULL
        String sql = "CREATE TABLE IF NOT EXISTS " + miniranks.getTable() + " (" +
                "`ID` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                "`UUID` varchar(36) COLLATE utf8_unicode_ci NOT NULL UNIQUE KEY," +
                "`PLAYER_RANK` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL," +
                "`NICK` varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;";

        // create the rank table if it doesn't exist
        // `ID` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
        // `NAME` varchar(36) COLLATE utf8_unicode_ci NOT NULL UNIQUE KEY,
        // `PREFIX` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL,
        // `WEIGHT` int(11) NOT NULL UNIQUE KEY
        String sqlr = "CREATE TABLE IF NOT EXISTS " + miniranks.getRankTable() + " (" +
                "`ID` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                "`NAME` varchar(36) COLLATE utf8_unicode_ci NOT NULL UNIQUE KEY," +
                "`PREFIX` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL," +
                "`WEIGHT` int(11) NOT NULL UNIQUE KEY" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;";
        try {
            connection.prepareStatement(sql).execute();
            connection.prepareStatement(sqlr).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
