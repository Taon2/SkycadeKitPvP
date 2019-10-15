package net.skycade.kitpvp.commands.staff;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import static net.skycade.kitpvp.Messages.STATS;

public class CommandViewStats extends Command<KitManager> {

    public CommandViewStats(KitManager module) {
        super(module, "See stats for a player.", new Permission("kitpvp.admin", PermissionDefault.OP), "viewstats", "seestats");
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
        STATS.msg(member.getPlayer(),
                "%player%", target.getName(),
                "%kits%", Integer.toString(stats.getKits().size()),
                "%coins%", Integer.toString(stats.getCoins()),
                "%eventtokens%", Integer.toString(stats.getEventTokens()),
                "%deaths%", Integer.toString(stats.getDeaths()),
                "%kills%", Integer.toString(stats.getKills()),
                "%assists%", Integer.toString(stats.getAssists()),
                "%killstreak", Integer.toString(stats.getHighestStreak())
                );
    }

}
