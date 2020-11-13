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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class KitPvPDB {

    private static SkycadeCorePlugin plugin;

    static {
        plugin = JavaPlugin.getPlugin(SkycadeCorePlugin.class);
    }

    private static KitPvPDB instance;
    public static String kitPvPTable;

    private KitPvPDB() {
        kitPvPTable = KitPvP.getInstance().getDatabaseManager().get().getString("database.kitpvp-table");
    }

    public Member getMemberData(UUID uuid) {
        String sql = "SELECT * FROM " + kitPvPTable + " WHERE UUID = ? AND Instance = ? AND Season = ?";
        Member member;
        try (Connection connection = CoreSettings.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                statement.setString(2, CoreSettings.getInstance().getThisInstance());
                statement.setString(3, CoreSettings.getInstance().getSeason());
                statement.executeQuery();
                ResultSet result = statement.getResultSet();
                if (!result.next()) return null;
                member = new Member(uuid, result.getString("PlayerName"));
                KitPvPStats stats = KitPvP.getInstance().getStats(member);
                stats.setKills(result.getInt("Kills"));
                stats.setHighestStreak(result.getInt("HighestStreak"));
                stats.setDeaths(result.getInt("Deaths"));
                String currentKit = result.getString("CurrentKit");
                stats.setActiveKit(currentKit == null ? KitType.CHANCE : KitType.getTypeFromString(currentKit));
                try {
                    JSONObject kitsJson = (JSONObject) new JSONParser().parse(result.getString("Kits"));
                    for (Object o : kitsJson.keySet()) {
                        KitType type = KitType.getTypeFromString((String) o);
                        stats.addKit(type);
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                stats.setCoins(result.getLong("Coins"));
                stats.setEventTokens(result.getInt("EventCoins"));
                stats.setAssists(result.getInt("Assists"));
                stats.setAbilityToggle(result.getBoolean("AbilityToggle"));
                stats.setKitPreference(KitType.getTypeFromString(result.getString("ChosenKit")));
                stats.setPrestigeLevel(result.getInt("PrestigeLevel"));
            }

            MemberManager.getInstance().getMembers().put(uuid, member);
            return member;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<UUID> getAllUUIDs() {
        List<UUID> uuidList = new ArrayList<>();
        String sql = "SELECT UUID FROM " + kitPvPTable + " WHERE Instance = ? AND Season = ?";

        try (Connection connection = CoreSettings.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, CoreSettings.getInstance().getThisInstance());
                statement.setString(2, CoreSettings.getInstance().getSeason());
                statement.executeQuery();
                ResultSet resultSet = statement.getResultSet();
                while (resultSet.next()) {
                    String uuidString = resultSet.getString("UUID");
                    uuidList.add(UUID.fromString(uuidString));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return uuidList;
    }

    public Set<UUID> getTopUuids() {
        Set<UUID> uuids = new HashSet<>();

        try (Connection connection = CoreSettings.getInstance().getConnection()) {
            String sql = "SELECT UUID FROM skycade_kitpvp_members WHERE Instance = ? AND Season = ? " +
                    "ORDER BY %s DESC LIMIT 50";
            for (String col : new String[]{"Kills", "Deaths", "HighestStreak", "KillRatio", "Coins"}) {
                try (PreparedStatement statement = connection.prepareStatement(String.format(sql, col))) {
                    statement.setString(1, CoreSettings.getInstance().getThisInstance());
                    statement.setString(2, CoreSettings.getInstance().getSeason());

                    ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        uuids.add(UUID.fromString(resultSet.getString("UUID")));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return uuids;
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
        String sql = "SELECT UUID, HighestStreak FROM " + kitPvPTable + " ORDER BY HighestStreak * 1 DESC LIMIT 0, 1 WHERE Instance = ? AND Season = ?";

        try (Connection connection = CoreSettings.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, CoreSettings.getInstance().getThisInstance());
                statement.setString(2, CoreSettings.getInstance().getSeason());
                statement.executeQuery();
                ResultSet resultSet = statement.getResultSet();
                Map<String, Object> map = new HashMap<>();
                if (resultSet.next()) {
                    map.put("uuid", resultSet.getString("UUID"));
                    map.put("score", resultSet.getInt("HighestStreak"));
                }
                return map;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void executeUpdate(Member member) {
        try (Connection connection = CoreSettings.getInstance().getConnection()) {
            String query = "INSERT INTO " + kitPvPTable + " (UUID, PlayerName, Kills, HighestStreak, Deaths, KillRatio, CurrentKit, Kits, Coins, EventCoins, Assists, ChosenKit, PrestigeLevel, AbilityToggle, Instance, Season) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,? ,? ,?, ?) " +
                    "ON DUPLICATE KEY UPDATE PlayerName = VALUES(PlayerName), Kills = VALUES(Kills), HighestStreak = VALUES(HighestStreak), Deaths = VALUES(Deaths), KillRatio = VALUES(KillRatio), CurrentKit = VALUES(CurrentKit), Kits = VALUES(Kits), " +
                    "Coins = VALUES(Coins), EventCoins = VALUES(EventCoins), Assists = VALUES(Assists), ChosenKit = VALUES(ChosenKit), PrestigeLevel = VALUES(PrestigeLevel), AbilityToggle = VALUES(AbilityToggle)";
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
                    map.put("level", 1);
                    kitMap.put(entry.getKey().name(), map);
                }

                statement.setString(8, new JSONObject(kitMap).toJSONString());

                statement.setLong(9, stats.getCoins());
                statement.setInt(10, stats.getEventTokens());
                statement.setInt(11, stats.getAssists());
                statement.setString(12, stats.getKitPreference().name());
                statement.setInt(13, stats.getPrestigeLevel());
                statement.setBoolean(14, stats.isAbilityToggle());
                statement.setString(15, CoreSettings.getInstance().getThisInstance());
                statement.setString(16, CoreSettings.getInstance().getSeason());

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
