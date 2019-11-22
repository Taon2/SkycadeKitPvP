package net.skycade.kitpvp.stat.leaderboards.stats;

import net.skycade.kitpvp.stat.KitPvPDB;
import net.skycade.kitpvp.stat.leaderboards.caching.LeaderboardsCache;
import net.skycade.skycadeleaderboards.leaderboards.StatisticType;
import org.bukkit.Bukkit;

import java.util.UUID;

public class StatKitPvPKills extends StatisticType<UUID, Integer> {

    public StatKitPvPKills() {
        super("kitpvp-kills");
        update(KitPvPDB.getInstance().getAllUUIDs(), true);
    }

    @Override
    public Integer get(UUID s) {
        int kills = LeaderboardsCache.getKills(s);
        return kills;
    }

    @Override
    public String keyToString(UUID s) {
        return Bukkit.getOfflinePlayer(s).getName();
    }

    @Override
    public String valueToString(Integer integer) {
        return Integer.toString(integer);
    }
}
