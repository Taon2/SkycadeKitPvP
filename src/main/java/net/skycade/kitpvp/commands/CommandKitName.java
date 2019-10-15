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

import static net.skycade.kitpvp.Messages.COULDNT_FIND;
import static net.skycade.kitpvp.Messages.USING_KIT;

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
            COULDNT_FIND.msg(member.getPlayer(), "%type%", "player", "%thing%", args[0]);
            return;
        }
        KitPvPStats stats = getModule().getKitPvP().getStats(target);
        USING_KIT.msg(member.getPlayer(), "%player%", target.getName(), "%kitname%", stats.getActiveKit().getKit().getName());
    }

}
