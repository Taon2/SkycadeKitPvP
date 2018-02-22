package net.skycade.kitpvp.commands.staff;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class CommandResetStats extends Command<KitManager> {

    public CommandResetStats(KitManager module) {
        super(module, "Reset stats for a player", new Permission("kitpvp.admin", PermissionDefault.OP), "resetstats");
        setUsage("<playername>");
    }

    @Override
    public void execute(Member member, String aliasUsed, String... args) {
        if (!checkArgs(member, aliasUsed, args, 1))
            return;
        if (!getPlayer(member, args[0])) {
            couldNotFind(member, "playername", args[0]);
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        KitPvPStats stats = getModule().getKitPvP().getStats(target);
        stats.setKills(0);
        stats.setCoins(0);
        stats.setCrateKeys(0);
        stats.setDeaths(0);
        stats.setStreak(0);
        stats.setHighestStreak(0);
        stats.resetKits();
        member.message("§a" + target.getName() + "§7 stats are reset.");
    }

}
