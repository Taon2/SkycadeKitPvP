package net.skycade.kitpvp.listeners;

import net.minecraft.server.v1_8_R3.EntityArrow;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArrow;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;

public class WorldListeners implements Listener {

    private final KitPvP plugin;

    public WorldListeners(KitPvP plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWeatherChange(WeatherChangeEvent event) {
        World w = event.getWorld();
        if (!w.hasStorm())
            event.setCancelled(true);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (w.hasStorm())
                w.setStorm(false);
        }, 5);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public synchronized void onBlockFromTo(BlockFromToEvent event) {
        if (event.getBlock().isLiquid())
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Entity en : event.getEntity().getNearbyEntities(4, 4, 4))
            if (en instanceof FallingBlock)
                event.getEntity().remove();
        event.setCancelled(true);
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) return;

        // only run this event on health pots
        for (PotionEffect effect : event.getPotion().getEffects()) {
            if (!effect.getType().equals(PotionEffectType.HEAL)) return;
        }

        // only let the health pot affect the thrower
        for (LivingEntity affectedEntity : event.getAffectedEntities()) {
            if (!affectedEntity.getUniqueId().equals(((Player) event.getEntity().getShooter()).getUniqueId())) {
                event.setIntensity(affectedEntity, 0);
            }
        }
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER)
            return;

        event.getDrops().clear();
        event.setDroppedExp(0);
    }

    @EventHandler
    private void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player))
            return;
        if (event.getEntityType() == EntityType.ARROW) {
            Player shooter = (Player) event.getEntity().getShooter();
            // Must be run in a delayed task otherwise it won't be able to find the block
            Bukkit.getScheduler().scheduleSyncDelayedTask(KitPvP.getInstance(), () -> {
                try {

                    EntityArrow entityArrow = ((CraftArrow) event
                            .getEntity()).getHandle();

                    Field fieldX = EntityArrow.class
                            .getDeclaredField("d");
                    Field fieldY = EntityArrow.class
                            .getDeclaredField("e");
                    Field fieldZ = EntityArrow.class
                            .getDeclaredField("f");

                    fieldX.setAccessible(true);
                    fieldY.setAccessible(true);
                    fieldZ.setAccessible(true);

                    int x = fieldX.getInt(entityArrow);
                    int y = fieldY.getInt(entityArrow);
                    int z = fieldZ.getInt(entityArrow);

                    if (y != -1) {
                        Block block = event.getEntity().getWorld().getBlockAt(x, y, z);
                        KitPvPStats stats = plugin.getStats(shooter);
                        Kit kit = null;
                        if (stats != null)
                            kit = stats.getActiveKit().getKit();

                        if (kit != null && kit.getKitType() == KitType.PYROMANCER)
                            kit.onArrowLand(shooter, block, event);
                    }

                } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e1) {
                    e1.printStackTrace();
                }
            });
        }
    }
}