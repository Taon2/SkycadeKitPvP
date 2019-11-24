package net.skycade.kitpvp.stat.leaderboards.runnables;

import net.skycade.kitpvp.stat.leaderboards.caching.LeaderboardsCache;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.UUID;

class UpdateRunnable extends BukkitRunnable {

    @Override
    public void run() {
        Set<UUID> uuidSet = LeaderboardsCache.memberCache.asMap().keySet();
        for (UUID u : uuidSet) {
            LeaderboardsCache.memberCache.refresh(u);
        }
    }
}
