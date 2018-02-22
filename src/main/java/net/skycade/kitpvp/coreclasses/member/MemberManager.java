package net.skycade.kitpvp.coreclasses.member;

import net.skycade.kitpvp.coreclasses.commands.Module;
import net.skycade.kitpvp.coreclasses.member.listeners.MemberJoinQuit;
import net.skycade.kitpvp.coreclasses.member.listeners.MemberListeners;
import net.skycade.kitpvp.stat.KitPvPDB;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemberManager extends Module {

    private static MemberManager instance;

    private final Map<UUID, Member> members = new HashMap<>();

    private MemberManager() {
        registerListener(new MemberJoinQuit(this));
        registerListener(new MemberListeners(this));
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
            if (member != null) return member;
            else return null;
        }
        return null;
    }

    /* public Member getMember(String name, boolean database) {
        for (Member member : members.values())
            if (member.getName().equalsIgnoreCase(name))
                return member;
        if (database) {
            if (name.length() < 3 && !name.equalsIgnoreCase("G") && !name.equalsIgnoreCase("F")
                    && !name.equalsIgnoreCase("8"))
                return null;
            UUID uuid = KitPvPDB.getInstance().getUUIDForName(name);
            if (uuid == null) return null; else return new Member(uuid, name);
        }
        return null;
    } */

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
        if (member == null) return null;
        else return member;
    }

    public void update(Member member) {
        update(member, false);
    }

    public void update(Member member, boolean unload) {
        new BukkitRunnable() {
            public void run() {
                KitPvPDB.getInstance().setMemberData(member);
            }
        }.runTaskAsynchronously(getPlugin());

        if (unload) members.remove(member.getUUID());
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