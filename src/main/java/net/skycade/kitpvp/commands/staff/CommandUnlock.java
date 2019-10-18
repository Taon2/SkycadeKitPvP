package net.skycade.kitpvp.commands.staff;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.Map;

import static net.skycade.kitpvp.Messages.*;

public class CommandUnlock extends Command<KitManager> {

    public CommandUnlock(KitManager module) {
        super(module, "Unlock kits for a player.", new Permission("kitpvp.admin", PermissionDefault.OP), "unlock", "unlockkit", "lock", "lockkit");
        registerAsBukkitCommand();
        setUsage("<player>", "<kit/all>", "<lock/unlock>");
    }

    @Override
    public void execute(Member member, String aliasUsed, String... args) {
        if (!checkArgs(member, aliasUsed, args, getUsage().length))
            return;
        if (!getPlayer(member, args[0]))
            return;
        Player target = Bukkit.getPlayer(args[0]);
        KitPvPStats stats = getModule().getKitPvP().getStats(target);

        if (args[2].equalsIgnoreCase("unlock")) {
            if (args[1].equalsIgnoreCase("all")) {
                getModule().getKits().forEach((kitType, kit) -> {
                    if (kit.isEnabled())
                        stats.addKit(kitType);
                });
                ScoreboardInfo.getInstance().updatePlayer(target);
                KIT_UNLOCKED.msg(member.getPlayer(), "%player%", target.getName(), "%kit%", "every");
                if (!member.getPlayer().equals(target))
                    YOUR_KIT_UNLOCKED.msg(target, "%kit%", "every");
                return;
            }
            Kit kit = getKit(args, member);
            if (kit == null)
                return;
            stats.addKit(kit.getKitType());
            ScoreboardInfo.getInstance().updatePlayer(member.getPlayer());

            KIT_UNLOCKED.msg(member.getPlayer(), "%player%", target.getName(), "%kit%", kit.getName());
            if (!member.getPlayer().equals(target))
                YOUR_KIT_UNLOCKED.msg(target, "%kit%", kit.getName());
            return;

        } else if (args[2].equalsIgnoreCase("lock")) {
            if (args[1].equalsIgnoreCase("all")) {
                stats.resetKits();
                KIT_LOCKED.msg(target, "%player%", member.getName(), "%kit%", "every kit");
                return;
            }
            Kit kit = getKit(args, member);
            if (kit == null)
                return;
            stats.removeKit(kit.getKitType());

            KIT_LOCKED.msg(target, "%player%", member.getName(), "%kit%", kit.getName());
            if (!member.getPlayer().equals(target))
                YOUR_KIT_LOCKED.msg(target, "%kit%", kit.getName());
            return;
        }
        member.message(getUsageToString());

        ScoreboardInfo.getInstance().updatePlayer(target);
    }

    private Kit getKit(String[] args, Member member) {
        Kit kit = null;
        for (Map.Entry<KitType, Kit> entry : getModule().getKits().entrySet())
            if (entry.getValue().getName().equalsIgnoreCase(args[1]))
                kit = entry.getValue();
        if (kit == null) {
            COULDNT_FIND.msg(member.getPlayer(), "%type%", "kit", "%thing%", args[1]);
            return null;
        }
        return kit;
    }

}
