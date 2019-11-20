package net.skycade.kitpvp.ui.eventshopitems.items;

import net.skycade.kitpvp.KitPvP;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ItemKeepKillstreak extends EventShopItem {

    private EventShopManager eventShopManager;

    public ItemKeepKillstreak(EventShopManager eventShopManager) {
        super(eventShopManager, ChatColor.RED + "Keep Killstreak", new ItemStack(Material.DIAMOND_SWORD), 100, 0);
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
        if (eventShopManager.isKeepingKs(p)) {
            KitPvPStats stats = eventShopManager.getKitPvP().getStats(p);
            stats.setStreak(stats.getLastStreak());
            ScoreboardInfo.getInstance().updatePlayer(p);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getFrom().getBlock().equals(e.getTo().getBlock())) return;

        Player p = e.getPlayer();

        if (KitPvP.getInstance().getSpawnRegion().contains(e.getFrom()) && KitPvP.getInstance().getSpawnRegion().contains(e.getTo()) && eventShopManager.isKeepingKs(p)) {
            YamlConfiguration yaml = eventShopManager.getYaml();
            yaml.set((p.getUniqueId() + "." + getName()), false);
            eventShopManager.setYaml(yaml);
            eventShopManager.save();
        } else if (KitPvP.getInstance().getSpawnRegion().contains(p) && eventShopManager.isKeepingKs(p)){
            KitPvPStats stats = eventShopManager.getKitPvP().getStats(p);
            stats.setStreak(stats.getLastStreak());
            ScoreboardInfo.getInstance().updatePlayer(p);
        }
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                ChatColor.WHITE + "Keep your Killstreak",
                ChatColor.WHITE + "through your next death.",
                ChatColor.GOLD + "Price: " + ChatColor.WHITE + getPrice() + " Tokens.",
                ChatColor.GRAY + "Click to buy this upgrade."
        );
    }
}