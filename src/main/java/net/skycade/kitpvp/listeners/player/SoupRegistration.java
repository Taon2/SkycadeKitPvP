package net.skycade.kitpvp.listeners.player;

import net.skycade.kitpvp.KitPvP;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SoupRegistration implements Listener {
    private final KitPvP plugin;
    private List<UUID> preventDoubleSoup = new ArrayList<>();

    public SoupRegistration(KitPvP plugin) {
        this.plugin = plugin;
        Bukkit.getScheduler().runTaskTimer(plugin, this::preventDoubleSoupArrayListStuck, 1L, 5L);
    }
    private void preventDoubleSoupArrayListStuck() {
        preventDoubleSoup.clear();
    }

    // this is necessary to fix soup reg, because it runs before the general PlayerInteractEvent
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        int slot = event.getPlayer().getInventory().getHeldItemSlot();
        if (item == null) return;
        if (item.getType() == Material.MUSHROOM_SOUP) {
            if (preventDoubleSoup.contains(player.getUniqueId())) return;
            if (player.getHealth() == player.getMaxHealth()) return;

            preventDoubleSoup.add(player.getUniqueId());
            double toHeal = Math.min(player.getHealth() + 7.0, player.getMaxHealth());
            player.setHealth(toHeal);
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.getInventory().clear(slot);
                    player.updateInventory();
                    preventDoubleSoup.remove(player.getUniqueId());
                }
            }.runTaskLater(plugin, 1L);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void soup(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        int slot = event.getPlayer().getInventory().getHeldItemSlot();
        if (event.getItem() == null) return;
        if (event.getItem().getType() == Material.MUSHROOM_SOUP) {
            if (preventDoubleSoup.contains(player.getUniqueId())) return;
            if (player.getHealth() == player.getMaxHealth()) return;

            preventDoubleSoup.add(player.getUniqueId());
            double toHeal = Math.min(player.getHealth() + 7.0, player.getMaxHealth());
            player.setHealth(toHeal);
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.getInventory().clear(slot);
                    player.updateInventory();
                    preventDoubleSoup.remove(player.getUniqueId());
                }
            }.runTaskLater(plugin, 1L);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void soupWhileAttacking(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            int slot = player.getInventory().getHeldItemSlot();
            if (player.getInventory().getItemInHand() == null) return;
            if (player.getInventory().getItemInHand().getType() == Material.MUSHROOM_SOUP) {
                if (preventDoubleSoup.contains(player.getUniqueId())) return;
                if (player.getHealth() == player.getMaxHealth()) return;

                preventDoubleSoup.add(player.getUniqueId());
                double toHeal = Math.min(player.getHealth() + 7.0, player.getMaxHealth());
                player.setHealth(toHeal);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.getInventory().clear(slot);
                        player.updateInventory();
                        preventDoubleSoup.remove(player.getUniqueId());
                    }
                }.runTaskLater(plugin, 1L);
            }
        }
    }
}
