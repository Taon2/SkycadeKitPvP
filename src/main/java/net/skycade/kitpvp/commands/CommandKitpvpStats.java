package net.skycade.kitpvp.commands;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import static net.skycade.kitpvp.Messages.STATS;

public class CommandKitpvpStats extends Command<KitManager> {

    public CommandKitpvpStats(KitManager module) {
        super(module, "Get an overview of all your KitPvP related stats.", new Permission("kitpvp.default", PermissionDefault.TRUE), "kitpvpstats", "statskitpvp", "kitstats", "statskit");
    }

    @Override
    public void execute(Member member, String aliasUsed, String... args) {
        KitPvPStats stats = getModule().getKitPvP().getStats(member);
        STATS.msg(member.getPlayer(),
                "%player%", member.getName(),
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