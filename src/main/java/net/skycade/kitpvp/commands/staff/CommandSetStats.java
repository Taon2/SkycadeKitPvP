package net.skycade.kitpvp.commands.staff;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import static net.skycade.kitpvp.Messages.*;

public class CommandSetStats extends Command<KitManager> {

    public CommandSetStats(KitManager module) {
        super(module, "Change stats for a player.", new Permission("kitpvp.admin", PermissionDefault.OP), "setstats", "setstat");
        setUsage("<player>", "<stats>", "<amount>");
    }

    @Override
    public void execute(Member member, String aliasUsed, String... args) {
        if (!checkArgs(member, aliasUsed, args))
            return;
        if (!getPlayer(member, args[0]))
            return;
        if (!parseInt(member, args[2])) {
            COULDNT_FIND.msg(member.getPlayer(), "%type%", "amount", "%thing%", args[2]);
            return;
        }
        int amount = Integer.parseInt(args[2]);
        Player target = Bukkit.getPlayer(args[0]);
        KitPvPStats stats = getModule().getKitPvP().getStats(target);

        if (args[1].equalsIgnoreCase("kills") || args[1].equalsIgnoreCase("kill")) {
            stats.setKills(amount);
            sendMsg("kills", member, target, amount);
        } else if (args[1].equalsIgnoreCase("deaths")) {
            stats.setDeaths(amount);
            sendMsg("deaths", member, target, amount);
        } else if (args[1].equalsIgnoreCase("killstreak") || args[1].equalsIgnoreCase("ks")) {
            stats.setStreak(amount);
            sendMsg("killstreak", member, target, amount);
        } else if (args[1].equalsIgnoreCase("highestkillstreak") || args[1].equalsIgnoreCase("highks") || args[1].equalsIgnoreCase("highkillstreak") || args[1].equalsIgnoreCase("highestks")) {
            stats.setHighestStreak(amount);
            sendMsg("highestks", member, target, amount);
        } else if (args[1].equalsIgnoreCase("assist") || args[0].equalsIgnoreCase("assists")) {
            stats.setAssists(amount);
            sendMsg("assist", member, target, amount);
        }else
            COULDNT_FIND.msg(member.getPlayer(), "%type%", "stats", "%thing%", args[1]);

        ScoreboardInfo.getInstance().updatePlayer(target);
    }

    private void sendMsg(String stat, Member member, Player target, int amount) {
        YOUR_STAT_SET.msg(member.getPlayer(), "%stat%", stat, "%amount%", Integer.toString(amount));
        STAT_SET.msg(member.getPlayer(), "%player%", target.getName(), "%stat%", stat, "%amount%", Integer.toString(amount));
    }

}
