package net.skycade.kitpvp.commands;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.SkycadeCore.vanish.VanishStatus;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.skycade.kitpvp.Messages.*;

public class CommandKitName extends SkycadeCommand {
    public CommandKitName() {
        super("kitname");
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        if (strings.length < 1) {
            RESETSTATS_USAGE.msg(commandSender);
            return;
        }
        if (Bukkit.getPlayer(strings[0]) == null) {
            COULDNT_FIND.msg(commandSender, "%type%", "player", "%thing%", strings[0]);
            return;
        }

        Player target = Bukkit.getPlayer(strings[0]);
        if (VanishStatus.isVanished(target.getUniqueId())) {
            COULDNT_FIND.msg(commandSender, "%type%", "player", "%thing%", strings[0]);
            return;
        }
        KitPvPStats stats = KitPvP.getInstance().getStats(target);
        USING_KIT.msg(commandSender, "%player%", target.getName(), "%kitname%", stats.getActiveKit().getKit().getName());
    }
}
