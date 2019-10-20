package net.skycade.kitpvp.listeners.player;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.bukkitevents.KitPvPKillstreakChange;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.events.RandomEvent;
import net.skycade.kitpvp.events.TagEvent;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.kit.kits.disabled.KitMedic;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PlayerListeners implements Listener {

    private final KitPvP plugin;

    public PlayerListeners(KitPvP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
            e.getItem().remove();
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE)
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE)
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            if (e.getItemDrop().getItemStack().getType() != Material.LEATHER)
                e.setCancelled(true);
            else {
                Kit playerKit = plugin.getStats(MemberManager.getInstance().getMember(e.getPlayer()))
                        .getActiveKit().getKit();
                if (playerKit.getKitType() == KitType.MEDIC)
                    ((KitMedic) playerKit).onMedpackUse(e.getPlayer(), e.getItemDrop());
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (e.getInventory().getHolder() instanceof Chest || e.getInventory().getHolder() instanceof DoubleChest
                || e.getInventory().getType() == InventoryType.ANVIL
                || e.getInventory().getType() == InventoryType.FURNACE)
            if (e.getPlayer().getGameMode() != GameMode.CREATIVE)
                e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (!plugin.getSpawnRegion().contains(e.getTo()) || plugin.getSpawnRegion().contains(e.getFrom())) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!e.isCancelled()) resetKitAndKS(e.getPlayer());
            }
        }.runTaskLater(plugin, 2);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getFrom().getBlock().equals(e.getTo().getBlock())) return;

        KitPvPStats stats = KitPvP.getInstance().getStats(e.getPlayer());
        stats.getActiveKit().getKit().onMove(e.getPlayer());

        if (!plugin.getSpawnRegion().contains(e.getTo()) || plugin.getSpawnRegion().contains(e.getFrom())) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!e.isCancelled()) resetKitAndKS(e.getPlayer());
            }
        }.runTaskLater(plugin, 2);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event) {
        resetKitAndKS(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getName() != null && !e.getWhoClicked().getGameMode().equals(GameMode.CREATIVE) && e.getInventory().getType() != InventoryType.CRAFTING)
            e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        KitPvPStats stats = plugin.getStats(e.getPlayer());

        String prestige = ChatColor.GRAY + "[" + ChatColor.WHITE + stats.getPrestigeLevel() + "★" + ChatColor.GRAY + "]";
        e.setFormat(prestige + " " + ChatColor.RESET + e.getFormat());
    }

    private void resetKitAndKS(Player p) {
        KitPvPStats stats = plugin.getStats(p);
        if (stats == null) return;
        stats.applyKitPreference();
        p.getInventory().clear();
        if (p.getItemOnCursor().getType() != null && p.getItemOnCursor().getType() != Material.AIR)
            p.setItemOnCursor(null);
        for (PotionEffect potionEffect : p.getActivePotionEffects()) p.removePotionEffect(potionEffect.getType());
        int streak = stats.getStreak();

        //For missions
        KitPvPKillstreakChange killstreakEvent = new KitPvPKillstreakChange(p, streak);
        Bukkit.getServer().getPluginManager().callEvent(killstreakEvent);

        if (streak > stats.getHighestStreak())
            stats.setHighestStreak(stats.getStreak());
        stats.setStreak(0);

        if (RandomEvent.getCurrent() instanceof TagEvent) {
            TagEvent tagEvent = (TagEvent) RandomEvent.getCurrent();
            if (tagEvent.getInfected() != null) {
                if (tagEvent.getInfected().equals(p.getUniqueId())) {
                    tagEvent.stop();
                } else {
                    tagEvent.remove(p.getUniqueId());
                }
            }
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (p.isOnline()) {
                stats.getActiveKit().getKit().beginApplyKit(p);
                stats.getActiveKit().getKit().giveSoup(p, 32);
                plugin.getEventShopManager().reapplyUpgrades(p);
            }
        }, 1);

        Bukkit.getScheduler().runTaskLater(plugin, p::updateInventory, 10);
    }

    // Not the best place for this method..
    public static void respawn(Player p) {
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> UtilPlayer.reset(p), 1);
        p.setHealth(p.getMaxHealth());
        p.setVelocity(new Vector(0, 0, 0));
        p.setGameMode(GameMode.SURVIVAL);
        p.teleport(KitPvP.getInstance().getSpawnLocation());
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), p::updateInventory, 10);
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> p.setVelocity(new org.bukkit.util.Vector(0, 0, 0)), 5);
        KitPvPStats stats = KitPvP.getInstance().getStats(p);
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
            stats.getActiveKit().getKit().giveSoup(p, 32);
        }, 5);
        stats.applyKitPreference();
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
            stats.getActiveKit().getKit().beginApplyKit(p);
            KitPvP.getInstance().getEventShopManager().reapplyUpgrades(p);
        }, 3);
    }
}
