package net.skycade.kitpvp.listeners.player;

import net.skycade.SkycadeCore.utility.TeleportUtil;
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
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player p = event.getPlayer();

        // Reset killstreak
        Member member = MemberManager.getInstance().getMember(p);
        if (member == null) return;
        if (!plugin.getSpawnRegion().contains(p) && !member.getPlayer().hasPermission(new Permission("kitpvp.admin", PermissionDefault.OP)) && !KitPvP.getInstance().getEventShopManager().isKeepingKs(p))
            plugin.getStats(p).setStreak(0);

        plugin.getStats(p).getActiveKit().getKit().stopItemRunnables(p);
        plugin.getStats(p).getActiveKit().getKit().cancelRunnables(p);

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player p = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!p.isOnline()) return;

                KitPvPStats stats = plugin.getStats(p);

                // Gives every kit, only used during beta testing.
//                KitPvP.getInstance().getKitManager().getKits().forEach((kitType, kit) -> {
//                    if (kit.isEnabled())
//                        stats.addKit(kitType);
//                });
                if (!stats.getActiveKit().getKit().isEnabled()) {
                    stats.setActiveKit(KitType.CHANCE);
                    stats.getActiveKit().getKit().giveSoup(p, 32);
                }

                if (plugin.isInSpawnArea(p)) {
                    stats.getActiveKit().getKit().beginApplyKit(p);
                    stats.getActiveKit().getKit().giveSoup(p, 32);
                }

                //Unlock KitMaster
                if (KitPvP.getInstance().getAvailableKits() - 1 == stats.getKits().size()) {
                    ALL_KITS_UNLOCKED.msg(p);
                    stats.addKit(KitType.KITMASTER);
                }

                ScoreboardInfo.getInstance().updatePlayer(p);
                p.teleport(TeleportUtil.getSpawn());
            }
        }.runTaskLater(KitPvP.getInstance(), 1L);
    }
}
