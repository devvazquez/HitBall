package dev.galacticmc.hitball.objects;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.util.Utils;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class Database {

    public final HitBallPlugin plugin;
    public final String host, database, username, password;
    public final int port;

    public Database(HitBallPlugin plugin) {
        this.plugin = plugin;
        ConfigurationSection database = plugin.getConfig().getConfigurationSection("database");
        if (database == null) {
            throw new IllegalArgumentException("Database configuration section is missing");
        }
        this.host = database.getString("address");
        if (this.host == null) {
            throw new IllegalArgumentException("Database host is not configured");
        }
        this.port = database.getInt("port");
        if (this.port == 0) {
            throw new IllegalArgumentException("Database port is not configured");
        }
        this.database = database.getString("database");
        if (this.database == null) {
            throw new IllegalArgumentException("Database name is not configured");
        }
        this.username = database.getString("user");
        if (this.username == null) {
            throw new IllegalArgumentException("Database username is not configured");
        }
        this.password = database.getString("password");
        if (this.password == null) {
            throw new IllegalArgumentException("Database password is not configured");
        }

        // Create stats table
        createTable("Stats", Utils.createMap(
                "Uuid", "VARCHAR(36) PRIMARY KEY",
                "Kills", "INTEGER",
                "Wins", "INTEGER",
                "Losses", "INTEGER",
                "GamesPlayed", "INTEGER"));
        plugin.getLogger().fine("Conectado a la base de datos!");
    }

    private java.sql.Connection openConnection() throws SQLException, ClassNotFoundException {
        //Load driver
        Class.forName("org.mariadb.jdbc.Driver");
        // Connection Configuration
        Properties connConfig = new Properties();
        connConfig.setProperty("user", username);
        connConfig.setProperty("password", password);
        return DriverManager.getConnection(String.format("jdbc:mariadb://%s:%s/%s", host, port, database), connConfig);
    }

    private void createTable(String tableName, HashMap<String, String> nameTypes) {
        // Open connection
        try (java.sql.Connection con = openConnection()) {
            // Prepare the statement
            StringBuilder sql = new StringBuilder(String.format("CREATE TABLE IF NOT EXISTS %s ( ", tableName));
            // For each entry, append to the SQL string
            for (Map.Entry<String, String> nameType : nameTypes.entrySet()) {
                sql.append(nameType.getKey()).append(" ").append(nameType.getValue()).append(", ");
            }
            sql.delete(sql.length() - 2, sql.length());
            sql.append(");");
            PreparedStatement stmt = con.prepareStatement(sql.toString());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private int getStat(UUID playerUUID, String stat) {
        try (Connection con = openConnection()) {
            String query = "SELECT " + stat + " FROM Stats WHERE Uuid = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, playerUUID.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(stat);
            }
            rs.close();
            stmt.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void setStat(UUID playerUUID, String stat, int value) {
        try (Connection con = openConnection()) {
            String query = "INSERT INTO Stats (Uuid, " + stat + ") VALUES (?, ?) ON DUPLICATE KEY UPDATE " + stat + " = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, playerUUID.toString());
            stmt.setInt(2, value);
            stmt.setInt(3, value);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addStat(UUID playerUUID, String stat, int value) {
        try (Connection con = openConnection()) {
            String query = "INSERT INTO Stats (Uuid, " + stat + ") VALUES (?, ?) ON DUPLICATE KEY UPDATE " + stat + " = " + stat + " + ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, playerUUID.toString());
            stmt.setInt(2, value);
            stmt.setInt(3, value);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String getPlayerWithMost(String stat) {
        try (Connection con = openConnection()) {
            String query = "SELECT Uuid, " + stat + " FROM Stats ORDER BY " + stat + " DESC LIMIT 1";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Uuid");
            }
            rs.close();
            stmt.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Getters
    public int getKills(UUID playerUUID) {
        return getStat(playerUUID, "Kills");
    }

    public int getWins(UUID playerUUID) {
        return getStat(playerUUID, "Wins");
    }

    public int getLosses(UUID playerUUID) {
        return getStat(playerUUID, "Losses");
    }

    public int getGamesPlayed(UUID playerUUID) {
        return getStat(playerUUID, "GamesPlayed");
    }

    // Setters
    public void setKills(UUID playerUUID, int kills) {
        setStat(playerUUID, "Kills", kills);
    }

    public void setWins(UUID playerUUID, int wins) {
        setStat(playerUUID, "Wins", wins);
    }

    public void setLosses(UUID playerUUID, int losses) {
        setStat(playerUUID, "Losses", losses);
    }

    public void setGamesPlayed(UUID playerUUID, int gamesPlayed) {
        setStat(playerUUID, "GamesPlayed", gamesPlayed);
    }

    // Adders
    public void addKills(UUID playerUUID, int kills) {
        addStat(playerUUID, "Kills", kills);
    }

    public void addWins(UUID playerUUID, int wins) {
        addStat(playerUUID, "Wins", wins);
    }

    public void addLosses(UUID playerUUID, int losses) {
        addStat(playerUUID, "Losses", losses);
    }

    public void addGamesPlayed(UUID playerUUID, int gamesPlayed) {
        addStat(playerUUID, "GamesPlayed", gamesPlayed);
    }

    // Methods to get the player with the most stats
    public String getPlayerWithMostKills() {
        return getPlayerWithMost("Kills");
    }

    public String getPlayerWithMostWins() {
        return getPlayerWithMost("Wins");
    }

    public String getPlayerWithMostLosses() {
        return getPlayerWithMost("Losses");
    }

    public String getPlayerWithMostGamesPlayed() {
        return getPlayerWithMost("GamesPlayed");
    }

}
