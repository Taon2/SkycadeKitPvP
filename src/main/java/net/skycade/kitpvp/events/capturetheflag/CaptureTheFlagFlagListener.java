package net.skycade.kitpvp.events.capturetheflag;

import net.md_5.bungee.api.ChatColor;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.events.CaptureTheFlagEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static net.skycade.kitpvp.Messages.*;

public class CaptureTheFlagFlagListener implements Listener {
    private final KitPvP plugin;
    private final CaptureTheFlagEvent captureTheFlagEvent;
    private static CaptureTheFlagFlagListener instance;

    private Player flagCarrier;
    private Player lastCarrier;
    private Location bannerSpawnLocation;
    private Location bannerCurrentLocation;
    private Location red1;
    private Location red2;
    private Location blue1;
    private Location blue2;

    public CaptureTheFlagFlagListener(KitPvP plugin, CaptureTheFlagEvent captureTheFlagEvent) {
        this.plugin = plugin;
        this.captureTheFlagEvent = captureTheFlagEvent;
        instance = this;

        load();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlock().equals(event.getTo().getBlock())) return;

        if (captureTheFlagEvent.getBegin() == null) return;

        Player p = event.getPlayer();
        if (p.getLocation().getBlock().getType() == Material.STANDING_BANNER) {
            if (p.equals(lastCarrier)) {
                CAPTURETHEFLAG_CANT_CARRY.msg(p);
                return;
            }

            CAPTURETHEFLAG_PICKED_UP_FLAG.broadcast("%player%", captureTheFlagEvent.isTeamRed(p) ? ChatColor.RED + "" + ChatColor.BOLD + p.getName() : ChatColor.BLUE + "" + ChatColor.BOLD + p.getName());
            flagCarrier = event.getPlayer();
            captureTheFlagEvent.refreshArmor(flagCarrier, true);
            flagCarrier.getLocation().getBlock().setType(Material.AIR);
            flagCarrier.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 2));
            bannerCurrentLocation = null;
        }

        if (p.equals(flagCarrier) && captureTheFlagEvent.isTeamRed(p) && isWithinLocation(p.getLocation(), red1, red2)) {
            CAPTURETHEFLAG_POINT.broadcast("%player%", ChatColor.RED + "" + ChatColor.BOLD + p.getName(), "%team%", ChatColor.RED + "" + ChatColor.BOLD + "RED");
            captureTheFlagEvent.addRedPoints(1);
            p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 2.0F, 1.0F);
            clearFlagCarrier();
            spawnBanner();
        } else if (p.equals(flagCarrier) && !captureTheFlagEvent.isTeamRed(p) && isWithinLocation(p.getLocation(), blue1, blue2)) {
            CAPTURETHEFLAG_POINT.broadcast("%player%", ChatColor.BLUE + "" + ChatColor.BOLD + p.getName(), "%team%", ChatColor.BLUE + "" + ChatColor.BOLD + "BLUE");
            captureTheFlagEvent.addBluePoints(1);
            p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 2.0F, 1.0F);
            clearFlagCarrier();
            spawnBanner();
        }

        if (flagCarrier != null)
            captureTheFlagEvent.refreshArmor(flagCarrier, true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (captureTheFlagEvent.getBegin() == null) return;

        Player p = event.getEntity();

        if (p.equals(flagCarrier)) {
            CAPTURETHEFLAG_DROPPED_FLAG.broadcast("%player%", captureTheFlagEvent.isTeamRed(p) ? ChatColor.RED + "" + ChatColor.BOLD + p.getName() : ChatColor.BLUE + "" + ChatColor.BOLD + p.getName());
            if (p.getLocation().getBlock().getType() != Material.AIR)
                p.getLocation().add(0, 1, 0).getBlock().setType(Material.STANDING_BANNER);
            else
                p.getLocation().getBlock().setType(Material.STANDING_BANNER);
            bannerCurrentLocation = p.getLocation();
            clearFlagCarrier();
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (captureTheFlagEvent.getBegin() == null) return;

        Player p = event.getPlayer();

        if (p.equals(flagCarrier)) {
            CAPTURETHEFLAG_DROPPED_FLAG.broadcast("%player%", captureTheFlagEvent.isTeamRed(p) ? ChatColor.RED + "" + ChatColor.BOLD + p.getName() : ChatColor.BLUE + "" + ChatColor.BOLD + p.getName());
            if (p.getLocation().getBlock().getType() != Material.AIR)
                p.getLocation().add(0, 1, 0).getBlock().setType(Material.STANDING_BANNER);
            else
                p.getLocation().getBlock().setType(Material.STANDING_BANNER);
            bannerCurrentLocation = p.getLocation();
            clearFlagCarrier();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (captureTheFlagEvent.getBegin() == null) return;

        Player p = event.getPlayer();

        if (p.equals(flagCarrier)) {
            CAPTURETHEFLAG_DROPPED_FLAG.broadcast("%player%", captureTheFlagEvent.isTeamRed(p) ? ChatColor.RED + "" + ChatColor.BOLD + p.getName() : ChatColor.BLUE + "" + ChatColor.BOLD + p.getName());
            if (p.getLocation().getBlock().getType() != Material.AIR)
                p.getLocation().add(0, 1, 0).getBlock().setType(Material.STANDING_BANNER);
            else
                p.getLocation().getBlock().setType(Material.STANDING_BANNER);
            bannerCurrentLocation = p.getLocation();
            clearFlagCarrier();
        }
    }

    private static boolean isWithinLocation(Location location, Location boundaryPoint1, Location boundaryPoint2) {
        if (!location.getWorld().getName().equalsIgnoreCase(boundaryPoint1.getWorld().getName())) return false;

        if ((location.getBlockX() >= boundaryPoint1.getBlockX() && location.getBlockX() <= boundaryPoint2.getBlockX()) || (location.getBlockX() <= boundaryPoint1.getBlockX() && location.getBlockX() >= boundaryPoint2.getBlockX())) {
            return (location.getBlockZ() >= boundaryPoint1.getBlockZ() && location.getBlockZ() <= boundaryPoint2.getBlockZ()) || (location.getBlockZ() <= boundaryPoint1.getBlockZ() && location.getBlockZ() >= boundaryPoint2.getBlockZ());
        }

        return false;
    }

    public void clearFlagCarrier() {
        if (flagCarrier == null) return;

        flagCarrier.removePotionEffect(PotionEffectType.SLOW);
        captureTheFlagEvent.refreshArmor(flagCarrier, false);
        lastCarrier = flagCarrier;
        flagCarrier = null;
    }

    public void spawnBanner() {
        bannerSpawnLocation.getBlock().setType(Material.STANDING_BANNER);
        bannerCurrentLocation = bannerSpawnLocation;
    }

    public void removeBanner() {
        if (bannerCurrentLocation != null) {
            bannerCurrentLocation.getBlock().setType(Material.AIR);
        }
    }

    public static CaptureTheFlagFlagListener getInstance() {
        return instance;
    }

    public Player getCurrentCarrier() {
        return flagCarrier;
    }

    public Location getCurrentFlagLocation() {
        if (flagCarrier == null) {
            return bannerCurrentLocation;
        } else {
            return flagCarrier.getLocation();
        }
    }

    private void load() {
        ConfigurationSection main = plugin.getConfig().getConfigurationSection("capturetheflag");
        bannerSpawnLocation = ((Location) main.get("banner-spawn"));

        ConfigurationSection redRegion = main.getConfigurationSection("red-region");
        red1 = ((Location) redRegion.get("point-1"));
        red2 = ((Location) redRegion.get("point-2"));

        ConfigurationSection blueRegion = main.getConfigurationSection("blue-region");
        blue1 = ((Location) blueRegion.get("point-1"));
        blue2 = ((Location) blueRegion.get("point-2"));
    }
}
