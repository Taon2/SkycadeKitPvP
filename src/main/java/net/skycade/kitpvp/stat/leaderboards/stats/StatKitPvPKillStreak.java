package net.skycade.kitpvp.stat.leaderboards.stats;

import net.skycade.SkycadeCore.utility.MojangUtil;
import net.skycade.kitpvp.stat.leaderboards.caching.LeaderboardsCache;
import net.skycade.skycadeleaderboards.leaderboards.StatisticType;

import java.util.List;
import java.util.UUID;

public class StatKitPvPKillStreak extends StatisticType<UUID, Integer> {

    private static StatKitPvPKillStreak instance;

    private StatKitPvPKillStreak() {
        super("kitpvp-killstreak");
    }

    public static StatKitPvPKillStreak getInstance() {
        if (instance == null)
            instance = new StatKitPvPKillStreak();
        return instance;
    }

    public void init(List<UUID> uuids) {
        update(uuids, true);
    }

    @Override
    public Integer get(UUID s) {
        return LeaderboardsCache.get(s).getHighestStreak();
    }

    @Override
    public String keyToString(UUID s) {
        MojangUtil.PlayerData data = MojangUtil.get(s);
        return data == null ? "unknown" : data.getName();
    }

    @Override
    public String valueToString(Integer integer) {
        return Integer.toString(integer);
    }
}
