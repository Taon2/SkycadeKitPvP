package net.skycade.kitpvp.coreclasses.member.listeners;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.stat.leaderboards.stats.StatKitPvPCoins;
import net.skycade.kitpvp.stat.leaderboards.stats.StatKitPvPDeaths;
import net.skycade.kitpvp.stat.leaderboards.stats.StatKitPvPKillStreak;
import net.skycade.kitpvp.stat.leaderboards.stats.StatKitPvPKills;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;


public class MemberJoinQuit implements Listener {

    private final MemberManager memberManager;
    private final Map<UUID, Long> lastLogin = new HashMap<>();
    private final long startup;

    public MemberJoinQuit(MemberManager memberManager) {
        this.memberManager = memberManager;
        this.startup = System.currentTimeMillis();
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        if (System.currentTimeMillis() - startup < 2000) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Server starting up...");
            return;
        }
        if (lastLogin.containsKey(e.getUniqueId())
                && System.currentTimeMillis() - lastLogin.get(e.getUniqueId()) < 4000L) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Please wait before re-logging in");
            return;
        }

        lastLogin.put(e.getUniqueId(), System.currentTimeMillis());
        Member member;
        try {
            member = memberManager.getMember(e.getUniqueId(), true);
            if (member == null) {
                member = new Member(e.getUniqueId(), e.getName());
            } else {
                member.setName(e.getName());
            }
            memberManager.getMembers().put(member.getUUID(), member);

            // update the UUIDs when a player joins so that the stats includes them
            StatKitPvPKills.getInstance().update(Collections.singletonList(member.getUUID()), true);
            StatKitPvPCoins.getInstance().update(Collections.singletonList(member.getUUID()), true);
            StatKitPvPDeaths.getInstance().update(Collections.singletonList(member.getUUID()), true);
            StatKitPvPKillStreak.getInstance().update(Collections.singletonList(member.getUUID()), true);
        } catch (Exception a) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Sorry, your data was not loaded correctly! Please re-join!");
            KitPvP.getInstance().getLogger().log(Level.WARNING, "An error occurred while loading player's data.", a);
            memberManager.getMembers().remove(e.getUniqueId());
        }

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Member member = memberManager.getMember(p, true);

        if (member == null) {
            e.getPlayer().kickPlayer("§cSorry, your data was not loaded correctly! Please re-join!");
            return;
        }

        // Update name
        if (!p.getName().equals(member.getName()))
            member.setName(p.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Member member = memberManager.getMember(e.getPlayer());

        UtilPlayer.removeAttachment(e.getPlayer());

        if (member != null) {
            MemberManager.getInstance().update(member, true);
        } else
            e.setQuitMessage(null);
    }

}
