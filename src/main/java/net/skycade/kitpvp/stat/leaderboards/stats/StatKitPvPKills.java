package net.skycade.kitpvp.stat.leaderboards.stats;

import net.skycade.SkycadeCore.utility.MojangUtil;
import net.skycade.kitpvp.stat.leaderboards.caching.LeaderboardsCache;
import net.skycade.skycadeleaderboards.leaderboards.StatisticType;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class StatKitPvPKills extends StatisticType<UUID, Integer> {

    private static StatKitPvPKills instance;

    private StatKitPvPKills() {
        super("kitpvp-kills");
    }

    public static StatKitPvPKills getInstance() {
        if (instance == null)
            instance = new StatKitPvPKills();
        return instance;
    }

    public void init(Collection<UUID> uuids) {
        update(uuids, true);
    }

    @Override
    public Integer get(UUID s) {
        Integer data = LeaderboardsCache.get(s).getKills();
        if (data == null) {
            return 0;
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
    public String valueToString(Integer integer) {
        return Integer.toString(integer);
    }
}
