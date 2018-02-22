package net.skycade.kitpvp.listeners.player;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.kit.kits.KitMedic;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListeners implements Listener {

    private final KitPvP plugin;

    public PlayerListeners(KitPvP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(PlayerPickupItemEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
            e.getItem().remove();
        }
    }

    @EventHandler
    public void on(BlockPlaceEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE)
            e.setCancelled(true);
    }

    @EventHandler
    public void on(BlockBreakEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE)
            e.setCancelled(true);
    }

    @EventHandler
    public void on(PlayerDropItemEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            if (e.getItemDrop().getItemStack().getType() != Material.LEATHER)
                e.setCancelled(true);
            else {
                if (plugin.isInSpawnArea(e.getPlayer())) {
                    e.setCancelled(true);
                    return;
                }
                Kit playerKit = plugin.getStats(MemberManager.getInstance().getMember(e.getPlayer()))
                        .getActiveKit().getKit();
                if (playerKit.getKitType() == KitType.MEDIC)
                    ((KitMedic) playerKit).onMedpackUse(e.getPlayer(), e.getItemDrop());
            }
        }
    }

    @EventHandler
    public void on(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void on(InventoryOpenEvent e) {
        if (e.getInventory().getHolder() instanceof Chest || e.getInventory().getHolder() instanceof DoubleChest
                || e.getInventory().getType() == InventoryType.ANVIL
                || e.getInventory().getType() == InventoryType.FURNACE)
            if (e.getPlayer().getGameMode() != GameMode.CREATIVE)
                e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void on(PlayerTeleportEvent e) {
        if (!plugin.getSpawnRegion().contains(e.getTo()) || plugin.getSpawnRegion().contains(e.getFrom())) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!e.isCancelled()) resetKitAndKS(e.getPlayer());
            }
        }.runTaskLater(plugin, 2);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void on(PlayerMoveEvent e) {
        if (e.getFrom().getBlock().equals(e.getTo().getBlock())) return;
        if (!plugin.getSpawnRegion().contains(e.getTo()) || plugin.getSpawnRegion().contains(e.getFrom())) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!e.isCancelled()) resetKitAndKS(e.getPlayer());
            }
        }.runTaskLater(plugin, 2);
    }

    public void resetKitAndKS(Player p) {
        KitPvPStats stats = plugin.getStats(p);
        stats.applyKitPreference();
        p.getInventory().clear();
        for (PotionEffect potionEffect : p.getActivePotionEffects()) p.removePotionEffect(potionEffect.getType());
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            stats.getActiveKit().getKit().applyKit(p);
        }, 3);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            stats.getActiveKit().getKit().giveSoup(p, 32);
        }, 5);
        Bukkit.getScheduler().runTaskLater(plugin, p::updateInventory, 10);
        int streak = stats.getStreak();
        if (streak > stats.getHighestStreak())
            stats.setHighestStreak(stats.getStreak());
        stats.setStreak(0);
    }

}
