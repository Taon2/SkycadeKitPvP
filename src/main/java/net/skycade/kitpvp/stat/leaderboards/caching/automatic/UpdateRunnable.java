package net.skycade.kitpvp.stat.leaderboards.caching.automatic;

import net.skycade.kitpvp.stat.leaderboards.caching.LeaderboardsCache;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class UpdateRunnable implements Runnable {

    /*
    Updates the data fed into leaderBoards
     */

    @Override
    public void run() {
        List<UUID> uuidList = new ArrayList<>();
        LeaderboardsCache.killCache.asMap().forEach((uuid, kills) -> {
            uuidList.add(uuid);
        });
        for (UUID u : uuidList) {

            // refresh for each stat
            LeaderboardsCache.killCache.invalidate(u);
            LeaderboardsCache.killCache.getUnchecked(u);

            LeaderboardsCache.coinsCache.invalidate(u);
            LeaderboardsCache.coinsCache.getUnchecked(u);
        }
    }
}
