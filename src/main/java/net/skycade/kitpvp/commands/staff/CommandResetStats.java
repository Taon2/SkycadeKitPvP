package net.skycade.kitpvp.commands.staff;

import net.skycade.SkycadeCore.utility.TeleportUtil;
import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import static net.skycade.kitpvp.Messages.STATS_RESET;

public class CommandResetStats extends Command<KitManager> {

    public CommandResetStats(KitManager module) {
        super(module, "Reset stats for a player.", new Permission("kitpvp.admin", PermissionDefault.OP), "resetstats");
        setUsage("<playername>");
    }

    @Override
    public void execute(Member member, String aliasUsed, String... args) {
        if (!checkArgs(member, aliasUsed, args, 1))
            return;
        if (!getPlayer(member, args[0])) {
            return;
        }
        Member target = MemberManager.getInstance().getMember(Bukkit.getPlayer(args[0]));
        KitPvPStats stats = getModule().getKitPvP().getStats(target);
        stats.setCoins(0);
        stats.setEventCoins(0);
        stats.setKills(0);
        stats.setDeaths(0);
        stats.setStreak(0);
        stats.setHighestStreak(0);
        stats.resetKits();
        stats.setActiveKit(KitType.CHANCE);
        TeleportUtil.teleport(target.getPlayer(), TeleportUtil.getSpawn());

        ScoreboardInfo.getInstance().updatePlayer(target.getPlayer());

        STATS_RESET.msg(member.getPlayer(), "%player%", target.getName());
    }

}
