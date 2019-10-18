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

public class CommandEco extends Command<KitManager> {

    public CommandEco(KitManager module) {
        super(module, "Manage coins.", new Permission("kitpvp.admin", PermissionDefault.OP), "economy", "eco");
        setUsage("<reset/give/take> <player/all> <amount>");
    }

    @Override
    public void execute(Member member, String aliasUsed, String... args) {
        if (!checkArgs(member, aliasUsed, args, 2))
            return;
        int amount = 0;
        if (!args[0].equalsIgnoreCase("reset")) {
            if (!checkArgs(member, aliasUsed, args))
                return;
            if (!parseInt(member, args[2])) {
                COULDNT_FIND.msg(member.getPlayer(), "%type%", "amount", "%thing%", args[2]);
                return;
            }
            amount = Integer.parseInt(args[2]);
        }

        if (args[1].equalsIgnoreCase("all")) {
            if (args[0].equalsIgnoreCase("reset")) {
                Bukkit.getOnlinePlayers().forEach(p -> resetCoins(getModule().getKitPvP().getStats(p), member, p));
                return;
            }
            if (args[0].equalsIgnoreCase("give")) {
                Bukkit.getOnlinePlayers().forEach(p -> incCoins(getModule().getKitPvP().getStats(p), member, p, Integer.parseInt(args[2])));
                return;
            }
            if (args[0].equalsIgnoreCase("take")) {
                Bukkit.getOnlinePlayers().forEach(p -> takeCoins(getModule().getKitPvP().getStats(p), member, p, Integer.parseInt(args[2])));
                return;
            }
        }
        if (!getPlayer(member, args[1])) {
            COULDNT_FIND.msg(member.getPlayer(), "%type%", "player", "%thing%", args[1]);
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        KitPvPStats targetStats = getModule().getKitPvP().getStats(target);

        if (args[0].equalsIgnoreCase("reset"))
            resetCoins(targetStats, member, target);
        else if (args[0].equalsIgnoreCase("give"))
            incCoins(targetStats, member, target, amount);
        else if (args[0].equalsIgnoreCase("take"))
            takeCoins(targetStats, member, target, amount);

        ScoreboardInfo.getInstance().updatePlayer(target);
    }

    private void resetCoins(KitPvPStats targetStats, Member member, Player target) {
        targetStats.setCoins(0);
        YOUR_CURRENCY_RESET.msg(member.getPlayer(), "%currency%", "coins");
        CURRENCY_RESET.msg(member.getPlayer(), "%player%", target.getName(), "%currency%", "coins");
    }

    private void incCoins(KitPvPStats targetStats, Member member, Player target, int amount) {
        targetStats.setCoins(targetStats.getCoins() + amount);
        YOUR_CURRENCY_ADDED.msg(target, "%amount%", Integer.toString(amount), "%currency%", "coins", "%total%", Integer.toString(targetStats.getCoins()));
        CURRENCY_ADDED.msg(member.getPlayer(), "%amount%", Integer.toString(amount), "%currency%", "coins", "%player%", target.getName());
    }

    private void takeCoins(KitPvPStats targetStats, Member member, Player target, int amount) {
        if (targetStats.getCoins() - amount < 0)
            resetCoins(targetStats, member, target);
        else {
            targetStats.setCoins(targetStats.getCoins() - amount);
            YOUR_CURRENCY_REMOVED.msg(target, "%amount%", Integer.toString(amount), "%currency%", "coins");
            CURRENCY_REMOVED.msg(member.getPlayer(), "%player%", target.getName(), "%amount%", Integer.toString(amount), "%currency%", "coins", "%total%", Integer.toString(targetStats.getCoins()));
        }
    }

}
