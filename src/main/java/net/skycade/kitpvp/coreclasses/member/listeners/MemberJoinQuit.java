package net.skycade.kitpvp.coreclasses.member.listeners;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.member.Permission;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.stat.KitPvPDB;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


public class MemberJoinQuit implements Listener {

    private final MemberManager memberManager;
    private final Map<UUID, Long> lastLogin = new HashMap<>();
    private final long startup;

    public MemberJoinQuit(MemberManager memberManager) {
        this.memberManager = memberManager;
        this.startup = System.currentTimeMillis();
    }

    @EventHandler
    public void on(AsyncPlayerPreLoginEvent e) {
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
        Member member = memberManager.getMember(e.getUniqueId(), true);
        if (member == null) {
            member = new Member(e.getUniqueId(), e.getName());
        } else {
            member.setName(e.getName());
            /* List<String> previousNames = member.getPreviousNames();
            if (!previousNames.contains(e.getName())) {
                member.addPreviousName(e.getName());
            } setName already handles this no? */
        }
        memberManager.getMembers().put(member.getUUID(), member);

    }

    @EventHandler
    public void on(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Member member = memberManager.getMember(p);

        PermissionAttachment perms = UtilPlayer.getAttachment(e.getPlayer(), KitPvP.getInstance());
        List<Permission> groups = member.getPermissions(true);
        if (groups.isEmpty()) {
            member.addPermission(Permission.NONE);
            groups.add(Permission.NONE);
        }
        Collections.sort(groups);

        for (Permission group : groups) {
            for (String perm : group.getPermissions()) {
                if (perm.startsWith("-")) {
                    perms.setPermission(perm.substring(1), false);
                } else {
                    perms.setPermission(perm, true);
                }
            }
            perms.setPermission(group.name().toLowerCase(), true);
        }

        // Update name
        if (!p.getName().equals(member.getName()))
            member.setName(p.getName());

        // This is because the Mongo doesn't update fast enough on leave to be
        // up to date by the time they change server. Its not pretty, but it
        // works.
        new BukkitRunnable() {
            public void run() {
                member.update();
            }
        }.runTaskLaterAsynchronously(KitPvP.getInstance(), 10);
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        Member member = memberManager.getMember(e.getPlayer());

        UtilPlayer.removeAttachment(e.getPlayer());

        if (member != null) {
            Bukkit.getScheduler().runTaskAsynchronously(KitPvP.getInstance(), () ->
                            KitPvPDB.getInstance().setMemberData(member.getUUID(), member.getName(), member.getPreviousNames(), member.getRawPermissions(), member.getKills(), member.getHighestStreak(), member.getKills(), member.getProperties()));
            Bukkit.getScheduler().runTaskAsynchronously(KitPvP.getInstance(), () ->
                memberManager.getMembers().remove(member.getUUID())
            );
        } else
            e.setQuitMessage(null);
    }

}
