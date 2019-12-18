package net.skycade.kitpvp.stat;

import net.skycade.SkycadeCore.CoreSettings;
import net.skycade.SkycadeCore.SkycadeCorePlugin;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import redis.clients.jedis.Jedis;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KitPvPDB {

    private static SkycadeCorePlugin plugin;

    static {
        plugin = JavaPlugin.getPlugin(SkycadeCorePlugin.class);
    }

    private static KitPvPDB instance;
    private final String kitPvPTable;

    private KitPvPDB() {
        kitPvPTable = KitPvP.getInstance().getConfig().getString("database.kitpvp-table");
    }

    public Member getMemberData(UUID uuid) {
        String sql = "SELECT * FROM " + kitPvPTable + " WHERE UUID = ?";
        Member member;
        try (Connection connection = CoreSettings.getInstance().getConnection()) {
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
                    throw new RuntimeException(e);
                }

                stats.setCoins(result.getInt("Coins"));
                stats.setEventCoins(result.getInt("EventCoins"));
                stats.setAssists(result.getInt("Assists"));
                stats.setKitPreference(KitType.valueOf(result.getString("ChosenKit")));
                stats.setCrateKeys(result.getInt("CrateKeys"));
            }

            MemberManager.getInstance().getMembers().put(uuid, member);
            return member;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet getAllMembers() {
        try (Connection connection = CoreSettings.getInstance().getConnection()) {
            try (Statement statement = connection.createStatement()) {
                return statement.executeQuery("SELECT * FROM " + kitPvPTable);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UUID getUUIDForName(String playerName) {
        try (Jedis jedis = plugin.getJedis()) {
            String uuid = jedis.hget("uuidCache", playerName.toLowerCase());
            if (uuid == null) return null;
            return UUID.fromString(uuid);
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
        try (Connection connection = CoreSettings.getInstance().getConnection()) {
            try (Statement statement = connection.createStatement()) {
                ResultSet results = statement.executeQuery("SELECT UUID, HighestStreak FROM " + kitPvPTable + " ORDER BY HighestStreak * 1 DESC LIMIT 0, 1");
                Map<String, Object> map = new HashMap<>();
                if (results.next()) {
                    map.put("uuid", results.getString("UUID"));
                    map.put("score", results.getInt("HighestStreak"));
                }
                return map;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void executeUpdate(Member member) {

        try (Connection connection = CoreSettings.getInstance().getConnection()) {
            String query = "INSERT INTO " + kitPvPTable + " (UUID, PlayerName, Kills, HighestStreak, Deaths, KillRatio, CurrentKit, Kits, Coins, EventCoins, Assists, ChosenKit, CrateKeys) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ? ,? ,? ,?, ?) ON DUPLICATE KEY UPDATE PlayerName = VALUES(PlayerName), Kills = GREATEST(Kills, VALUES(Kills)), HighestStreak = GREATEST(HighestStreak, VALUES(HighestStreak)), Deaths = GREATEST(Deaths, VALUES(Deaths)), KillRatio = VALUES(KillRatio), CurrentKit = VALUES(CurrentKit), Kits = VALUES(Kits), Coins = VALUES(Coins), EventCoins = VALUES(EventCoins), Assists = VALUES(Assists), ChosenKit = VALUES(ChosenKit), CrateKeys = VALUES(CrateKeys)";
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
                statement.setInt(10, stats.getEventCoins());
                statement.setInt(11, stats.getAssists());
                statement.setString(12, stats.getKitPreference().name());
                statement.setInt(13, stats.getCrateKeys());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
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
