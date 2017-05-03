package me.bukkit.kitpvp.coreclasses.utils;

import me.bukkit.kitpvp.KitPvP;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DelayedTeleport extends BukkitRunnable {

    private final Player player;
    private final Location to;
    private final byte delay;

    private Location last;
    private byte tick = 0;

    public DelayedTeleport(Player player, Location to, int secondsDelay) {
        this.player = player;
        this.to = to;
        delay = (byte) secondsDelay;

        runTaskTimer(KitPvP.getInstance(), 20, 20);
        player.sendMessage("§aTeleporting in §6" + delay + " §aseconds.");
        last = player.getLocation();
    }

    @Override
    public void run() {
        if (player == null || !player.isOnline()) {
            cancel();
            return;
        }

        Location current = player.getLocation();

        if (current.getBlockX() != last.getBlockX() || current.getBlockY() != last.getBlockY() || current.getBlockZ() != last.getBlockZ()) {
            player.sendMessage("§cTeleport canceled.");
            cancel();
            onCancel();
            return;
        }
        last = current;

        if (tick++ == delay) {
            player.sendMessage("§aTeleporting...");
            player.teleport(to);
            cancel();
            onTeleport();
        }
    }

    public void onCancel() {}

    public void onTeleport() {}

}