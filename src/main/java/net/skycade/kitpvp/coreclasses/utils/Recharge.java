package net.skycade.kitpvp.coreclasses.utils;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Recharge {

    private static final Map<UUID, Map<String, Long>> lastUse = new HashMap<>();

    public static boolean recharge(Member member, String item, double time) {
        return recharge(member, item, time, false);
    }

    public static boolean recharge(Player p, String item, double time, boolean notifyOnRecharge) {
        return recharge(MemberManager.getInstance().getMember(p), item, time, notifyOnRecharge);
    }

    public static boolean recharge(Member member, String item, double time, boolean notifyOnRecharge) {
        UUID uuid = member.getUUID();
        if (!lastUse.containsKey(uuid)) {
            Map<String, Long> sessions = new HashMap<>();
            sessions.put(item, System.currentTimeMillis());
            lastUse.put(uuid, sessions);
            return true;
        }
        Map<String, Long> sessions = lastUse.get(uuid);
        if (!sessions.containsKey(item) || System.currentTimeMillis() - sessions.get(item) + 100 >= time * 1000) {
            sessions.put(item, System.currentTimeMillis());
            return true;
        }
        if (time > 1)
            member.message("§7You must wait §b" + new DecimalFormat("0.0").format((sessions.get(item) + time * 1000 - System.currentTimeMillis()) / 1000) + "§7 seconds to use §e" + item + "§7.");
        if (notifyOnRecharge)
            Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> member.message("§7You can now use §e" + item + "§7."), 20);
        return false;
    }

}
