package net.skycade.kitpvp.ui.eventshopitems.items;

import net.skycade.kitpvp.bukkitevents.KitPvPCoinsRewardEvent;
import net.skycade.kitpvp.ui.eventshopitems.EventShopItem;
import net.skycade.kitpvp.ui.eventshopitems.EventShopManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ItemCoinBoost extends EventShopItem {

    private final EventShopManager eventShopManager;

    public ItemCoinBoost(EventShopManager eventShopManager) {
        super(eventShopManager, "Double Coins Upgrade", new ItemStack(Material.DOUBLE_PLANT, 1, (short) 0), 50, 600, true);
        this.eventShopManager = eventShopManager;
    }

    public void giveReward(Player p) {
        YamlConfiguration yaml = eventShopManager.getYaml();
        long now = System.currentTimeMillis();
        yaml.set((p.getUniqueId() + "." + getName()), now);
        eventShopManager.setYaml(yaml);
        eventShopManager.save();
    }

    public void reapplyReward(Player p) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onKitPvPCoinsReward(KitPvPCoinsRewardEvent e) {
        Player p = e.getPlayer();
        if (eventShopManager.getYaml().contains(p.getUniqueId().toString()) && eventShopManager.getYaml().contains(p.getUniqueId().toString() + "." + getName()) && (System.currentTimeMillis() - eventShopManager.getYaml().getLong(p.getUniqueId().toString() + "." + getName())) / 1000L < getDuration()) {
            e.setNewCoins(e.getCoins() * 2);
        }
    }

    public List<String> getDescription() {
        return Arrays.asList(
                ChatColor.GRAY + "",
                ChatColor.GRAY + "Receive double the coins",
                ChatColor.GRAY + "gained from killing players."
        );
    }
}
