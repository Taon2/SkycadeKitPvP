package net.skycade.kitpvp.stat.leaderboards.caching;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.skycade.SkycadeCore.CoreSettings;
import net.skycade.SkycadeCore.utility.MojangUtil;
import net.skycade.kitpvp.stat.KitPvPDB;
import net.skycade.kitpvp.stat.leaderboards.member.StatsMember;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class LeaderboardsCache {

    public static LoadingCache<UUID, StatsMember> memberCache = CacheBuilder.newBuilder().build(new CacheLoader<UUID, StatsMember>() {
        @Override
        public StatsMember load(@Nonnull UUID key) {
            MojangUtil.PlayerData data = MojangUtil.get(key);
            String name = data == null ? "unknown" : data.getName();
            StatsMember member = new StatsMember(key, name);
            try (Connection connection = CoreSettings.getInstance().getConnection()) {
                String sql = "SELECT Kills, Coins, Deaths, HighestStreak FROM " + KitPvPDB.kitPvPTable + " WHERE UUID = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, key.toString());
                    ResultSet result = statement.executeQuery();
                    while (result.next()) {
                        Integer kills = result.getInt("Kills");
                        Integer highestKills  = result.getInt("HighestStreak");
                        Integer deaths  = result.getInt("Deaths");
                        Integer coins  = result.getInt("Coins");
                        member.setKills(kills);
                        member.setHighestStreak(highestKills);
                        member.setDeaths(deaths);
                        member.setCoins(coins);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return member;
        }
    });
    public static StatsMember get(UUID key) {
        return memberCache.getUnchecked(key);
    }

    @SuppressWarnings("unused")
    public static void invalidate(UUID uuid) {
        memberCache.invalidate(uuid);
    }
}
