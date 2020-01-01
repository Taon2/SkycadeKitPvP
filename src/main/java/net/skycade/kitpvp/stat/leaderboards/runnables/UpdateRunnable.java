package net.skycade.kitpvp.stat.leaderboards.runnables;

import net.skycade.kitpvp.stat.leaderboards.caching.LeaderboardsCache;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

class UpdateRunnable extends BukkitRunnable {

    @Override
    public void run() {
//        Set<UUID> uuidSet = LeaderboardsCache.memberCache.asMap().keySet();
//        for (UUID u : uuidSet) {
//            LeaderboardsCache.memberCache.refresh(u);
//        }

        // Runs through online players. Offline players don't need updating.
        Bukkit.getOnlinePlayers().forEach(player -> {
            LeaderboardsCache.memberCache.refresh(player.getUniqueId());
        });
    }
}
