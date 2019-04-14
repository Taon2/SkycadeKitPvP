package net.skycade.kitpvp.ui.eventshopitems.items;

import net.skycade.kitpvp.ui.EventShopMenu;
import net.skycade.kitpvp.ui.eventshopitems.EventShopItem;
import net.skycade.kitpvp.ui.eventshopitems.EventShopManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ItemCoinBoost extends EventShopItem {

    private YamlConfiguration yaml;
    private EventShopManager eventShopManager;

    public ItemCoinBoost(EventShopManager eventShopManager) {
        super(eventShopManager, "§6Double Coins Upgrade", new ItemStack(Material.DOUBLE_PLANT, 1, (short) 0), 50, 600);
        this.eventShopManager = eventShopManager;
    }

    public void giveReward(Player p) {
        this.yaml = eventShopManager.getYaml();
        long now = System.currentTimeMillis();
        yaml.set((p.getUniqueId() + "." + getName()), now);
        eventShopManager.setYaml(yaml);
        eventShopManager.save();
    }

    public void reapplyReward(Player p) {
    }

    public List<String> getDescription() {
        return Arrays.asList(
                ChatColor.WHITE + "Receive double the coins",
                ChatColor.WHITE + "gained from killing players.",
                ChatColor.GOLD + "Price: " + ChatColor.WHITE + "50 Tokens.",
                ChatColor.GOLD + "Duration: " + ChatColor.WHITE + "10 Minutes.", "",
                ChatColor.GRAY + "Click to buy this upgrade."
        );
    }
}
