package me.bukkit.kitpvp.commands.staff;

import me.bukkit.kitpvp.coreclasses.commands.Command;
import me.bukkit.kitpvp.coreclasses.member.Member;
import me.bukkit.kitpvp.coreclasses.member.MemberManager;
import me.bukkit.kitpvp.coreclasses.member.Permission;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;

public class RefundCommand extends Command<KitManager> {

    public RefundCommand(KitManager module) {
        super(module, "Refund the killstreak for a player", Permission.MOD, "refund", "refundks");
        setUsage("<player>");
    }

    @Override
    public void execute(Member member, String aliasUsed, String... args) {
        if (!checkArgs(member, aliasUsed, args))
            return;
        if (!getPlayer(member, args[0])) 
            return;
        Member target = MemberManager.getInstance().getMember(Bukkit.getPlayer(args[0]));
        KitPvPStats stats = getModule().getKitPvP().getStats(target);
        if (stats.getLastStreak() == null || stats.getLastStreak() < 0) {
            member.message("Last killstreak can't be found for " + target.getName() + "§7.");
            return;
        } 
        if (stats.getStreak() > stats.getLastStreak()) {
            member.message("Current killstreak is higher than last killstreak." );
            return;
        }
        stats.setStreak(stats.getLastStreak());
        target.message("Your killstreak got refunded.");
    }

}
