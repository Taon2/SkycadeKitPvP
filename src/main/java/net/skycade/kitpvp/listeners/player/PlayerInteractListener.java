package net.skycade.kitpvp.listeners.player;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerInteractListener implements Listener {

    private final KitPvP plugin;

    public PlayerInteractListener(KitPvP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof Player))
            return;
        if (plugin.isInSpawnArea(e.getPlayer()))
            return;
        plugin.getStats(e.getPlayer()).getActiveKit().getKit().onInteract(e.getPlayer(), (Player) e.getRightClicked(),
                e.getPlayer().getInventory().getItemInHand());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (plugin.isInSpawnArea(e.getPlayer()) || e.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return;

        KitType type = plugin.getStats(e.getPlayer()).getActiveKit();
        if (type == KitType.BUILDUHC)
            type.getKit().onBlockPlace(e.getPlayer(), e.getBlock());
        else
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRightClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getItem() == null) {
            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && plugin.getStats(p).getActiveKit().equals(KitType.HULK) && !p.getGameMode().equals(GameMode.CREATIVE) && !plugin.isInSpawnArea(p)) {
                plugin.getStats(p).getActiveKit().getKit().onItemUse(p, new ItemStack(Material.AIR));
            }
            return;
        }
        if (e.getItem().getType() == Material.MUSHROOM_SOUP) {
            double maxHealth = p.getMaxHealth();
            if (p.getHealth() < maxHealth) {
                if (p.getHealth() < maxHealth - 7) {
                    p.setHealth(p.getHealth() + 7);
                } else {
                    p.setHealth(maxHealth);
                }
                final int heldItemSlot = e.getPlayer().getInventory().getHeldItemSlot();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        e.getPlayer().getInventory().clear(heldItemSlot);
                        p.updateInventory();
                        addBowl(p);
                    }
                }.runTaskLater(plugin, 1L);

            }
            return;
        }

        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !p.getGameMode().equals(GameMode.CREATIVE) && !plugin.isInSpawnArea(p)) {
            plugin.getStats(p).getActiveKit().getKit().onItemUse(p, e.getItem());
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
