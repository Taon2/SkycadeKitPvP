package net.skycade.kitpvp.stat.leaderboards.stats;

import net.skycade.kitpvp.stat.KitPvPDB;
import net.skycade.kitpvp.stat.leaderboards.caching.LeaderboardsCache;
import net.skycade.skycadeleaderboards.leaderboards.StatisticType;
import org.bukkit.Bukkit;

import java.util.UUID;

public class StatKitPvPCoins extends StatisticType<UUID, Integer> {

    public StatKitPvPCoins() {
        super("kitpvp-coins");
        update(KitPvPDB.getInstance().getAllUUIDs(), true);
    }

    @Override
    public Integer get(UUID s) {
        int coins = LeaderboardsCache.getCoins(s);
        return coins;
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
