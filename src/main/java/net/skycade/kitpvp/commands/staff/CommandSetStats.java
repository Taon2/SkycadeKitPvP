package net.skycade.kitpvp.commands.staff;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.SkycadeCore.utility.command.addons.Permissible;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

import static net.skycade.kitpvp.Messages.*;

@Permissible("kitpvp.admin")
public class CommandSetStats extends SkycadeCommand {
    public CommandSetStats() {
        super("setstat", Arrays.asList("statset", "setstats", "statsset"));
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        if (strings.length < 3) {
            SETSTATS_USAGE.msg(commandSender);
            return;
        }
        if (Bukkit.getPlayer(strings[0]) == null) {
            COULDNT_FIND.msg(commandSender, "%type%", "player", "%thing%", strings[0]);
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(strings[2]);
        } catch (NumberFormatException exception) {
            COULDNT_FIND.msg(commandSender, "%type%", "number", "%thing%", strings[2]);
            return;
        }

        Player target = Bukkit.getPlayer(strings[0]);
        KitPvPStats stats = KitPvP.getInstance().getStats(target);

        if (strings[1].equalsIgnoreCase("kills") || strings[1].equalsIgnoreCase("kill")) {
            stats.setKills(amount);
            sendMsg("kills", commandSender, target, amount);
        } else if (strings[1].equalsIgnoreCase("deaths")) {
            stats.setDeaths(amount);
            sendMsg("deaths", commandSender, target, amount);
        } else if (strings[1].equalsIgnoreCase("killstreak") || strings[1].equalsIgnoreCase("ks")) {
            stats.setStreak(amount);
            sendMsg("killstreak", commandSender, target, amount);
        } else if (strings[1].equalsIgnoreCase("highestkillstreak") || strings[1].equalsIgnoreCase("highks") || strings[1].equalsIgnoreCase("highkillstreak") || strings[1].equalsIgnoreCase("highestks")) {
            stats.setHighestStreak(amount);
            sendMsg("highestks", commandSender, target, amount);
        } else if (strings[1].equalsIgnoreCase("assist") || strings[0].equalsIgnoreCase("assists")) {
            stats.setAssists(amount);
            sendMsg("assist", commandSender, target, amount);
        } else
            COULDNT_FIND.msg(commandSender, "%type%", "stats", "%thing%", strings[1]);

        ScoreboardInfo.getInstance().updatePlayer(target);
    }

    private void sendMsg(String stat, CommandSender commandSender, Player target, int amount) {
        YOUR_STAT_SET.msg(target, "%stat%", stat, "%amount%", Integer.toString(amount));
        STAT_SET.msg(commandSender, "%player%", target.getName(), "%stat%", stat, "%amount%", Integer.toString(amount));
    }
}
