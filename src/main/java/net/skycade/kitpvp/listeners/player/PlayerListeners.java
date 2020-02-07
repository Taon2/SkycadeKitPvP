package net.skycade.kitpvp.listeners.player;

import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import net.skycade.SkycadeCombat.data.CombatData;
import net.skycade.SkycadeCore.utility.TeleportUtil;
import net.skycade.SkycadeCore.vanish.VanishStatus;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.events.RandomEvent;
import net.skycade.kitpvp.events.TagEvent;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.kit.kits.disabled.KitMedic;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPStats;
import net.skycade.koth.SkycadeKoth;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import static net.skycade.kitpvp.Messages.KNOCKBACK_REMOVED;

public class PlayerListeners implements Listener {

    private final KitPvP plugin;

    public PlayerListeners(KitPvP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            event.getItem().remove();
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            if (event.getItemDrop().getItemStack().getType() != Material.LEATHER)
                event.setCancelled(true);
            else {
                Kit playerKit = plugin.getStats(MemberManager.getInstance().getMember(event.getPlayer()))
                        .getActiveKit().getKit();
                if (playerKit.getKitType() == KitType.MEDIC)
                    ((KitMedic) playerKit).onMedpackUse(event.getPlayer(), event.getItemDrop());
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof Chest || event.getInventory().getHolder() instanceof DoubleChest
                || event.getInventory().getType() == InventoryType.ANVIL
                || event.getInventory().getType() == InventoryType.FURNACE)
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getPlayer().isDead()) return;

        CombatData.Combat combat = CombatData.getCombat(event.getPlayer());

        if (TeleportUtil.getSpawn().equals(event.getTo()) && !plugin.isInSpawnArea(event.getPlayer()) && combat.isInCombat()) {
            event.setCancelled(true);
            return;
        }

        if (!plugin.getSpawnRegion().contains(event.getTo()) || plugin.getSpawnRegion().contains(event.getFrom())) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!event.isCancelled()) resetKitAndKS(event.getPlayer());
            }
        }.runTaskLater(plugin, 2);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlock().equals(event.getTo().getBlock())) return;

        // Removes knockback during KOTH
        if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getEnchantments().containsKey(Enchantment.KNOCKBACK) && SkycadeKoth.getInstance().getGameManager().getActiveKOTHGame() != null) {
            event.getPlayer().getItemInHand().removeEnchantment(Enchantment.KNOCKBACK);
            event.getPlayer().updateInventory();
            KNOCKBACK_REMOVED.msg(event.getPlayer());
        }

        if (!plugin.getSpawnRegion().contains(event.getPlayer().getLocation()) && !VanishStatus.isVanished(event.getPlayer().getUniqueId())) {
            KitPvPStats stats = KitPvP.getInstance().getStats(event.getPlayer());
            stats.getActiveKit().getKit().onMove(event.getPlayer());
        }

        if (!plugin.getSpawnRegion().contains(event.getTo()) || plugin.getSpawnRegion().contains(event.getFrom())) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!event.isCancelled()) resetKitAndKS(event.getPlayer());
            }
        }.runTaskLater(plugin, 2);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event) {
        resetKitAndKS(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if ((!VanishStatus.isVanished(event.getWhoClicked().getUniqueId())
                && event.getClickedInventory() != null
                && event.getClickedInventory().getName() != null
                && !event.getWhoClicked().getGameMode().equals(GameMode.CREATIVE)
                && event.getInventory().getType() != InventoryType.CRAFTING
                && event.getInventory().getType() != InventoryType.CHEST)
                || event.getSlotType() == InventoryType.SlotType.CRAFTING)
            event.setCancelled(true);

        if (event.getSlotType() == InventoryType.SlotType.CRAFTING && event.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
            event.setCurrentItem(null);
            event.setCursor(null);
            ((Player) event.getWhoClicked()).updateInventory();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        KitPvPStats stats = plugin.getStats(event.getPlayer());

        String gangTitle = null;

        Gang gang = GangsPlusApi.getPlayersGang(event.getPlayer().getPlayer());
        String gangName;
        if (gang != null) {
            gangName = gang.getName();
            gangTitle = ChatColor.GRAY + "[" + ChatColor.WHITE + gangName + ChatColor.GRAY + "]";
        }

        String prestige = ChatColor.GRAY + "[" + ChatColor.WHITE + stats.getPrestigeLevel() + "★" + ChatColor.GRAY + "]";

        if (gangTitle == null)
            event.setFormat(prestige + " " + ChatColor.RESET + event.getFormat());
        else
            event.setFormat(gangTitle + " " + prestige + " " + ChatColor.RESET + event.getFormat());
    }

    private void resetKitAndKS(Player p) {
        KitPvPStats stats = plugin.getStats(p);
        if (stats == null) return;
        stats.getActiveKit().getKit().cancelRunnables(p);
        stats.applyKitPreference();
        p.getOpenInventory().close();
        p.getInventory().clear();
        if (p.getItemOnCursor().getType() != null && p.getItemOnCursor().getType() != Material.AIR)
            p.setItemOnCursor(null);
        for (PotionEffect potionEffect : p.getActivePotionEffects()) p.removePotionEffect(potionEffect.getType());
        int streak = stats.getStreak();

        if (streak > stats.getHighestStreak())
            stats.setHighestStreak(stats.getStreak());

        if (!KitPvP.getInstance().getEventShopManager().isKeepingKs(p))
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
                stats.getActiveKit().getKit().stopItemRunnables(p);
                stats.getActiveKit().getKit().beginApplyKit(p);
                stats.getActiveKit().getKit().giveSoup(p, 32);
                plugin.getEventShopManager().reapplyUpgrades(p);
            }
        }, 1);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (p.isOnline()) p.updateInventory();
        }, 10);

        ScoreboardInfo.getInstance().updatePlayer(p);
    }
}
