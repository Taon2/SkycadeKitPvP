package net.skycade.kitpvp.ui.eventshopitems.items;

import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPStats;
import net.skycade.kitpvp.ui.eventshopitems.EventShopItem;
import net.skycade.kitpvp.ui.eventshopitems.EventShopManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ItemKeepKillstreak extends EventShopItem {

    private EventShopManager eventShopManager;

    public ItemKeepKillstreak(EventShopManager eventShopManager) {
        super(eventShopManager, ChatColor.RED + "Keep Kill Streak", new ItemStack(Material.DIAMOND_SWORD), 100, 0);
        this.eventShopManager = eventShopManager;
    }

    @Override
    public void giveReward(Player p) {
        YamlConfiguration yaml = eventShopManager.getYaml();
        yaml.set((p.getUniqueId() + "." + getName()), true);
        eventShopManager.setYaml(yaml);
        eventShopManager.save();
    }

    @Override
    public void reapplyReward(Player p) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (eventShopManager.getYaml().contains(p.getUniqueId().toString()) && eventShopManager.getYaml().contains(p.getUniqueId().toString() + "." + getName()) && eventShopManager.getYaml().getBoolean(p.getUniqueId().toString() + "." + getName())) {
            KitPvPStats stats = eventShopManager.getKitPvP().getStats(p);
            stats.setStreak(stats.getLastStreak());
            ScoreboardInfo.getInstance().updatePlayer(p);

            YamlConfiguration yaml = eventShopManager.getYaml();
            yaml.set((p.getUniqueId() + "." + getName()), false);
            eventShopManager.setYaml(yaml);
            eventShopManager.save();
        }
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                ChatColor.WHITE + "Keep your Kill Streak",
                ChatColor.WHITE + "through your next death.",
                ChatColor.GOLD + "Price: " + ChatColor.WHITE + getPrice() + " Tokens.",
                ChatColor.GRAY + "Click to buy this upgrade."
        );
    }
}
