package net.skycade.kitpvp.listeners.player;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.scoreboard.ScoreboardHandler;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.scheduler.BukkitRunnable;


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
        Member member = MemberManager.getInstance().getMember(p);
        if (member == null) return;
        if (!plugin.getSpawnRegion().contains(p) && !member.getPlayer().hasPermission(new Permission("kitpvp.admin", PermissionDefault.OP)))
            plugin.getStats(e.getPlayer()).setStreak(0);

    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        Player p = e.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!p.isOnline()) return;

                KitPvPStats stats = plugin.getStats(p);

                if (!stats.getActiveKit().getKit().isEnabled())
                    stats.setActiveKit(KitType.DEFAULT);

                if (plugin.isInSpawnArea(p)) {
                    stats.getActiveKit().getKit().applyKit(p);
                }

                //Unlock KitMaster
                if (KitPvP.getInstance().getAvailableKits() - 1 == stats.getKits().size()) {
                    p.sendMessage("§7You unlocked §aall §7the kits! You unlocked the §aKitMaster §7kit.");
                    stats.addKit(KitType.KITMASTER);
                }

                ScoreboardHandler.updatePlayer(p);
            }
        }.runTaskLater(KitPvP.getInstance(), 1L);
    }

    @EventHandler
    public void onFirstJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (p.hasPlayedBefore())
            return;

        sendDelayedMessage(p, 0, "Welcome to §aKitPvP§7!");
        sendDelayedMessage(p, 3, "Type §a/kits §7to view your kits. You start off with three unlocked.");
        sendDelayedMessage(p, 6, "You can purchase more kits through §a/shop §7using §acoins§7.");
        sendDelayedMessage(p, 9, "You can earn §acoins §7from kills, assists, and events.");
        sendDelayedMessage(p, 12, "New kits can also be unlocked by using the §acrate system (/crate)§7, this will require a §acrate key§7.");
        sendDelayedMessage(p, 15, "You can get §akeys §7from §astore.skycade.net");
        sendDelayedMessage(p, 18, "Some kits have a §aspecial ability§7, they can be activated by right clicking with your sword or by using a special item.");
        sendDelayedMessage(p, 21, "Type §a/kitpvphelp §7for more kitpvp commands.");
        sendDelayedMessage(p, 24, "Good luck on the battlefield " + p.getName() + "§7!");
    }

    private void sendDelayedMessage(Player p, int secondsDelay, String message) {
        if (!p.isOnline())
            return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> p.sendMessage("§7" + message), secondsDelay * 20);
    }


}
