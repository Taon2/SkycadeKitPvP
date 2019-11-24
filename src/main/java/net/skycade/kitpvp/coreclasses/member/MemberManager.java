package net.skycade.kitpvp.coreclasses.member;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.listeners.MemberJoinQuit;
import net.skycade.kitpvp.coreclasses.member.listeners.MemberListeners;
import net.skycade.kitpvp.stat.KitPvPDB;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemberManager {

    private static MemberManager instance;

    private final Map<UUID, Member> members = new HashMap<>();

    private MemberManager() {
        Bukkit.getPluginManager().registerEvents(new MemberJoinQuit(this), KitPvP.getInstance());
        Bukkit.getPluginManager().registerEvents(new MemberListeners(this), KitPvP.getInstance());
    }

    public Map<UUID, Member> getMembers() {
        return members;
    }

    public Member getMember(Player p, boolean database) {
        return getMember(p.getUniqueId(), database);
    }

    public Member getMember(Player p) {
        return getMember(p.getUniqueId());
    }

    public Member getMember(UUID uuid) {
        return getMember(uuid, false);
    }

    public Member getOfflineMember(UUID uuid) {
        return getMember(uuid, true);
    }

    public Member getMember(UUID uuid, boolean database) {
        if (members.containsKey(uuid))
            return members.get(uuid);
        if (database) {
            Member member = KitPvPDB.getInstance().getMemberData(uuid);
            return member;
        }
        return null;
    }

    public Member getMember(String name) {
        for (Member member : members.values())
            if (member.getName().equalsIgnoreCase(name))
                return member;

        if (name.length() < 3 && !name.equalsIgnoreCase("G") && !name.equalsIgnoreCase("F")
                && !name.equalsIgnoreCase("8"))
            return null;

        UUID uuid = KitPvPDB.getInstance().getUUIDForName(name);
        if (uuid == null) return null;

        Member member = KitPvPDB.getInstance().getMemberData(uuid);
        return member;
    }

    public void update(Member member) {
        update(member, false);
    }

    public void update(Member member, boolean unload) {
        new BukkitRunnable() {
            public void run() {
                KitPvPDB.getInstance().setMemberData(member);
            }
        }.runTaskAsynchronously(KitPvP.getInstance());

        if (unload) new BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayer(member.getUUID());
                if (player == null || !player.isOnline())
                    members.remove(member.getUUID());
            }
        }.runTaskLater(KitPvP.getInstance(), 2L);
    }

    public void onDisable() {
        for (Member member : members.values()) {
            KitPvPDB.getInstance().setMemberDataSync(member);
        }
    }

    public static MemberManager getInstance() {
        if (instance == null)
            instance = new MemberManager();
        return instance;
    }

}