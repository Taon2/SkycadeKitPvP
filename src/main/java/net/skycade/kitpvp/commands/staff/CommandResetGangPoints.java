package net.skycade.kitpvp.commands.staff;

import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.SkycadeCore.utility.command.addons.Permissible;
import net.skycade.kitpvp.KitPvP;
import org.bukkit.command.CommandSender;

import static net.skycade.kitpvp.Messages.*;

@Permissible("kitpvp.admin")
public class CommandResetGangPoints extends SkycadeCommand {
    public CommandResetGangPoints() {
        super("resetgangpoints");
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        if (strings.length < 1) {
            RESETGANGPOINTS_USAGE.msg(commandSender);
            return;
        }

        if (strings[0].equalsIgnoreCase("all")) {
            KitPvP.getInstance().getGangPointsManager().resetAllPoints();

            GANG_POINTS_RESET.msg(commandSender, "%gang%", "Every gang");
        } else {
            Gang gang = null;
            for (Gang g : GangsPlusApi.getAllGangs()) {
                if (g.getName().equalsIgnoreCase(strings[0]))
                    gang = g;
            }

            if (gang == null) {
                COULDNT_FIND.msg(commandSender, "%type%", "gang", "%thing%", strings[0]);
                return;
            }

            KitPvP.getInstance().getGangPointsManager().setPoints(gang.getName(), 0L);

            GANG_POINTS_RESET.msg(commandSender, "%gang%", gang.getName());
        }
    }
}
