package net.skycade.kitpvp.commands.staff;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import static net.skycade.kitpvp.Messages.*;

public class RefundCommand extends Command<KitManager> {

    public RefundCommand(KitManager module) {
        super(module, "Refund the killstreak for a player.", new Permission("kitpvp.admin", PermissionDefault.OP), "refund", "refundks");
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
            NO_LAST_KILLSTREAK.msg(member.getPlayer(), "%player%", target.getName());
            return;
        }
        if (stats.getStreak() > stats.getLastStreak()) {
            CURRENT_KILlSTREAK_HIGHER.msg(member.getPlayer());
            return;
        }
        stats.setStreak(stats.getLastStreak());
        KILLSTREAK_REFUNDED.msg(target.getPlayer());

        ScoreboardInfo.getInstance().updatePlayer(target.getPlayer());
    }

}
