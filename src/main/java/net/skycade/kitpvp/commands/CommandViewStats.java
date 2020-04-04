package net.skycade.kitpvp.commands;

import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import net.md_5.bungee.api.ChatColor;
import net.skycade.SkycadeCore.utility.MojangUtil;
import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.SkycadeCore.vanish.VanishStatus;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.stat.KitPvPDB;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.UUID;

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

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(strings[0]);

        if (offlinePlayer == null || offlinePlayer.getUniqueId() == null) { // double check case data pattern
            COULDNT_FIND.msg(commandSender, "%type%", "player", "%thing%", strings[0]);
            return;
        }

        // ASYNC (because it's grabbing from database)
        Bukkit.getScheduler().runTaskAsynchronously(KitPvP.getInstance(), () -> {

            Member target = MemberManager.getInstance().getMember(offlinePlayer.getUniqueId(), true);

            if (target == null) { // double-single check case data pattern
                COULDNT_FIND.msg(commandSender, "%type%", "player", "%thing%", strings[0]);
                return;
            }

            KitPvPStats stats = KitPvP.getInstance().getStats(target);

            //Gang name
            String gangName = "Not Available";

            if (target.getPlayer() != null) {
                Gang gang = GangsPlusApi.getPlayersGang(target.getPlayer());
                if (gang != null)
                    gangName = gang.getName();
            }

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
                    "%coins%", Long.toString(stats.getCoins()),
                    "%eventtokens%", Integer.toString(stats.getEventTokens())
            );
        });
    }
}
