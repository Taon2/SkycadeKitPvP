package net.skycade.kitpvp.stat.leaderboards.stats;

import net.skycade.SkycadeCore.utility.MojangUtil;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.stat.leaderboards.caching.LeaderboardsCache;
import net.skycade.skycadeleaderboards.leaderboards.StatisticType;

import java.util.Collection;
import java.util.UUID;

public class StatKitPvPKDR extends StatisticType<UUID, Double> {

    private static StatKitPvPKDR instance;

    private StatKitPvPKDR() {
        super("kitpvp-kdr");
    }

    public static StatKitPvPKDR getInstance() {
        if (instance == null)
            instance = new StatKitPvPKDR();
        return instance;
    }

    public void init(Collection<UUID> uuids) {
        update(uuids, true);
    }

    @Override
    public Double get(UUID s) {
        Integer kills = LeaderboardsCache.get(s).getKills();
        Integer deaths = LeaderboardsCache.get(s).getDeaths();

        if (kills == null || deaths == null) {
            return 0D;
        } else {
            Double data = UtilMath.getKDR(kills, deaths);
            return data;
        }
    }

    @Override
    public String keyToString(UUID s) {
        MojangUtil.PlayerData data = MojangUtil.get(s);
        return data == null ? "unknown" : data.getName();
    }

    @Override
    public String valueToString(Double dbl) {
        return Double.toString(dbl);
    }
}
