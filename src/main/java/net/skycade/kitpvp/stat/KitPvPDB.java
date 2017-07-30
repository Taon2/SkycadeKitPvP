package net.skycade.kitpvp.stat;

import com.google.common.base.Joiner;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class KitPvPDB {

    private static KitPvPDB instance;
    private final String kitPvPTable;
    private final String previousNamesTable;
    private final String propertiesTable;
    private final String host;
    private final int port;
    private final String databaseName;
    private final String username;
    private final String password;
    private FileHandler logger = null;
    private BukkitRunnable keepAlive;
    private Connection connection;

    private int transactionId = 1;

    private void log(int transactionId, String msg) {
        if (logger == null) return;
        logger.publish(new LogRecord(Level.FINE, "#" + transactionId + ": " + msg));
    }

    private KitPvPDB() {
        try {
            this.logger = new FileHandler("logs/db.log", true);
            logger.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    StringBuilder builder = new StringBuilder();
                    Throwable ex = record.getThrown();
                    builder.append(new SimpleDateFormat().format(record.getMillis()));
                    builder.append(" [");
                    builder.append(record.getLevel().getLocalizedName().toUpperCase());
                    builder.append("] ");
                    builder.append(this.formatMessage(record));
                    builder.append('\n');
                    if(ex != null) {
                        StringWriter writer = new StringWriter();
                        ex.printStackTrace(new PrintWriter(writer));
                        builder.append(writer);
                    }

                    return builder.toString();
                }
            });
        } catch (IOException e) {
            KitPvP.getInstance().getLogger().log(Level.SEVERE, "An error occurred while trying to create a database log file.", e);
        }


        host = KitPvP.getInstance().getConfig().getString("database.host");
        port = KitPvP.getInstance().getConfig().getInt("database.port");
        databaseName = KitPvP.getInstance().getConfig().getString("database.name");
        username = KitPvP.getInstance().getConfig().getString("database.username");
        password = KitPvP.getInstance().getConfig().getString("database.password");
        kitPvPTable = KitPvP.getInstance().getConfig().getString("database.kitpvp-table");
        previousNamesTable = KitPvP.getInstance().getConfig().getString("database.previous-names-table");
        propertiesTable = KitPvP.getInstance().getConfig().getString("database.properties-table");

        try {
            openConnection();
            keepAlive = new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        Statement statement = connection.createStatement();
                        statement.executeQuery("SELECT 1;");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            keepAlive.runTaskTimerAsynchronously(KitPvP.getInstance(), 10L, 80L);

        } catch (SQLException e) {
            KitPvP.getInstance().getLogger().log(Level.SEVERE, "Coulnd't connect to mysql.", e);
        }
    }

    private synchronized void openConnection() throws SQLException {
        int tid = transactionId;
        ++transactionId;
        connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + databaseName + "?useSSL=true&autoReconnect=true", username, password);
        log(tid, "Connection to database established.");
    }

    public Member getMemberData(UUID uuid) {
        try {
            if (connection == null || connection.isClosed()) openConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + kitPvPTable + " WHERE UUID = ?");
            statement.setString(1, uuid.toString());
            statement.executeQuery();
            ResultSet result = statement.getResultSet();
            if (!result.next()) return null;

            Member member = new Member(uuid, result.getString("PlayerName"));
            member.setDeaths(result.getInt("Deaths"));
            member.setHighestStreak(result.getInt("HighestStreak"));

            PreparedStatement previousNamesStatement = connection.prepareStatement("SELECT * FROM " + previousNamesTable + " WHERE UUID = ?");
            previousNamesStatement.setString(1, uuid.toString());
            ResultSet previousNamesResults = previousNamesStatement.executeQuery();
            while (previousNamesResults.next()) {
                member.addPreviousName(previousNamesResults.getString("PreviousName"));
            }

            PreparedStatement propertiesStatement = connection.prepareStatement("SELECT * FROM " + propertiesTable + " WHERE UUID = ?");
            propertiesStatement.setString(1, uuid.toString());
            ResultSet propertiesResults = propertiesStatement.executeQuery();
            while (propertiesResults.next()) {
                String propertyKey = propertiesResults.getString("PropertyKey");
                String propertyValue = propertiesResults.getString("PropertyValue");
                try {
                    member.getProperties().put(propertyKey, Integer.parseInt(propertyValue));
                } catch (NumberFormatException e1) {
                    try {
                        JSONObject json = (JSONObject) new JSONParser().parse(propertyValue); // {"KIT": {"level": 1}, "SOMETHING": {"level: 2}}
                        Map<String, Map<String, Integer>> subProperties = new HashMap<>(); // final

                        for (Object entry : json.entrySet()) {
                            Map.Entry<String, JSONObject> jsonEntry = (Map.Entry<String, JSONObject>) entry; // "KIT": {"level": 1}
                            String kitName = jsonEntry.getKey(); // "KIT"
                            Map<String, Long> kitMap = jsonEntry.getValue(); // {"level": 1}
                            Map<String, Integer> conversion = new HashMap<>();
                            for (Map.Entry<String, Long> entry2 : kitMap.entrySet()) {
                                conversion.put(entry2.getKey(), entry2.getValue().intValue());
                            }
                            subProperties.put(kitName, conversion);
                        }
                        member.getProperties().put(propertyKey, subProperties);
                    } catch (ParseException e) {
                        member.getProperties().put(propertyKey, propertyValue);
                    }
                }
            }
            MemberManager.getInstance().getMembers().put(uuid, member);
            return member;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet getAllMembers() {
        try {
            if (connection == null || connection.isClosed()) openConnection();
            Statement statement = connection.createStatement();
            return statement.executeQuery("SELECT * FROM " + kitPvPTable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UUID getUUIDForName(String playerName) {
        try {
            if (connection == null || connection.isClosed()) openConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT UUID FROM " + previousNamesTable + " WHERE PlayerName = ?");
            statement.setString(1, playerName);
            ResultSet results = statement.executeQuery();
            if (results.next())
                return UUID.fromString(results.getString(1));
            else return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setMemberDataSync(UUID uuid, String name, List<String> previousNames, Integer kills, Integer highestStreak, Integer death, Map<String, Object> properties) {
        executeUpdate(uuid, name, previousNames, kills, highestStreak, death, properties);
    }

    public void setMemberData(UUID uuid, String name, List<String> previousNames, Integer kills, Integer highestStreak, Integer death, Map<String, Object> properties) {
        Map<String, Object> propertiesCopy = new HashMap<>(properties);
        List<String> previousNamesCopy = new ArrayList<>(previousNames);
        new BukkitRunnable() {
            @Override
            public void run() {
                executeUpdate(uuid, name, previousNamesCopy, kills, highestStreak, death, propertiesCopy);
            }
        }.runTaskAsynchronously(KitPvP.getInstance());
    }

    public Map<String, Object> getHighestKs() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet results = statement.executeQuery("SELECT UUID, PropertyValue FROM " + propertiesTable + " WHERE PropertyKey = 'highest_streak' ORDER BY PropertyValue * 1 DESC LIMIT 0, 1");
        Map<String, Object> map = new HashMap<>();
        if (results.next()) {
            map.put("uuid", results.getString("UUID"));
            map.put("score", Integer.parseInt(results.getString("PropertyValue")));
        }
        return map;
    }

    public synchronized void closeConnection() {
        try {
            connection.close();
            int tid = transactionId;
            ++transactionId;
            log(tid, "Connection to database closed.");
            keepAlive.cancel();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void executeUpdate(UUID uuid, String name, List<String> previousNames, Integer kills, Integer highestStreak, Integer death, Map<String, Object> properties) {
        int tid = ++transactionId;
        log(tid, "Saving data for " + uuid.toString() + ":");
        log(tid, "Name: " + name + ", Previous names: " + Joiner.on(", ").join(previousNames));
        log(tid, "Kills: " + kills + ", Highest streak: " + highestStreak + ", Deaths: " + death);
        log(tid, "Other properties (JSON): " + new JSONObject(properties).toJSONString());
        try {
            if (connection == null || connection.isClosed()) openConnection();
            String query = "INSERT INTO " + kitPvPTable + " (UUID, PlayerName, Kills, HighestStreak, Deaths, KillRatio, CurrentKit, Kits, Coins, Assists, ChosenKit, CrateKeys) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ? ,? ,? ,?) ON DUPLICATE KEY UPDATE PlayerName = VALUES(PlayerName), Kills = VALUES(Kills), HighestStreak = VALUES(HighestStreak), Deaths = VALUES(Deaths), KillRatio = VALUES(KillRatio), CurrentKit = VALUES(CurrentKit), Kits = VALUES(Kits), Coins = VALUES(Coins), Assists = VALUES(Assists), ChosenKit = VALUES(ChosenKit), CrateKeys = VALUES(CrateKeys)";
            log(tid, "Query: " + query);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, uuid.toString());
            statement.setString(2, name);

            statement.setInt(3, kills);
            statement.setInt(4, highestStreak);
            statement.setInt(5, death);

            statement.setFloat(6, (float) UtilMath.getKDR(kills, death));
            statement.setString(7, properties.containsKey("kit") ? (String) properties.get("kit") : "DEFAULT");
            statement.setString(8, new JSONObject((Map<String, Map<String, Integer>>) properties.get("kits")).toJSONString());

            statement.setInt(9, properties.containsKey("coins") ? (int) properties.get("coins"): 0);
            statement.setInt(10, properties.containsKey("assists") ? (int) properties.get("assists") : 0);
            statement.setString(11, properties.containsKey("kit_preference") ? (String) properties.get("kit_preference") : "DEFAULT");
            statement.setInt(12, properties.containsKey("keys") ? (int) properties.get("keys") : 0);

            statement.executeUpdate();
            log(tid, "Executed.");

            for (String previousName : previousNames) {
                PreparedStatement namesStatement = connection.prepareStatement("INSERT INTO " + previousNamesTable + " (PreviousName, UUID) VALUES (?, ?) ON DUPLICATE KEY UPDATE UUID = VALUES(UUID)");
                namesStatement.setString(1, previousName);
                namesStatement.setString(2, uuid.toString());
                namesStatement.executeUpdate();
            }
            log(tid, "Previous names saved.");

            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                PreparedStatement propertiesQuery = connection.prepareStatement("SELECT * FROM " + propertiesTable + " WHERE UUID = ? AND PropertyKey = ?");
                propertiesQuery.setString(1, uuid.toString());
                propertiesQuery.setString(2, entry.getKey());
                ResultSet results = propertiesQuery.executeQuery();
                if (results.next()) {
                    PreparedStatement propertiesStatement = connection.prepareStatement("UPDATE " + propertiesTable + " SET PropertyValue = ? WHERE UUID = ? AND PropertyKey = ?");
                    if (entry.getValue() instanceof Map) {
                        propertiesStatement.setString(1, new JSONObject((Map) entry.getValue()).toJSONString());
                    } else {
                        propertiesStatement.setString(1, entry.getValue().toString());
                    }
                    propertiesStatement.setString(2, uuid.toString());
                    propertiesStatement.setString(3, entry.getKey());
                    propertiesStatement.executeUpdate();
                } else {
                    PreparedStatement propertiesStatement = connection.prepareStatement("INSERT INTO " + propertiesTable + " (UUID, PropertyKey, PropertyValue) VALUES (?, ?, ?)");
                    propertiesStatement.setString(1, uuid.toString());
                    propertiesStatement.setString(2, entry.getKey());
                    if (entry.getValue() instanceof Map) {
                        propertiesStatement.setString(3, new JSONObject((Map) entry.getValue()).toJSONString());
                    } else {
                        propertiesStatement.setString(3, entry.getValue().toString());
                    }
                    propertiesStatement.executeUpdate();
                }
                log(tid, "Property '" + entry.getKey() + "' saved with value: " + entry.getValue().toString() + ".");
            }
        } catch (SQLException e) {
            if (logger != null) {
                LogRecord lr = new LogRecord(Level.SEVERE, "An error occurred for #" + tid + ":");
                lr.setThrown(e);
                logger.publish(lr);
            }
            throw new RuntimeException(e);
        }
    }

    public static KitPvPDB getInstance() {
        if (instance == null) {
            instance = new KitPvPDB();
        }
        return instance;
    }

}
