package net.skycade.kitpvp.commands.staff;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.SkycadeCore.utility.command.addons.Permissible;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Collections;

import static net.skycade.kitpvp.Messages.*;

@Permissible("kitpvp.admin")
public class CommandRefund extends SkycadeCommand {
    public CommandRefund() {
        super("refund", Collections.singletonList("refundks"));
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        if (strings.length < 1) {
            //todo usage
            return;
        }
        if (Bukkit.getPlayer(strings[0]) == null) {
            COULDNT_FIND.msg(commandSender, "%type%", "player", "%thing%", strings[0]);
            return;
        }
        Member target = MemberManager.getInstance().getMember(Bukkit.getPlayer(strings[0]));
        KitPvPStats stats = KitPvP.getInstance().getStats(target);
        if (stats.getLastStreak() == null || stats.getLastStreak() < 0) {
            NO_LAST_KILLSTREAK.msg(commandSender, "%player%", target.getName());
            return;
        }
        if (stats.getStreak() > stats.getLastStreak()) {
            CURRENT_KILlSTREAK_HIGHER.msg(commandSender);
            return;
        }
        stats.setStreak(stats.getLastStreak());
        KILLSTREAK_REFUNDED.msg(target.getPlayer());

        ScoreboardInfo.getInstance().updatePlayer(target.getPlayer());
    }
}
