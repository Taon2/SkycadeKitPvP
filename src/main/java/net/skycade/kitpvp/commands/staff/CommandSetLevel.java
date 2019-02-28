package net.skycade.kitpvp.commands.staff;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.Map.Entry;

public class CommandSetLevel extends Command<KitManager> {

    public CommandSetLevel(KitManager module) {
        super(module, "Unlock kits for a player.", new Permission("kitpvp.admin", PermissionDefault.OP), "setlevel", "setlvl");
        setUsage("<player>", "<kit>", "<level>");
    }

    @Override
    public void execute(Member member, String aliasUsed, String... args) {
        if (!checkArgs(member, aliasUsed, args, getUsage().length))
            return;
        if (!getPlayer(member, args[0]))
            return;
        Player target = Bukkit.getPlayer(args[0]);
        KitPvPStats stats = getModule().getKitPvP().getStats(target);

        if (!UtilMath.isInt(args[2])) {
            couldNotFind(member, "level", args[2]);
            return;
        }
        int level = Integer.parseInt(args[2]) > 3 ? 3 : Integer.parseInt(args[2]);
        if (args[1].equalsIgnoreCase("all")) {
            stats.getKits().entrySet().forEach(k -> k.getValue().setLevel(level));
            member.message("All kit levels set to " + level + " for §a" + target.getName() + "§7.");
            return;
        }

        Kit kit = getKit(stats, args, member);
        if (kit == null)
            return;
        if (!stats.hasKit(kit.getKitType())) {
            member.message("This kit is §anot unlocked §7yet.");
        } else {
            stats.getKits().get(kit.getKitType()).setLevel(level);
            member.message("§a" + kit.getName() + " §7level set to " + level + " for §a" + target.getName() + "§7.");
        }
    }

    private Kit getKit(KitPvPStats stats, String[] args, Member member) {
        Kit kit = null;
        for (Entry<KitType, Kit> entry : getModule().getKits().entrySet())
            if (entry.getValue().getName().equalsIgnoreCase(args[1]))
                kit = entry.getValue();
        if (kit == null) {
            couldNotFind(member, "kit", args[1]);
            return null;
        }
        return kit;
    }

}
