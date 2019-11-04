package net.skycade.kitpvp.listeners.player;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class PlayerMoveListener implements Listener {

    private final KitPvP plugin;
    private List<Location> teleports = new ArrayList<>();

    public PlayerMoveListener(KitPvP plugin) {
        this.plugin = plugin;

        ConfigurationSection main = plugin.getConfig().getConfigurationSection("teleport-locations");
        for (String handle : main.getKeys(false)) {
            teleports.add((Location) main.get(handle));
        }

        startLilypadEffect();
        startPressurePadEffect();
    }

    private void startLilypadEffect() {
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> (p.getLocation().getBlock().getType() == Material.WATER_LILY)
                                && UtilPlayer.isMoving(p)).collect(Collectors.toList())
                .forEach(p -> p.setVelocity(new org.bukkit.util.Vector(p.getLocation().getDirection().getX(), 1.3,
                        p.getLocation().getDirection().getZ())));
        Bukkit.getScheduler().runTaskLater(plugin, this::startLilypadEffect, 5);
    }

    private void startPressurePadEffect() {
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> (p.getLocation().getBlock().getType() == Material.GOLD_PLATE)
                        && UtilPlayer.isMoving(p)).collect(Collectors.toList())
                .forEach(p -> p.teleport(teleports.get(ThreadLocalRandom.current().nextInt(0, teleports.size()))));
        Bukkit.getScheduler().runTaskLater(plugin, this::startPressurePadEffect, 5);
    }
}
