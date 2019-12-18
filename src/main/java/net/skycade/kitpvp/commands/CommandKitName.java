package net.skycade.kitpvp.commands;

import net.skycade.SkycadeCore.vanish.VanishStatus;
import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class CommandKitName extends Command<KitManager> {

    public CommandKitName(KitManager module) {
        super(module, "Get the kitname from a player", new Permission("kitpvp.default", PermissionDefault.TRUE), "kitname");
        setUsage("<player>");
    }

    @Override
    public void execute(Member member, String aliasUsed, String... args) {
        if (!checkArgs(member, aliasUsed, args, 1))
            return;
        if (!getPlayer(member, args[0]))
            return;
        Player target = Bukkit.getPlayer(args[0]);
        if (VanishStatus.isVanished(target.getUniqueId())) {
            member.message("&7Could not find Player '§e" + target.getName() + "&7'.");
            return;
        }
        KitPvPStats stats = getModule().getKitPvP().getStats(target);
        member.message("§a" + target.getName() + "§7 is using the §a" + stats.getActiveKit().getKit().getName() + "§7 kit.");
    }

}
