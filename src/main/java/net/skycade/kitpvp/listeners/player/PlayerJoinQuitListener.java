package net.skycade.kitpvp.listeners.player;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.scheduler.BukkitRunnable;

import static net.skycade.kitpvp.Messages.ALL_KITS_UNLOCKED;


public class PlayerJoinQuitListener implements Listener {

    private final KitPvP plugin;

    public PlayerJoinQuitListener(KitPvP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        Player p = e.getPlayer();

        // Reset killstreak
        Member member = MemberManager.getInstance().getMember(p);
        if (member == null) return;
        if (!plugin.getSpawnRegion().contains(p) && !member.getPlayer().hasPermission(new Permission("kitpvp.admin", PermissionDefault.OP)))
            plugin.getStats(e.getPlayer()).setStreak(0);

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        Player p = e.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!p.isOnline()) return;

                KitPvPStats stats = plugin.getStats(p);

                if (!stats.getActiveKit().getKit().isEnabled()) {
                    stats.setActiveKit(KitType.DEFAULT);
                    stats.getActiveKit().getKit().giveSoup(p, 32);
                }

                if (plugin.isInSpawnArea(p)) {
                    stats.getActiveKit().getKit().applyKit(p);
                    stats.getActiveKit().getKit().giveSoup(p, 32);
                }

                //Unlock KitMaster
                if (KitPvP.getInstance().getAvailableKits() - 1 == stats.getKits().size()) {
                    ALL_KITS_UNLOCKED.msg(p);
                    stats.addKit(KitType.KITMASTER);
                }

                ScoreboardInfo.getInstance().updatePlayer(p);
                p.teleport(KitPvP.getInstance().getSpawnLocation());

            }
        }.runTaskLater(KitPvP.getInstance(), 1L);
    }
}
