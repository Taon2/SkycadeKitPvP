package net.skycade.kitpvp.stat.leaderboards.caching;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.skycade.SkycadeCore.CoreSettings;
import net.skycade.SkycadeCore.utility.MojangUtil;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.stat.KitPvPDB;
import net.skycade.kitpvp.stat.KitPvPStats;
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
            StatsMember member;

            if (memberCache.getIfPresent(key) == null){
                MojangUtil.PlayerData data = MojangUtil.get(key);
                String name = data == null ? "unknown" : data.getName();

                // Creates member
                member = new StatsMember(key, name);

                // Loads information from database if uuid is offline
                try (Connection connection = CoreSettings.getInstance().getConnection()) {
                    String sql = "SELECT Kills, Coins, Deaths, HighestStreak FROM " + KitPvPDB.kitPvPTable + " WHERE UUID = ? AND Instance = ? AND Season = ?";
                    try (PreparedStatement statement = connection.prepareStatement(sql)) {
                        statement.setString(1, key.toString());
                        statement.setString(2, CoreSettings.getInstance().getThisInstance());
                        statement.setString(3, CoreSettings.getInstance().getSeason());
                        ResultSet result = statement.executeQuery();
                        while (result.next()) {
                            Integer kills = result.getInt("Kills");
                            Integer highestKills = result.getInt("HighestStreak");
                            Integer deaths = result.getInt("Deaths");
                            Long coins = result.getLong("Coins");
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

            Member kitpvpMember = MemberManager.getInstance().getMember(key);
            KitPvPStats stats = KitPvP.getInstance().getStats(kitpvpMember);

            // Creates member
            member = new StatsMember(key, kitpvpMember.getName());

            // Loads information from online player if player is online
            member.setKills(stats.getKills());
            member.setHighestStreak(stats.getHighestStreak());
            member.setDeaths(stats.getDeaths());
            member.setCoins(stats.getCoins());

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
