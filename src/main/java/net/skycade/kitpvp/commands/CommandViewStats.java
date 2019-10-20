package net.skycade.kitpvp.commands;

import net.md_5.bungee.api.ChatColor;
import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

import static net.skycade.kitpvp.Messages.STATS;

public class CommandViewStats extends SkycadeCommand {
    public CommandViewStats() {
        super("stats", Collections.singletonList("viewstats"));
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        Member member = MemberManager.getInstance().getMember((Player) commandSender);

        if (strings.length < 1)
            return;
        if (Bukkit.getPlayer(strings[0]) == null)
            return;
        Member target = MemberManager.getInstance().getMember(Bukkit.getPlayer(strings[0]));
        KitPvPStats stats = KitPvP.getInstance().getStats(target);
        STATS.msg(member.getPlayer(),
                "%player%", ChatColor.GRAY + "[" + ChatColor.WHITE + stats.getPrestigeLevel() + "★" + ChatColor.GRAY + "] " + ChatColor.WHITE + ChatColor.BOLD + target.getName(),
                "%deaths%", Integer.toString(stats.getDeaths()),
                "%kills%", Integer.toString(stats.getKills()),
                "%kdr%", Double.toString(UtilMath.getKDR(member.getKills(), member.getDeaths())),
                "%assists%", Integer.toString(stats.getAssists()),
                "%currentkillstreak%", Integer.toString(stats.getStreak()),
                "%highestkillstreak%", Integer.toString(stats.getHighestStreak()),
                "%kits%", Integer.toString(stats.getKits().size()),
                "%currentkit%", stats.getActiveKit().getKit().getName(),
                "%coins%", Integer.toString(stats.getCoins()),
                "%eventtokens%", Integer.toString(stats.getEventTokens())
        );
    }
}
