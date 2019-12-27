package net.skycade.kitpvp.coreclasses.member.listeners;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.stat.leaderboards.stats.StatKitPvPCoins;
import net.skycade.kitpvp.stat.leaderboards.stats.StatKitPvPDeaths;
import net.skycade.kitpvp.stat.leaderboards.stats.StatKitPvPKillStreak;
import net.skycade.kitpvp.stat.leaderboards.stats.StatKitPvPKills;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.logging.Level;


public class MemberJoinQuit implements Listener {

    private final MemberManager memberManager;
//    private final Map<UUID, Long> lastLogin = new HashMap<>();
//    private final long startup;

    public MemberJoinQuit(MemberManager memberManager) {
        this.memberManager = memberManager;
//        this.startup = System.currentTimeMillis();
    }

//    @EventHandler
//    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
//        if (System.currentTimeMillis() - startup < 2000) {
//            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Server starting up...");
//            return;
//        }
//        if (lastLogin.containsKey(event.getUniqueId())
//                && System.currentTimeMillis() - lastLogin.get(event.getUniqueId()) < 4000L) {
//            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Please wait before re-logging in");
//            return;
//        }
//
//        lastLogin.put(event.getUniqueId(), System.currentTimeMillis());
//        Member member;
//        try {
//            member = memberManager.getMember(event.getUniqueId(), true);
//            if (member == null) {
//                member = new Member(event.getUniqueId(), event.getName());
//            } else {
//                member.setName(event.getName());
//            }
//            memberManager.getMembers().put(member.getUUID(), member);
//        } catch (Exception a) {
//            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Sorry, your data was not loaded correctly! Please re-join!");
//            KitPvP.getInstance().getLogger().log(Level.WARNING, "An error occurred while loading player's data.", a);
//            memberManager.getMembers().remove(event.getUniqueId());
//        }
//
//        // update the UUIDs when a player joins so that the stats includes them
//        StatKitPvPKills.getInstance().update(Collections.singletonList(event.getUniqueId()), true);
//        StatKitPvPCoins.getInstance().update(Collections.singletonList(event.getUniqueId()), true);
//        StatKitPvPDeaths.getInstance().update(Collections.singletonList(event.getUniqueId()), true);
//        StatKitPvPKillStreak.getInstance().update(Collections.singletonList(event.getUniqueId()), true);
//    }

    @EventHandler (ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        Member member;
        try {
            member = memberManager.getMember(p.getUniqueId(), true);
            if (member == null) {
                member = new Member(p.getUniqueId(), p.getName());
            } else {
                member.setName(p.getName());
            }
            memberManager.getMembers().put(member.getUUID(), member);
        } catch (Exception a) {
            event.getPlayer().kickPlayer("§cSorry, your data was not loaded correctly! Please re-join!");
            KitPvP.getInstance().getLogger().log(Level.WARNING, "An error occurred while loading player's data.", a);
            memberManager.getMembers().remove(p.getUniqueId());
            return;
        }

        // update the UUIDs when a player joins so that the stats includes them
        StatKitPvPKills.getInstance().update(Collections.singletonList(p.getUniqueId()), true);
        StatKitPvPCoins.getInstance().update(Collections.singletonList(p.getUniqueId()), true);
        StatKitPvPDeaths.getInstance().update(Collections.singletonList(p.getUniqueId()), true);
        StatKitPvPKillStreak.getInstance().update(Collections.singletonList(p.getUniqueId()), true);

        // Update name
        if (!p.getName().equals(member.getName()))
            member.setName(p.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Member member = memberManager.getMember(event.getPlayer());

        UtilPlayer.removeAttachment(event.getPlayer());

        if (member != null) {
            MemberManager.getInstance().update(member, true);
        } else
            event.setQuitMessage(null);
    }

}
