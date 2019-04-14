package net.skycade.kitpvp.commands.staff;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.stat.KitPvPStats;
import net.skycade.kitpvp.ui.eventshopitems.EventShopManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

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
                couldNotFind(member, "amount", args[2]);
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
            couldNotFind(member, "player", args[1]);
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        KitPvPStats targetStats = getModule().getKitPvP().getStats(target);

        if (args[0].equalsIgnoreCase("reset")) {
            resetEventTokens(targetStats, member, target);
            return;
        }
        if (args[0].equalsIgnoreCase("give"))
            incEventTokens(targetStats, member, target, amount);
        else if (args[0].equalsIgnoreCase("take"))
            takeEventTokens(targetStats, member, target, amount);
    }

    private void resetEventTokens(KitPvPStats targetStats, Member member, Player target) {
        targetStats.setEventCoins(0);
        target.sendMessage("§7Your event tokens got §Areset§7.");
        member.message("§7" + target.getName() + "'s event tokens got §Areset§7.");
    }

    private void incEventTokens(KitPvPStats targetStats, Member member, Player target, int amount) {
        targetStats.setEventCoins(targetStats.getEventCoins() + amount);
        target.sendMessage("§7You got §a" + amount + "§7 event tokens, your total event token balance is now §a" + targetStats.getEventCoins() + "§7 event tokens.");
        member.message("§a" + amount + "§7 event tokens given to §a" + target.getName() + "§7.");
    }

    private void takeEventTokens(KitPvPStats targetStats, Member member, Player target, int amount) {
        if (targetStats.getEventCoins() - amount < 0)
            resetEventTokens(targetStats, member, target);
        else {
            targetStats.setEventCoins(targetStats.getEventCoins() - amount);
            target.sendMessage("§7Your event token balance got lowered with §a" + amount + "§7 tokens.");
            member.message("§a" + target.getName() + "'s §7event token balance got lowered with §a" + amount + "§7event tokens to §a" + targetStats.getEventCoins() + "§7.");
        }
    }

}
