package net.skycade.kitpvp.ui.eventshopitems.items;

import net.skycade.kitpvp.ui.eventshopitems.EventShopItem;
import net.skycade.kitpvp.ui.eventshopitems.EventShopManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ItemRefreshKitCooldown extends EventShopItem {

    private final EventShopManager eventShopManager;

    public ItemRefreshKitCooldown(EventShopManager eventShopManager) {
        super(eventShopManager, "Permanently Reduce RefreshKit Cooldown", new ItemStack(Material.CHEST, 1, (short) 0), 400, 0, false);
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

    public List<String> getDescription() {
        return Arrays.asList(
                ChatColor.GRAY + "",
                ChatColor.GRAY + "Permanently reduce the cooldown",
                ChatColor.GRAY + "of /refreshkit to 1.5 hours."
        );
    }
}
