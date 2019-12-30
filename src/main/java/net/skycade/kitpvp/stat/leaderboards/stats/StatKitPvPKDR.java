package net.skycade.kitpvp.stat.leaderboards.stats;

import net.skycade.SkycadeCore.utility.MojangUtil;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.stat.leaderboards.caching.LeaderboardsCache;
import net.skycade.skycadeleaderboards.leaderboards.StatisticType;

import java.util.List;
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

    public void init(List<UUID> uuids) {
        update(uuids, true);
    }

    @Override
    public Double get(UUID s) {
        Integer kills = LeaderboardsCache.get(s).getKills();
        Integer deaths = LeaderboardsCache.get(s).getDeaths();

        Double data = UtilMath.getKDR(kills, deaths);
        if (data == null) {
            return 0D;
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
    public String valueToString(Double dbl) {
        return Double.toString(dbl);
    }
}
