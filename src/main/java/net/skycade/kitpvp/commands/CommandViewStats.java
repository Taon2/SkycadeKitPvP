package net.skycade.kitpvp.commands;

import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
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

import static net.skycade.kitpvp.Messages.*;

public class CommandViewStats extends SkycadeCommand {
    public CommandViewStats() {
        super("stats", Collections.singletonList("viewstats"));
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        Member member = MemberManager.getInstance().getMember((Player) commandSender);

        if (strings.length < 1) {
            VIEWSTATS_USAGE.msg(commandSender);
            return;
        }
        if (Bukkit.getPlayer(strings[0]) == null) {
            COULDNT_FIND.msg(commandSender, "%type%", "player", "%thing%", strings[0]);
            return;
        }
        Member target = MemberManager.getInstance().getMember(Bukkit.getPlayer(strings[0]));
        KitPvPStats stats = KitPvP.getInstance().getStats(target);

        //Gang name
        Gang gang = GangsPlusApi.getPlayersGang(target.getPlayer());
        String gangName = "None";
        if (gang != null)
            gangName = gang.getName();

        STATS.msg(member.getPlayer(),
                "%player%", ChatColor.GRAY + "[" + ChatColor.WHITE + stats.getPrestigeLevel() + "★" + ChatColor.GRAY + "] " + ChatColor.WHITE + ChatColor.BOLD + target.getName(),
                "%gang%", gangName,
                "%deaths%", Integer.toString(stats.getDeaths()),
                "%kills%", Integer.toString(stats.getKills()),
                "%kdr%", Double.toString(UtilMath.getKDR(stats.getKills(), stats.getDeaths())),
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
