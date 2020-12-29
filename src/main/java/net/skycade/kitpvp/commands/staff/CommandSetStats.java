package net.skycade.kitpvp.commands.staff;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.SkycadeCore.utility.command.addons.Permissible;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static net.skycade.kitpvp.Messages.*;

@Permissible("kitpvp.admin")
public class CommandSetStats extends SkycadeCommand {
    public CommandSetStats() {
        super("setstat", Arrays.asList("statset", "setstats", "statsset"));
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] args) {
        if (args.length < 3) {
            SETSTATS_USAGE.msg(commandSender);
            return;
        }
        Member target;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        if (offlinePlayer == null || offlinePlayer.getUniqueId() == null) { // double check case data pattern
            COULDNT_FIND.msg(commandSender, "%type%", "player", "%thing%", args[0]);
            return;
        }

        target = MemberManager.getInstance().getMember(offlinePlayer.getUniqueId(), true);

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException exception) {
            COULDNT_FIND.msg(commandSender, "%type%", "number", "%thing%", args[2]);
            return;
        }

        KitPvPStats stats = KitPvP.getInstance().getStats(target);

        switch (args[1].toLowerCase()){
            case "kill":
            case "kills":{
                stats.setKills(amount);
                if (online(offlinePlayer)){
                    sendMsg("kills", commandSender, target.getPlayer(), amount);
                    ScoreboardInfo.getInstance().updatePlayer(offlinePlayer.getPlayer());
                }
                return;
            }
            case "death":
            case "deaths":{
                stats.setDeaths(amount);
                if (online(offlinePlayer)){
                    sendMsg("deaths", commandSender, target.getPlayer(), amount);
                    ScoreboardInfo.getInstance().updatePlayer(offlinePlayer.getPlayer());
                }
                return;
            }
            case "killstreak":
            case "ks":{
                stats.setStreak(amount);
                if (online(offlinePlayer)){
                    sendMsg("killstreak", commandSender, target.getPlayer(), amount);
                    ScoreboardInfo.getInstance().updatePlayer(offlinePlayer.getPlayer());
                }
                return;
            }
            case "highestks":
            case "highestkillstreak":
            case "highks":
            case "bestks":{
                stats.setHighestStreak(amount);
                if (online(offlinePlayer)){
                    sendMsg("highestks", commandSender, target.getPlayer(), amount);
                    ScoreboardInfo.getInstance().updatePlayer(offlinePlayer.getPlayer());
                }
                return;
            }
            case "assist":
            case "assists":{
                stats.setAssists(amount);
                if (online(offlinePlayer)){
                    sendMsg("assist", commandSender, target.getPlayer(), amount);
                    ScoreboardInfo.getInstance().updatePlayer(offlinePlayer.getPlayer());
                }
                return;
            }
            default:{
                COULDNT_FIND.msg(commandSender, "%type%", "stats", "%thing%", args[1]);
                VALID_STATS.msg(commandSender);
            }
        }
    }

    private void sendMsg(String stat, CommandSender commandSender, Player target, int amount) {
        YOUR_STAT_SET.msg(target, "%stat%", stat, "%amount%", Integer.toString(amount));
        STAT_SET.msg(commandSender, "%player%", target.getName(), "%stat%", stat, "%amount%", Integer.toString(amount));
    }
    private boolean online(OfflinePlayer p){
        return p.isOnline();
    }
}
