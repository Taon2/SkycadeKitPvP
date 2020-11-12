package net.skycade.kitpvp.ui.eventshopitems.items;

import net.skycade.SkycadeCore.Localization;
import net.skycade.crates.CrateUser;
import net.skycade.crates.CratesPlugin;
import net.skycade.crates.crates.Crate;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.stat.KitPvPStats;
import net.skycade.kitpvp.ui.HostMenu;
import net.skycade.kitpvp.ui.eventshopitems.EventShopItem;
import net.skycade.kitpvp.ui.eventshopitems.EventShopManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class ItemPlayerHostEvent extends EventShopItem {

    public ItemPlayerHostEvent(EventShopManager eventShopManager) {
        super(eventShopManager, "Player Host Event", new ItemStack(Material.EMERALD, 1, (short) 0), 0, 0, true);
    }

    public void giveReward(Player p) {
        p.closeInventory();
        new BukkitRunnable() {
            @Override
            public void run() {
                new HostMenu().open(p);
            }
        }.runTaskLater(KitPvP.getInstance(), 2);
    }

    public void reapplyReward(Player p) {
    }

    public List<String> getDescription() {
        return Arrays.asList(
                ChatColor.GRAY + "",
                ChatColor.GRAY + "Host an event for players to participate in!",
                ChatColor.GRAY + " ",
                ChatColor.GRAY + "Possible events:",
                ChatColor.WHITE + "Sumo",
                ChatColor.WHITE + "Brackets",
                ChatColor.WHITE + "Last Man Standing"
        );
    }
}
