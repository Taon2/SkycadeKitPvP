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

    private YamlConfiguration yaml;
    private EventShopManager eventShopManager;

    public ItemKeepKillstreak(EventShopManager eventShopManager) {
        super(eventShopManager, "§cKeep Kill Streak", new ItemStack(Material.DIAMOND_SWORD), 175, 60);
        this.eventShopManager = eventShopManager;
    }

    @Override
    public void giveReward(Player p) {
        this.yaml = eventShopManager.getYaml();
        long now = System.currentTimeMillis();
        yaml.set((p.getUniqueId() + "." + getName()), now);
        eventShopManager.setYaml(yaml);
        eventShopManager.save();
    }

    @Override
    public void reapplyReward(Player p) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (eventShopManager.getYaml().contains(p.getUniqueId().toString()) && eventShopManager.getYaml().contains(p.getUniqueId().toString() + "." + getName()) && (System.currentTimeMillis() - eventShopManager.getYaml().getLong(p.getUniqueId().toString() + "." + getName())) / 1000L < getDuration()) {
            KitPvPStats stats = eventShopManager.getKitPvP().getStats(p);
            stats.setStreak(stats.getLastStreak());
            ScoreboardInfo.getInstance().updatePlayer(p);
        }
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                ChatColor.WHITE + "Keep your Kill Streak through",
                ChatColor.WHITE + "death for the next minute.",
                ChatColor.GOLD + "Price: " + ChatColor.WHITE + getPrice() + " Tokens.",
                ChatColor.GOLD + "Duration: " + ChatColor.WHITE + getDuration()/60 + " Minute.", "",
                ChatColor.GRAY + "Click to buy this upgrade."
        );
    }
}
