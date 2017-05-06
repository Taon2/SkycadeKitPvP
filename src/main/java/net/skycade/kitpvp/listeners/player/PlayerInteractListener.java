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
                e.getPlayer().getItemInHand());
    }

    @EventHandler
    public void onNpcInteract(PlayerInteractEntityEvent e) {
        if (e.getRightClicked().getCustomName() == null)
            return;
        String name = e.getRightClicked().getCustomName();
        Player p = e.getPlayer();

        if (name.equalsIgnoreCase("§5§lCrate"))
            p.chat("/crate");
        else if (name.equalsIgnoreCase("§b§lKits"))
            p.chat("/kits");
        else if (name.equalsIgnoreCase("§6§lShop"))
            p.chat("/shop");
        else if (name.equalsIgnoreCase("§a§lHelp"))
            p.chat("/kitpvphelp");
        else if (name.equalsIgnoreCase("§1§lAchievements"))
            p.chat("/ach");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getItem() == null)
            return;
        if (e.getItem().getType() == Material.MUSHROOM_SOUP) {
            if (p.getHealth() < p.getMaxHealth()) {
                if (p.getHealth() < p.getMaxHealth() - 7) {
                    p.setHealth(p.getHealth() + 7);
                    p.getItemInHand().setType(Material.FERMENTED_SPIDER_EYE);
                    p.getItemInHand().setItemMeta(null);
                    addBowl(p);
                } else {
                    p.setHealth(p.getMaxHealth());
                    p.getItemInHand().setType(Material.FERMENTED_SPIDER_EYE);
                    p.getItemInHand().setItemMeta(null);
                    addBowl(p);
                }
                p.getInventory().remove(Material.FERMENTED_SPIDER_EYE);
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
                    soupInv.addItem(plugin.getStats(p).getActiveKit() == KitType.POTIONMASTER  ?
                            new ItemStack(Material.POTION, 1, (short) 16421) : new ItemStack(Material.MUSHROOM_SOUP, 1));

                p.openInventory(soupInv);

                soupChestCooldown.add(p.getUniqueId());
                Bukkit.getScheduler().runTaskLater(plugin, () -> soupChestCooldown.remove(p.getUniqueId()),
                        KitPvP.getInstance().getConfig().getInt("chest-cooldown") * 20);
                return;
            }
        }
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR)
                || e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !p.getGameMode().equals(GameMode.CREATIVE)) {
            if (plugin.isInSpawnArea(p))
                return;
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
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        soupChestCooldown.remove(e.getPlayer().getUniqueId());
    }

}
