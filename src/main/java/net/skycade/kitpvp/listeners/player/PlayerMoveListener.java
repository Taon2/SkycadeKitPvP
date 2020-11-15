package net.skycade.kitpvp.listeners.player;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class PlayerMoveListener implements Listener {

    private final KitPvP plugin;
    private List<Location> teleports = new ArrayList<>();
    private Map<UUID, Location> teleporting = new HashMap<>();
    private static List<UUID> immune = new ArrayList<>();
    private final Map<UUID, Long> lilyCooldown = new HashMap<>();

    private ArrayList<UUID> flyingParticles = new ArrayList<>();
    private ArrayList<UUID> flyingParticleStay = new ArrayList<>();

    public PlayerMoveListener(KitPvP plugin) {
        this.plugin = plugin;

        ConfigurationSection main = plugin.getConfig().getConfigurationSection("teleport-locations");
        for (String handle : main.getKeys(false)) {
            teleports.add((Location) main.get(handle));
        }

        Bukkit.getScheduler().runTaskTimer(plugin, this::randomTpEffects, 0L, 5L); // Random teleports from Spawn
        Bukkit.getScheduler().runTaskTimer(plugin, this::startLilypadEffect, 0L, 5L); // Lilypad Jump
        Bukkit.getScheduler().runTaskTimer(plugin, this::startSlimeblockEffect, 0L, 5L); // Slimeblock Jump
    }

    private void startLilypadEffect() {
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> (p.getLocation().getBlock().getType() == Material.WATER_LILY)
                        && UtilPlayer.isMoving(p) && !isOnLilyCooldown(p)).collect(Collectors.toList())
                .forEach(p -> {
                            p.setVelocity(new org.bukkit.util.Vector(p.getLocation().getDirection().getX(), 1.3,
                                    p.getLocation().getDirection().getZ()));
                            addLilyCooldown(p, 10); // 10 second cool-down
                        }
                );
    }

    private void startSlimeblockEffect() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            Location loc = p.getLocation();
            loc.setY(loc.getY() - 1);
            if (loc.getBlock().getType() == Material.SLIME_BLOCK) {
                p.setVelocity(new org.bukkit.util.Vector(p.getLocation().getDirection().getX(), 0.5, p.getLocation().getDirection().getZ()).multiply(2.8));
                flyingParticles.add(p.getUniqueId());
                flyingParticleStay.add(p.getUniqueId());

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        flyingParticleStay.remove(p.getUniqueId());
                    }
                }.runTaskLater(KitPvP.getInstance(), 15);
            }
        }
    }

    // New teleport pad method
    private void randomTpEffects() {
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> (p.getLocation().getBlock().getType() == Material.GOLD_PLATE) && UtilPlayer.isMoving(p)
                        && p.getGameMode() == GameMode.SURVIVAL)
                .forEach(p -> {
                    Location teleport = teleports.get(ThreadLocalRandom.current().nextInt(0, teleports.size()));
                    float yaw = 180;
                    float pitch = 0;
                    p.teleport(new Location(teleport.getWorld(), teleport.getX(), teleport.getY(), teleport.getZ(), yaw, pitch));

                    ActionBarAPI.sendActionBar(p, ChatColor.translateAlternateColorCodes('&', "&c&lNow entering the Arena"));

                    immune.add(p.getUniqueId());
                    p.setGameMode(GameMode.SURVIVAL);
                    p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
                    // Removes immunity after 4 seconds
                    Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
                        immune.remove(p.getUniqueId());
                    }, 4 * 20);
                });
    }
    // Old teleport pad method
    /*private void startPressurePadEffect() {
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> (p.getLocation().getBlock().getType() == Material.GOLD_PLATE)
                        && UtilPlayer.isMoving(p)
                        && p.getGameMode() == GameMode.SURVIVAL
                        && !teleporting.containsKey(p.getUniqueId())).collect(Collectors.toList())
                .forEach(p -> {
                    Location teleport = teleports.get(ThreadLocalRandom.current().nextInt(0, teleports.size()));
                    teleporting.put(p.getUniqueId(), teleport);

                    p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
                    p.setGameMode(GameMode.SPECTATOR);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 2));

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
                            p.setVelocity(dir.multiply(distance > 15 ? 3 : distance / 5));

                            if (distance < 4) {
                                p.setVelocity(dir.multiply(0));
                                p.removePotionEffect(PotionEffectType.BLINDNESS);
                                p.setGameMode(GameMode.SURVIVAL);
                                p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
                                teleporting.remove(p.getUniqueId());
                                immune.add(p.getUniqueId());

                                // Removes immunity after 2 seconds
                                Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
                                    immune.remove(p.getUniqueId());
                                }, 4 * 20);

                                this.cancel();
                            }
                        }
                    }.runTaskTimer(KitPvP.getInstance(), 1, 1);
                });
        Bukkit.getScheduler().runTaskLater(plugin, this::startPressurePadEffect, 5);
    }

     */

    // "Anti-phase"
    // If a player is detected inside of a block (due to a glitch with Knight and/or teleporter) it will do the following:
    // * If you are in combat, the last player to hit you will get the kill.
    // * If you are not in combat, you will be teleported to spawn.
    @EventHandler
    public void antiPhase(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;

        if (isGlitched(player)) {
            if (player.isInsideVehicle()) {
                player.getVehicle().eject();
            }
            player.kickPlayer("[Glitch Detection] Stuck in block?");
        }
    }

    @EventHandler
    public void slimeParticles(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        Location loc = p.getLocation();
        if (loc.getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) {
            if (flyingParticles.contains(p.getUniqueId())) {
                if (!flyingParticleStay.contains(p.getUniqueId())) {
                    flyingParticles.remove(p.getUniqueId());
                }
            }
        }
        if (flyingParticles.contains(p.getUniqueId())) {
            if (loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                p.getWorld().playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 5, 5);
            }
        }
    }

    public static boolean isGlitched(Player player) {
        double x = player.getLocation().getX();
        double z = player.getLocation().getZ();

        double y1 = player.getLocation().getY();
        double y2 = player.getLocation().getY() + 1;

        World world = player.getWorld();

        Location loc1 = new Location(world, x, y1, z);
        Location loc2 = new Location(world, x, y2, z);

        Material y1block = player.getWorld().getBlockAt(loc1).getType();
        Material y2block = player.getWorld().getBlockAt(loc2).getType();

        return y1block == Material.BARRIER || y2block == Material.BARRIER;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (teleporting.containsKey(event.getPlayer().getUniqueId())) {
            Player p = event.getPlayer();

            p.removePotionEffect(PotionEffectType.BLINDNESS);
            p.setGameMode(GameMode.SURVIVAL);
            teleporting.remove(p.getUniqueId());
            immune.remove(p.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        // Stops spectators from teleporting
        if (teleporting.containsKey(event.getPlayer().getUniqueId()) && event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            event.setCancelled(true);
            event.getPlayer().setSpectatorTarget(null);
        }
    }

    public static List<UUID> getImmunePlayers() {
        return immune;
    }

    public static void addImmunePlayer(UUID uuid) {
        immune.add(uuid);
    }

    public static void removeImmunePlayer(UUID uuid) {
        immune.remove(uuid);
    }


    private void removeLilyCooldown(Player p) {
        lilyCooldown.remove(p.getUniqueId());
    }

    private boolean isOnLilyCooldown(Player p) {
        return lilyCooldown.containsKey(p.getUniqueId());
    }

    public boolean addLilyCooldown(Player p, int seconds) {
        if (isOnLilyCooldown(p)) {
            return false;
        }

        lilyCooldown.put(p.getUniqueId(), new Date().getTime() + seconds * 1000);

        // cooldown task
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
            removeLilyCooldown(p);
        }, seconds * 20);
        return true;
    }
}