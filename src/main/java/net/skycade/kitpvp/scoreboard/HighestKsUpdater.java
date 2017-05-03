package net.skycade.kitpvp.scoreboard;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.Settings;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.UUID;

public class HighestKsUpdater {

    private final KitPvP plugin;
    private UUID highestKsPlayer;

    public HighestKsUpdater(KitPvP plugin) {
        this.plugin = plugin;
        startUpdating();
    }

    private void startUpdating() {
        int highestKs = -1;
        UUID uuid = null;
        for (Map.Entry<UUID, KitPvPStats> entry : plugin.getStats().entrySet()) {
            KitPvPStats stats = entry.getValue();
            if (stats.getStreak() > highestKs) {
                highestKs = stats.getStreak();
                uuid = entry.getKey();
            }
        }
        if (uuid != null) {
            highestKsPlayer = uuid;
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, this::startUpdating, 20 * Settings.KS_UPDATE_TIME);
    }

    public UUID getHighestKsPlayer() {
        return highestKsPlayer;
    }

}
