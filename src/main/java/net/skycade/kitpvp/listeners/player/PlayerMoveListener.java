package net.skycade.kitpvp.listeners.player;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class PlayerMoveListener implements Listener {

    private final KitPvP plugin;
    private List<Location> teleports = new ArrayList<>();
    private Map<UUID, Location> teleporting = new HashMap<>();

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
                        && UtilPlayer.isMoving(p)
                        && !teleporting.containsKey(p.getUniqueId())).collect(Collectors.toList())
                .forEach(p -> {
                    Location teleport = teleports.get(ThreadLocalRandom.current().nextInt(0, teleports.size()));
                    teleporting.put(p.getUniqueId(), teleport);

                    p.playSound(p.getLocation(), Sound.PORTAL_TRAVEL, 1f, 1f);
                    p.setGameMode(GameMode.SPECTATOR);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 2));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Integer.MAX_VALUE, 2));

                    new BukkitRunnable() {
                        public void run() {
                            if (!p.isOnline()) {
                                this.cancel();
                                return;
                            }

                            org.bukkit.util.Vector pos = p.getLocation().toVector();
                            org.bukkit.util.Vector target = teleport.toVector();
                            Vector dir = target.subtract(pos).normalize();
                            double distance = teleport.distance(p.getLocation());
                            p.setVelocity(dir.multiply(distance > 10 ? 3 : distance / 5));

                            if (distance < 2) {
                                p.setVelocity(dir.multiply(0));
                                p.removePotionEffect(PotionEffectType.BLINDNESS);
                                p.setGameMode(GameMode.SURVIVAL);
                                teleporting.remove(p.getUniqueId());
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(KitPvP.getInstance(), 1, 1);
                });
        Bukkit.getScheduler().runTaskLater(plugin, this::startPressurePadEffect, 5);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (teleporting.containsKey(event.getPlayer().getUniqueId())) {
            Player p = event.getPlayer();

            p.removePotionEffect(PotionEffectType.BLINDNESS);
            p.setGameMode(GameMode.SURVIVAL);
            teleporting.remove(p.getUniqueId());
        }
    }
}
