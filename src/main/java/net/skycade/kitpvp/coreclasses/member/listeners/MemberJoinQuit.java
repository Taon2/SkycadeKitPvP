package net.skycade.kitpvp.coreclasses.member.listeners;

import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


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
        Member member = memberManager.getMember(p, true);

        if (member == null) {
            e.getPlayer().kickPlayer("§cSorry, your data was not loaded correctly! Please re-join!");
            return;
        }

        // Update name
        if (!p.getName().equals(member.getName()))
            member.setName(p.getName());
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        Member member = memberManager.getMember(e.getPlayer());

        UtilPlayer.removeAttachment(e.getPlayer());

        if (member != null) {
            MemberManager.getInstance().update(member, true);
            /* Bukkit.getScheduler().runTaskAsynchronously(KitPvP.getInstance(), () ->
                memberManager.getMembers().remove(member.getUUID())
            ); */
        } else
            e.setQuitMessage(null);
    }

}
