package net.skycade.kitpvp.commands.staff;

import net.skycade.SkycadeCore.utility.TeleportUtil;
import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.SkycadeCore.utility.command.addons.Permissible;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import static net.skycade.kitpvp.Messages.*;

@Permissible("kitpvp.admin")
public class CommandResetStats extends SkycadeCommand {
    public CommandResetStats() {
        super("resetstats");
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        if (strings.length < 1) {
            RESETSTATS_USAGE.msg(commandSender);
            return;
        }
        if (Bukkit.getPlayer(strings[0]) == null) {
            COULDNT_FIND.msg(commandSender, "%type%", "player", "%thing%", strings[0]);
            return;
        }
        Member target = MemberManager.getInstance().getMember(Bukkit.getPlayer(strings[0]));
        KitPvPStats stats = KitPvP.getInstance().getStats(target);
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

        STATS_RESET.msg(commandSender, "%player%", target.getName());
    }
}
