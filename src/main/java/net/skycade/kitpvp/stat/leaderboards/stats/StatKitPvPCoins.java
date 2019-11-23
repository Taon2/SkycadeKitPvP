package net.skycade.kitpvp.stat.leaderboards.stats;

import net.skycade.SkycadeCore.utility.MojangUtil;
import net.skycade.kitpvp.stat.leaderboards.caching.LeaderboardsCache;
import net.skycade.skycadeleaderboards.leaderboards.StatisticType;

import java.util.Collection;
import java.util.UUID;

public class StatKitPvPCoins extends StatisticType<UUID, Integer> {

    private static StatKitPvPCoins instance;

    private StatKitPvPCoins() {
        super("kitpvp-coins");
    }

    public static StatKitPvPCoins getInstance() {
        if (instance == null)
            instance = new StatKitPvPCoins();
        return instance;
    }

    public void init(Collection<UUID> uuids) {
        update(uuids, true);
    }

    @Override
    public Integer get(UUID s) {
        return LeaderboardsCache.getCoins(s);
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
