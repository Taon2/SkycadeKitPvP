package net.skycade.kitpvp.listeners.player;

import net.skycade.SkycadeCore.vanish.VanishStatus;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.kit.KitType;
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();

        // applies soup to the player, runs first for soup reg reasons
        if (event.getItem() != null && event.getItem().getType() == Material.MUSHROOM_SOUP) {
            double maxHealth = p.getMaxHealth();
            if (p.getHealth() < maxHealth) {
                if (p.getHealth() < maxHealth - 7) {
                    p.setHealth(p.getHealth() + 7);
                } else {
                    p.setHealth(maxHealth);
                }
                final int heldItemSlot = event.getPlayer().getInventory().getHeldItemSlot();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        event.getPlayer().getInventory().clear(heldItemSlot);
                        p.updateInventory();
                        addBowl(p);
                    }
                }.runTaskLater(plugin, 1L);

            }
            return;
        }

        // gives item back if used in spawn
        if ((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (plugin.isInSpawnArea(p) || VanishStatus.isVanished(event.getPlayer().getUniqueId()))) {
            plugin.getStats(event.getPlayer()).getActiveKit().getKit().reimburseItem(event.getPlayer(), event.getItem());
            return;
        }

        // activates hulk kits ability with empty hand
        if (event.getItem() == null) {
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && plugin.getStats(p).getActiveKit() == KitType.HULK && !p.getGameMode().equals(GameMode.CREATIVE)) {
                if ((plugin.getStats(event.getPlayer()).isAbilityToggle() && !event.getPlayer().isSneaking()))
                    return;
                plugin.getStats(p).getActiveKit().getKit().onItemUse(p, new ItemStack(Material.AIR));
            }
            return;
        }

        // activates block ability
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !p.getGameMode().equals(GameMode.CREATIVE) && !plugin.isInSpawnArea(p)) {
            if ((plugin.getStats(event.getPlayer()).isAbilityToggle() && !event.getPlayer().isSneaking()))
                return;
            plugin.getStats(p).getActiveKit().getKit().onItemUse(p, event.getItem(), event.getClickedBlock());
        }

        // activates air ability
        if ((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && !p.getGameMode().equals(GameMode.CREATIVE)) {
            if ((plugin.getStats(event.getPlayer()).isAbilityToggle() && !event.getPlayer().isSneaking()))
                return;
            plugin.getStats(p).getActiveKit().getKit().onItemUse(p, event.getItem());
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
