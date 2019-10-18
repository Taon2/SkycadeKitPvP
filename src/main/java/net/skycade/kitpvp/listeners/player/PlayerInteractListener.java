package net.skycade.kitpvp.listeners.player;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerInteractListener implements Listener {

    private final KitPvP plugin;
    private final List<UUID> soupChestCooldown = new ArrayList<>();

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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRightClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getItem() == null)
            return;
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
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock().getType() == Material.CHEST) {
                if (soupChestCooldown.contains(p.getUniqueId()))
                    return;
                e.setCancelled(true);
                Inventory soupInv = Bukkit.createInventory(p, 45, "Soup");
                for (int i = 0; i < soupInv.getSize(); i++)
                    soupInv.addItem(plugin.getStats(p).getActiveKit() == KitType.POTIONMASTER ?
                            new ItemStack(Material.POTION, 1, (short) 16421) : new ItemStack(Material.MUSHROOM_SOUP, 1));

                p.openInventory(soupInv);

                soupChestCooldown.add(p.getUniqueId());
                Bukkit.getScheduler().runTaskLater(plugin, () -> soupChestCooldown.remove(p.getUniqueId()),
                        KitPvP.getInstance().getConfig().getInt("chest-cooldown") * 20);
                return;
            }
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

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        soupChestCooldown.remove(e.getPlayer().getUniqueId());
    }

}
