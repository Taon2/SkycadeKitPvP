package net.skycade.kitpvp.commands.staff;

import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.kitpvp.KitPvP;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;

import static net.skycade.kitpvp.Messages.*;
import static net.skycade.kitpvp.Messages.GANG_POINTS_RESET;

public class CommandGangPoints extends SkycadeCommand {
    public CommandGangPoints() {
        super("gangpoints");
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] args) {
        if (args.length < 1) {
            GANG_POINTS_USAGE.msg(commandSender);
            return;
        }
        if (args[0].equalsIgnoreCase("deduct")){
            Gang gang = null;
            for (Gang g : GangsPlusApi.getAllGangs()) {
                if (g.getName().equalsIgnoreCase(args[1]))
                    gang = g;
            }

            if (gang == null) {
                COULDNT_FIND.msg(commandSender, "%type%", "gang", "%thing%", args[1]);
                return;
            }

            long amount;
            try {
                amount = Long.parseLong(args[2]);
            } catch (NumberFormatException exception) {
                COULDNT_FIND.msg(commandSender, "%type%", "number", "%thing%", args[2]);
                return;
            }

            KitPvP.getInstance().getGangPointsManager().removePoints(gang.getName(), amount);

            DecimalFormat df = new DecimalFormat("###,###,###.##");
            GANG_POINTS_DEDUCTED.msg(commandSender, "%gang%", gang.getName(), "%amount%", df.format(amount));
            return;
        }

        if (args[0].equalsIgnoreCase("reset")){
            if (args[1].equalsIgnoreCase("all")){
                KitPvP.getInstance().getGangPointsManager().resetAllPoints();

                GANG_POINTS_RESET.msg(commandSender, "%gang%", "Every gang");
                return;
            }
            Gang gang = null;
            for (Gang g : GangsPlusApi.getAllGangs()) {
                if (g.getName().equalsIgnoreCase(args[1]))
                    gang = g;
            }

            if (gang == null) {
                COULDNT_FIND.msg(commandSender, "%type%", "gang", "%thing%", args[1]);
                return;
            }

            KitPvP.getInstance().getGangPointsManager().setPoints(gang.getName(), 0L);

            GANG_POINTS_RESET.msg(commandSender, "%gang%", gang.getName());
            return;
        }

        if (args[0].equalsIgnoreCase("set")){
            Gang gang = null;
            for (Gang g : GangsPlusApi.getAllGangs()) {
                if (g.getName().equalsIgnoreCase(args[1]))
                    gang = g;
            }

            if (gang == null) {
                COULDNT_FIND.msg(commandSender, "%type%", "gang", "%thing%", args[1]);
                return;
            }

            long amount;
            try {
                amount = Long.parseLong(args[2]);
            } catch (NumberFormatException exception) {
                COULDNT_FIND.msg(commandSender, "%type%", "number", "%thing%", args[2]);
                return;
            }

            KitPvP.getInstance().getGangPointsManager().setPoints(gang.getName(), amount);

            DecimalFormat df = new DecimalFormat("###,###,###.##");
            GANG_POINTS_SET.msg(commandSender, "%gang%", gang.getName(), "%amount%", df.format(amount));
            return;
        }
    }
}
