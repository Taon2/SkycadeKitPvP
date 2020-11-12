package net.skycade.kitpvp.ui.eventshopitems.items;

import net.skycade.SkycadeCore.Localization;
import net.skycade.crates.CrateUser;
import net.skycade.crates.CratesPlugin;
import net.skycade.crates.crates.Crate;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.stat.KitPvPStats;
import net.skycade.kitpvp.ui.eventshopitems.EventShopItem;
import net.skycade.kitpvp.ui.eventshopitems.EventShopManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ItemKitCrateKeyTransfer extends EventShopItem {

    public ItemKitCrateKeyTransfer(EventShopManager eventShopManager) {
        super(eventShopManager, "Kit Crate Key Transfer", new ItemStack(Material.TRIPWIRE_HOOK, 1, (short) 0), 0, 0, true);
    }

    public void giveReward(Player p) {
        Crate crate = CratesPlugin.getInstance().getEditorModule().getCrate("kitcrate");

        if (crate == null) {
            p.sendMessage(ChatColor.RED + "That crate doesn't exist.");
            return;
        }

        if (CrateUser.get(p.getUniqueId()).hasKey(crate)) {
            crate.getKey().take(p.getUniqueId(), 1);

            KitPvPStats stats = KitPvP.getInstance().getStats(p);
            stats.giveCoins(500);
        } else {
            p.sendMessage(Localization.getInstance().tl("skycade.crates.open.nokey"));
        }
    }

    public void reapplyReward(Player p) {
    }

    public List<String> getDescription() {
        return Arrays.asList(
                ChatColor.GRAY + "",
                ChatColor.GRAY + "Transfer 1 Kit Crate key",
                ChatColor.GRAY + "into " +ChatColor.GOLD + "500 Coins" + ChatColor.GRAY + "."
        );
    }
}
