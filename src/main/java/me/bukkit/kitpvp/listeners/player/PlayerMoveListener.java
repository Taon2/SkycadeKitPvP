package me.bukkit.kitpvp.listeners.player;

import me.bukkit.kitpvp.KitPvP;
import me.bukkit.kitpvp.coreclasses.utils.UtilPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;

import java.util.stream.Collectors;

public class PlayerMoveListener implements Listener {

    private final KitPvP plugin;

    public PlayerMoveListener(KitPvP plugin) {
        this.plugin = plugin;
        startLilypadEffect();
        startKitMove();
    }

    // Might cause lagg.
    private void startLilypadEffect() {
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getLocation().getBlock().getType() == Material.WATER_LILY && UtilPlayer.isMoving(p)).collect(Collectors.toList())
                .forEach(p -> p.setVelocity(new org.bukkit.util.Vector(p.getLocation().getDirection().getX(), 1.3,
                        p.getLocation().getDirection().getZ())));
        Bukkit.getScheduler().runTaskLater(plugin, this::startLilypadEffect, 5);
    }

    private void startKitMove() {
        Bukkit.getOnlinePlayers().stream()
                .filter(UtilPlayer::isMoving)
                .collect(Collectors.toList()).forEach(p -> plugin.getStats(p).getActiveKit().getKit().onMove(p));
        Bukkit.getScheduler().runTaskLater(plugin, this::startKitMove, 10);
    }
}
