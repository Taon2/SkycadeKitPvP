package me.bukkit.kitpvp.coreclasses.member.listeners;

import com.mongodb.client.FindIterable;
import me.bukkit.kitpvp.KitPvP;
import me.bukkit.kitpvp.coreclasses.member.Member;
import me.bukkit.kitpvp.coreclasses.member.MemberManager;
import me.bukkit.kitpvp.coreclasses.member.Permission;
import me.bukkit.kitpvp.coreclasses.utils.UtilPlayer;
import org.bson.Document;
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
            memberManager.getMembers().put(member.getUUID(), member);
        } else {
            member.put("name", e.getName());
            List<String> previousNames = member.getPreviousNames();
            if (!previousNames.contains(e.getName())) {
                previousNames.add(0, e.getName());
                member.put("previous_names", previousNames);
            }

        }

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
                FindIterable<Document> result = memberManager.getCollection()
                        .find(new Document("_id", member.getDocument().get("_id")));
                Document newDocument = result.first();
                if (newDocument != null) {
                    for (Map.Entry<String, Object> entry : member.getChanges().entrySet())
                        newDocument.put(entry.getKey(), entry.getValue());
                    member.setLogChanges(false);
                    member.getChanges().clear();
                    member.setDocument(newDocument);
                    member.update();
                }
            }
        }.runTaskLaterAsynchronously(KitPvP.getInstance(), 10);
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        Member member = memberManager.getMember(e.getPlayer());

        UtilPlayer.removeAttachment(e.getPlayer());

        if (member != null) {
            Bukkit.getScheduler().runTaskAsynchronously(KitPvP.getInstance(), () -> memberManager.getCollection().updateOne(new Document("_id", member.getDocument().get("_id")),
                    new Document("$set", member.getDocument())));
            Bukkit.getScheduler().runTaskAsynchronously(KitPvP.getInstance(), () ->
                memberManager.getMembers().remove(member.getUUID())
            );
        } else
            e.setQuitMessage(null);
    }

}
