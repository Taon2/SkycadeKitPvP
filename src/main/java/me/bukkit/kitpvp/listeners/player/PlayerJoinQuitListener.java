package me.bukkit.kitpvp.listeners.player;

import me.bukkit.kitpvp.KitPvP;
import me.bukkit.kitpvp.coreclasses.member.MemberManager;
import me.bukkit.kitpvp.kit.KitType;
import me.bukkit.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class PlayerJoinQuitListener implements Listener {

    private final KitPvP plugin;

    public PlayerJoinQuitListener(KitPvP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        Player p = e.getPlayer();

        // Reset killstreak
        if (!plugin.getSpawnRegion().contains(p) && !MemberManager.getInstance().getMember(p).isStaff())
            plugin.getStats(e.getPlayer()).setStreak(0);

        plugin.getStats().remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void on(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        Player p = e.getPlayer();

        KitPvPStats stats = plugin.getStats(p);
        //TODO: remove
        plugin.getKitManager().getKits().keySet().forEach(stats::addKit);

        if (!stats.getActiveKit().getKit().isEnabled())
            stats.setActiveKit(KitType.DEFAULT);

        //Unlock KitMaster
        if (KitType.values().length - 1 == stats.getKits().size()) {
            p.sendMessage("§7You unlocked §aall §7the kits! You unlocked the §aKitMaster §7kit.");
            stats.addKit(KitType.KITMASTER);
        }
    }

    @EventHandler
    public void onFirstJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        KitPvPStats stats = plugin.getStats(p);
        //TODO: remove
        plugin.getKitManager().getKits().keySet().forEach(stats::addKit);

        if (p.hasPlayedBefore())
            return;

        sendDelayedMessage(p, 0, "Welcome to §aKitPvP§7!");
        sendDelayedMessage(p, 3, "Type §a/kits §7to view your kits.");
        sendDelayedMessage(p, 6, "Every kit can be §aupgraded §7to level 3 with the §a/upgrade §7command.");
        sendDelayedMessage(p, 9, "Upgrading or buying new kits from the §a/shop §7will cost coins.");
        sendDelayedMessage(p, 12,
                "You already start out with some coins, so try to §aupgrade your default kit §7by typing §a/upgrade §7and right clicking the kit.");
        sendDelayedMessage(p, 15, "You can earn §acoins §7from kills, assists and §avoting §7(/vote).");
        sendDelayedMessage(p, 18,
                "Some kits have a §aspecial ability§7, they can be activated by right clicking with your sword or by using a special item.");
        sendDelayedMessage(p, 21,
                "New kits can also be unlocked by using the §acrate system (/crate)§7, this will require a §acrate key§7.");
        sendDelayedMessage(p, 24, "You can get §akeys §7from voting and by completing §aachievements (/ach)§7.");
        sendDelayedMessage(p, 27, "Type §a/kitpvphelp §7for more kitpvp commands.");
        sendDelayedMessage(p, 30, "Good luck on the battlefield "
                + p.getName() + "§7!");
    }

    private void sendDelayedMessage(Player p, int secondsDelay, String message) {
        if (!p.isOnline())
            return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> p.sendMessage("§7" + message), secondsDelay * 20);
    }


}
