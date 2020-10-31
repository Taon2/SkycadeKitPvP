package net.skycade.kitpvp.listeners.player;

import net.skycade.SkycadeCore.vanish.VanishStatus;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerInteractListener implements Listener {

    private final KitPvP plugin;

    public PlayerInteractListener(KitPvP plugin) {
        this.plugin = plugin;
        preventDoubleSoupArrayListStuck();
    }

    private void preventDoubleSoupArrayListStuck() {
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> (preventDoubleSoup.contains(p.getUniqueId())))
                .forEach(p -> {
                            preventDoubleSoup.remove(p.getUniqueId());
                        }
                );
        Bukkit.getScheduler().runTaskLater(plugin, this::preventDoubleSoupArrayListStuck, 5);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player) || VanishStatus.isVanished(event.getPlayer().getUniqueId()) || plugin.isInSpawnArea(event.getPlayer()) || (plugin.getStats(event.getPlayer()).isAbilityToggle() && !event.getPlayer().isSneaking()))
            return;

        plugin.getStats(event.getPlayer()).getActiveKit().getKit().onInteract(event.getPlayer(), (Player) event.getRightClicked(), event.getPlayer().getInventory().getItemInHand());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return;

        if (KitPvP.getInstance().isInSpawnArea(event.getPlayer())
                || event.getBlock().getRelative(BlockFace.UP).getType() == Material.WATER_LILY
                || event.getBlockReplacedState().getType() == Material.DOUBLE_PLANT) {
            event.setCancelled(true);
            return;
        }

        KitType type = plugin.getStats(event.getPlayer()).getActiveKit();
        if (type == KitType.BUILDUHC || type == KitType.LICH)
            type.getKit().onBlockPlace(event.getPlayer(), event.getBlock(), event.getBlockReplacedState());
        else
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return;

        if (KitPvP.getInstance().isInSpawnArea(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);
        KitType.LICH.getKit().onBlockBreak(event.getPlayer(), event.getBlock());
    }


    private List<UUID> preventDoubleSoup = new ArrayList<>();
    // this is necessary to fix soup reg, because it runs before the general PlayerInteractEvent
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityInteractAtEntity(PlayerInteractAtEntityEvent event) {
        /*Player p = event.getPlayer();
        ItemStack item = p.getItemInHand();

        // applies soup to the player, runs first for soup reg reasons
        if (item != null && item.getType() == Material.MUSHROOM_SOUP) {
            double health = p.getHealth();
            double maxHealth = p.getMaxHealth();
            if (health < maxHealth) {
                // heals
                if (health < maxHealth - 7)
                    p.setHealth(health + 7);
                else
                    p.setHealth(maxHealth);

                final int heldItemSlot = event.getPlayer().getInventory().getHeldItemSlot();

                event.getPlayer().getInventory().clear(heldItemSlot);
                p.updateInventory();
            }
        }

         */

        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        int slot = event.getPlayer().getInventory().getHeldItemSlot();
        if (item == null)return;
        if (item.getType() == Material.MUSHROOM_SOUP) {
            if (preventDoubleSoup.contains(player.getUniqueId())) return;
            if (player.getHealth() == player.getMaxHealth())return;

            preventDoubleSoup.add(player.getUniqueId());
            double toHeal = Math.min(player.getHealth() + 7.0, player.getMaxHealth());
            player.setHealth(toHeal);
            new BukkitRunnable(){
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
    public void soup(PlayerInteractEvent event){
        Player player = event.getPlayer();
        int slot = event.getPlayer().getInventory().getHeldItemSlot();
        if (event.getItem() == null)return;
        if (event.getItem().getType() == Material.MUSHROOM_SOUP) {
            if (preventDoubleSoup.contains(player.getUniqueId())) return;
            if (player.getHealth() == player.getMaxHealth())return;

            preventDoubleSoup.add(player.getUniqueId());
            double toHeal = Math.min(player.getHealth() + 7.0, player.getMaxHealth());
            player.setHealth(toHeal);
            new BukkitRunnable(){
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
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player p = event.getPlayer();

        /*
        // cancel if they are trying to soup again when they cant
        if (preventDoubleSoup.contains(p.getUniqueId())) return;

        // applies soup to the player, runs first for soup reg reasons
        if (item != null && item.getType() == Material.MUSHROOM_SOUP) {
            double health = p.getHealth();
            double maxHealth = p.getMaxHealth();
            if (health < maxHealth) {
                // heals
                if (health < maxHealth - 7)
                    p.setHealth(health + 7);
                else
                    p.setHealth(maxHealth);

                // add to a list to prevent them from souping before the runnable ends
                preventDoubleSoup.add(p.getUniqueId());

                final int heldItemSlot = p.getInventory().getHeldItemSlot();

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // remove the soup
                        preventDoubleSoup.remove(p.getUniqueId());
                        p.getInventory().clear(heldItemSlot);
                        p.updateInventory();
                    }
                }.runTaskLater(plugin, 1L);
            }
            return;
        }

         */

        KitPvPStats stats = plugin.getStats(p);

        // paint ball kit ability
        if ((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && stats.getActiveKit() == KitType.PAINTBALL || VanishStatus.isVanished(p.getUniqueId())) {
            stats.getActiveKit().getKit().onItemUse(p, event.getItem());
            return;
        }

        // gives item back if used in spawn
        if ((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (plugin.isInSpawnArea(p) || VanishStatus.isVanished(p.getUniqueId()))) {
            stats.getActiveKit().getKit().reimburseItem(p, event.getItem());
            return;
        }

        // activates hulk kits ability with empty hand
        if (item == null) {
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && stats.getActiveKit() == KitType.HULK && !p.getGameMode().equals(GameMode.CREATIVE)) {
                if ((stats.isAbilityToggle() && !p.isSneaking()))
                    return;
                stats.getActiveKit().getKit().onItemUse(p, new ItemStack(Material.AIR));
            }
            return;
        }

        // activates block ability
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !p.getGameMode().equals(GameMode.CREATIVE) && !plugin.isInSpawnArea(p)) {
            if ((stats.isAbilityToggle() && !p.isSneaking()))
                return;
            stats.getActiveKit().getKit().onItemUse(p, event.getItem(), event.getClickedBlock());
        }

        // activates air ability
        if ((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && !p.getGameMode().equals(GameMode.CREATIVE)) {
            if ((stats.isAbilityToggle() && !p.isSneaking()))
                return;
            stats.getActiveKit().getKit().onItemUse(p, event.getItem());
        }

    }


    private void addBowl(Player p) {
        Inventory inv = p.getInventory();
        if (inv.contains(Material.BOWL)) {
            ItemStack bowl = inv.getItem(inv.first(Material.BOWL));
            inv.setItem(inv.first(Material.BOWL), new ItemStack(Material.BOWL, bowl.getAmount() + 1));
        } else
            inv.setItem(17, new ItemStack(Material.BOWL));

        p.updateInventory();
    }
}
