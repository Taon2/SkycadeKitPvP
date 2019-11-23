package net.skycade.kitpvp.stat.leaderboards.caching;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.skycade.SkycadeCore.CoreSettings;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.stat.KitPvPDB;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class LeaderboardsCache {

    public static LoadingCache<UUID, Integer> killCache = CacheBuilder.newBuilder().build(new CacheLoader<UUID, Integer>() {
        @Override
        public Integer load(@Nonnull UUID key) {
            int kills = 0;
            try (Connection connection = CoreSettings.getInstance().getConnection()) {
                String sql = "SELECT Kills FROM " + KitPvPDB.kitPvPTable + " WHERE UUID = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, key.toString());
                    ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        kills = resultSet.getInt("Kills");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return kills;
        }
    });

    public static LoadingCache<UUID, Integer> coinsCache = CacheBuilder.newBuilder().build(new CacheLoader<UUID, Integer>() {
        @Override
        public Integer load(@Nonnull UUID key) {
            int coins = 0;
            try (Connection connection = CoreSettings.getInstance().getConnection()) {
                String sql = "SELECT Coins FROM " + KitPvPDB.kitPvPTable + " WHERE UUID = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, key.toString());
                    ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        coins = resultSet.getInt("Coins");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return coins;
        }
    });

    public static Integer getKills(UUID key) {
        return killCache.getUnchecked(key);
    }

    public static void invalidateKills(UUID uuid) {
        killCache.invalidate(uuid);
    }

    public static void persistKills(UUID key) {
        KitPvPDB.getInstance().setMemberData(MemberManager.getInstance().getMember(key));
    }

    public static Integer getCoins(UUID key) {
        return coinsCache.getUnchecked(key);
    }

    public static void invalidateCoins(UUID uuid) {
        coinsCache.invalidate(uuid);
    }

    public static void persistCoins(UUID key) {
        KitPvPDB.getInstance().setMemberData(MemberManager.getInstance().getMember(key));
    }
}
