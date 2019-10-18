package net.skycade.kitpvp.commands.staff;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPStats;
import net.skycade.kitpvp.ui.eventshopitems.EventShopManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import static net.skycade.kitpvp.Messages.*;

public class CommandEventEco extends Command<EventShopManager> {

    public CommandEventEco(EventShopManager module) {
        super(module, "Manage event tokens.", new Permission("kitpvp.admin", PermissionDefault.OP), "eventeconomy", "eventeco");
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
                Bukkit.getOnlinePlayers().forEach(p -> resetEventTokens(getModule().getKitPvP().getStats(p), member, p));
                return;
            }
            if (args[0].equalsIgnoreCase("give")) {
                Bukkit.getOnlinePlayers().forEach(p -> incEventTokens(getModule().getKitPvP().getStats(p), member, p, Integer.parseInt(args[2])));
                return;
            }
            if (args[0].equalsIgnoreCase("take")) {
                Bukkit.getOnlinePlayers().forEach(p -> takeEventTokens(getModule().getKitPvP().getStats(p), member, p, Integer.parseInt(args[2])));
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
            resetEventTokens(targetStats, member, target);
        if (args[0].equalsIgnoreCase("give"))
            incEventTokens(targetStats, member, target, amount);
        else if (args[0].equalsIgnoreCase("take"))
            takeEventTokens(targetStats, member, target, amount);

        ScoreboardInfo.getInstance().updatePlayer(target);

    }

    private void resetEventTokens(KitPvPStats targetStats, Member member, Player target) {
        targetStats.setEventCoins(0);
        YOUR_CURRENCY_RESET.msg(target, "%currency%", "event tokens");
        CURRENCY_RESET.msg(member.getPlayer(), "%player%", target.getName(), "%currency%", "event tokens");
    }

    private void incEventTokens(KitPvPStats targetStats, Member member, Player target, int amount) {
        targetStats.setEventCoins(targetStats.getEventTokens() + amount);
        YOUR_CURRENCY_ADDED.msg(target, "%amount%", Integer.toString(amount), "%currency%", "event tokens", "%total%", Integer.toString(targetStats.getEventTokens()));
        CURRENCY_ADDED.msg(member.getPlayer(), "%amount%", Integer.toString(amount), "%currency%", "event tokens", "%player%", target.getName());
    }

    private void takeEventTokens(KitPvPStats targetStats, Member member, Player target, int amount) {
        if (targetStats.getEventTokens() - amount < 0)
            resetEventTokens(targetStats, member, target);
        else {
            targetStats.setEventCoins(targetStats.getEventTokens() - amount);
            YOUR_CURRENCY_REMOVED.msg(target, "%amount%", Integer.toString(amount), "%currency%", "event tokens");
            CURRENCY_REMOVED.msg(member.getPlayer(), "%player%", target.getName(), "%amount%", Integer.toString(amount), "%currency%", "event tokens", "%total%", Integer.toString(targetStats.getEventTokens()));
        }
    }

}
