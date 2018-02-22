package net.skycade.kitpvp.stat;

import com.google.common.base.Joiner;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class KitPvPDB {

    private static KitPvPDB instance;
    private final String kitPvPTable;
    private final String previousNamesTable;
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
                    if (ex != null) {
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

        try {
            openConnection();
            keepAlive = new BukkitRunnable() {
                @Override
                public void run() {
                    try (Statement statement = connection.createStatement()) {
                        statement.executeQuery("SELECT 1;");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            keepAlive.runTaskTimerAsynchronously(KitPvP.getInstance(), 10L, 80L);

        } catch (Exception e) {
            KitPvP.getInstance().getLogger().log(Level.SEVERE, "Coulnd't connect to mysql.", e);
        }
    }

    private synchronized void openConnection() {
        int tid = transactionId;
        ++transactionId;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + databaseName + "?useSSL=true&autoReconnect=true", username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        log(tid, "Connection to database established.");
    }

    public Member getMemberData(UUID uuid) {
        String sql = "SELECT * FROM " + kitPvPTable + " WHERE UUID = ?";
        Member member;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            statement.executeQuery();
            ResultSet result = statement.getResultSet();
            if (!result.next()) return null;
            member = new Member(uuid, result.getString("PlayerName"));
            KitPvPStats stats = KitPvP.getInstance().getStats(member);
            stats.setKills(result.getInt("Kills"));
            stats.setHighestStreak(result.getInt("HighestStreak"));
            stats.setDeaths(result.getInt("Deaths"));
            String currentKit = result.getString("CurrentKit");
            stats.setActiveKit(currentKit == null ? KitType.DEFAULT : KitType.valueOf(currentKit));
            try {
                JSONObject kitsJson = (JSONObject) new JSONParser().parse(result.getString("Kits"));
                for (Object o : kitsJson.keySet()) {
                    KitType type = KitType.valueOf((String) o);
                    JSONObject obj = (JSONObject) kitsJson.get(o);
                    Long level = (Long) obj.get("level");
                    KitData kitData = new KitData(type);
                    kitData.setLevel(level.intValue());
                    stats.getKits().put(type, kitData);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            stats.setCoins(result.getInt("Coins"));
            stats.setAssists(result.getInt("Assists"));
            stats.setKitPreference(KitType.valueOf(result.getString("ChosenKit")));
            stats.setCrateKeys(result.getInt("CrateKeys"));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        String previousNamesSql = "SELECT * FROM " + previousNamesTable + " WHERE UUID = ?";
        try (PreparedStatement previousNamesStatement = connection.prepareStatement(previousNamesSql)) {
            previousNamesStatement.setString(1, uuid.toString());
            ResultSet previousNamesResults = previousNamesStatement.executeQuery();
            while (previousNamesResults.next()) {
                member.addPreviousName(previousNamesResults.getString("PreviousName"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        MemberManager.getInstance().getMembers().put(uuid, member);
        return member;
    }

    public ResultSet getAllMembers() {
        Connection connection = getConnection();
        try (Statement statement = connection.createStatement()) {
            return statement.executeQuery("SELECT * FROM " + kitPvPTable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UUID getUUIDForName(String playerName) {
        Connection connection = getConnection();
        String sql = "SELECT UUID FROM " + previousNamesTable + " WHERE PlayerName = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerName);
            ResultSet results = statement.executeQuery();
            if (results.next())
                return UUID.fromString(results.getString(1));
            else return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setMemberDataSync(Member member) {
        executeUpdate(member);
    }

    public void setMemberData(Member member) {
        new BukkitRunnable() {
            @Override
            public void run() {
                executeUpdate(member);
            }
        }.runTaskAsynchronously(KitPvP.getInstance());
    }

    public Map<String, Object> getHighestKs() {
        try (Statement statement = connection.createStatement()) {
            ResultSet results = statement.executeQuery("SELECT UUID, HighestStreak FROM " + kitPvPTable + " ORDER BY HighestStreak * 1 DESC LIMIT 0, 1");
            Map<String, Object> map = new HashMap<>();
            if (results.next()) {
                map.put("uuid", results.getString("UUID"));
                map.put("score", results.getInt("HighestStreak"));
            }
            return map;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

    public synchronized void executeUpdate(Member member) {
        int tid = ++transactionId;
        log(tid, "Saving data for " + member.getUUID().toString() + ":");
        log(tid, "Name: " + member.getName() + ", Previous names: " + Joiner.on(", ").join(member.getPreviousNames()));
        log(tid, "Kills: " + member.getKills() + ", Highest streak: " + member.getHighestStreak() + ", Deaths: " + member.getDeaths());
        Connection connection = getConnection();
        String query = "INSERT INTO " + kitPvPTable + " (UUID, PlayerName, Kills, HighestStreak, Deaths, KillRatio, CurrentKit, Kits, Coins, Assists, ChosenKit, CrateKeys) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ? ,? ,? ,?) ON DUPLICATE KEY UPDATE PlayerName = VALUES(PlayerName), Kills = VALUES(Kills), HighestStreak = VALUES(HighestStreak), Deaths = VALUES(Deaths), KillRatio = VALUES(KillRatio), CurrentKit = VALUES(CurrentKit), Kits = VALUES(Kits), Coins = VALUES(Coins), Assists = VALUES(Assists), ChosenKit = VALUES(ChosenKit), CrateKeys = VALUES(CrateKeys)";
        log(tid, "Query: " + query);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            KitPvPStats stats = KitPvP.getInstance().getStats(member);
            statement.setString(1, member.getUUID().toString());
            statement.setString(2, member.getName());

            statement.setInt(3, member.getKills());
            statement.setInt(4, member.getHighestStreak());
            statement.setInt(5, member.getDeaths());

            statement.setFloat(6, (float) UtilMath.getKDR(member.getKills(), member.getDeaths()));
            statement.setString(7, stats.getActiveKit().name());

            Map<String, Map<String, Integer>> kitMap = new HashMap<>();
            for (Map.Entry<KitType, KitData> entry : stats.getKits().entrySet()) {
                Map<String, Integer> map = new HashMap<>();
                map.put("level", entry.getValue().getLevel());
                kitMap.put(entry.getKey().name(), map);
            }

            statement.setString(8, new JSONObject(kitMap).toJSONString());

            statement.setInt(9, stats.getCoins());
            statement.setInt(10, stats.getAssists());
            statement.setString(11, stats.getKitPreference().name());
            statement.setInt(12, stats.getCrateKeys());

            statement.executeUpdate();
            log(tid, "Executed.");

            for (String previousName : member.getPreviousNames()) {
                String sql = "INSERT INTO " + previousNamesTable + " (PreviousName, UUID) VALUES (?, ?) ON DUPLICATE KEY UPDATE UUID = VALUES(UUID)";
                try (PreparedStatement namesStatement = connection.prepareStatement(sql)) {
                    namesStatement.setString(1, previousName);
                    namesStatement.setString(2, member.getUUID().toString());
                    namesStatement.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            log(tid, "Previous names saved.");
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

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) openConnection();
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
