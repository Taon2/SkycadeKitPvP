package net.skycade.kitpvp.listeners.player;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.kit.kits.KitMedic;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

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

}
