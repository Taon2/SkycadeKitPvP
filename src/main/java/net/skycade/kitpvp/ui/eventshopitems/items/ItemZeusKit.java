package net.skycade.kitpvp.ui.eventshopitems.items;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.stat.KitPvPStats;
import net.skycade.kitpvp.ui.eventshopitems.EventShopItem;
import net.skycade.kitpvp.ui.eventshopitems.EventShopManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ItemZeusKit extends EventShopItem {

    private EventShopManager eventShopManager;

    public ItemZeusKit(EventShopManager eventShopManager) {
        super(eventShopManager, "Zeus Kit", new ItemStack(Material.BLAZE_ROD, 1, (short) 0), 250, 0, false);
        this.eventShopManager = eventShopManager;
    }

    public void giveReward(Player p) {
        KitPvPStats stats = KitPvP.getInstance().getStats(p);
        stats.addKit(KitType.ZEUS);

        YamlConfiguration yaml = eventShopManager.getYaml();
        long now = System.currentTimeMillis();
        yaml.set((p.getUniqueId() + "." + getName()), now);
        eventShopManager.setYaml(yaml);
        eventShopManager.save();
    }

    public void reapplyReward(Player p) {
    }

    public List<String> getDescription() {
        return Arrays.asList(
                ChatColor.GRAY + "",
                ChatColor.GRAY + "Unlock kit Zeus."
        );
    }
}
