package net.skycade.kitpvp.stat.leaderboards.stats;

import net.skycade.SkycadeCore.utility.MojangUtil;
import net.skycade.kitpvp.stat.leaderboards.caching.LeaderboardsCache;
import net.skycade.skycadeleaderboards.leaderboards.StatisticType;

import java.util.List;
import java.util.UUID;

public class StatKitPvPCoins extends StatisticType<UUID, Long> {

    private static StatKitPvPCoins instance;

    private StatKitPvPCoins() {
        super("kitpvp-coins");
    }

    public static StatKitPvPCoins getInstance() {
        if (instance == null)
            instance = new StatKitPvPCoins();
        return instance;
    }

    public void init(List<UUID> uuids) {
        update(uuids, true);
    }

    @Override
    public Long get(UUID s) {
        Long data = LeaderboardsCache.get(s).getCoins();
        if (data == null) {
            return 0L;
        } else {
            return data;
        }
    }

    @Override
    public String keyToString(UUID s) {
        MojangUtil.PlayerData data = MojangUtil.get(s);
        return data == null ? "unknown" : data.getName();
    }

    @Override
    public String valueToString(Long lng) {
        return Long.toString(lng);
    }
}
