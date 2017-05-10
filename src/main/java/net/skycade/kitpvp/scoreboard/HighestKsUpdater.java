package net.skycade.kitpvp.scoreboard;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.stat.KitPvPDB;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.UUID;

public class HighestKsUpdater {

    private final KitPvP plugin;
    private UUID highestKsPlayer;
    private boolean lock = false;

    public HighestKsUpdater(KitPvP plugin) {
        this.plugin = plugin;
        startUpdating();
    }

    private void startUpdating() {
        if (lock) return;
        lock = true;
        try {
            UUID uuid = KitPvPDB.getInstance().getHighestKsPlayer();
            if (uuid != null) {
                highestKsPlayer = uuid;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            lock = false;
        }

        /*int highestKs = -1;
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
        }*/
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, this::startUpdating, 20 * KitPvP.getInstance().getConfig().getInt("ks-update-time"));
    }

    public UUID getHighestKsPlayer() {
        return highestKsPlayer;
    }

}
