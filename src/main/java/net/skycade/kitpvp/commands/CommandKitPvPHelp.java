package net.skycade.kitpvp.commands;

import com.google.common.collect.ImmutableList;
import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import static net.skycade.kitpvp.Messages.*;

public class CommandKitPvPHelp extends SkycadeCommand {

    public CommandKitPvPHelp() {
        super("kitpvphelp", ImmutableList.of("kithelp", "kitpvpcommands", "kitcommands"));
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        commandSender.sendMessage(KITPVPHELP_TITLE + "");
        commandSender.sendMessage(SOUP_USAGE + "" + ChatColor.WHITE + " - " + SOUP_DESCRIPTION);
        commandSender.sendMessage(REFRESHKIT_USAGE + "" + ChatColor.WHITE + " - " + REFRESHKIT_DESCRIPTION);
        commandSender.sendMessage(KIT_USAGE + "" + ChatColor.WHITE + " - " + KIT_DESCRIPTION);
        commandSender.sendMessage(SHOP_USAGE + "" + ChatColor.WHITE + " - " + SHOP_DESCRIPTION);
        commandSender.sendMessage(EVENTSHOP_USAGE + "" + ChatColor.WHITE + " - " + EVENTSHOP_DESCRIPTION);
        commandSender.sendMessage(PRESTIGE_USAGE + "" + ChatColor.WHITE + " - " + PRESTIGE_DESCRIPTION);
        commandSender.sendMessage(KITNAME_USAGE + "" + ChatColor.WHITE + " - " + KITNAME_DESCRIPTION);
        commandSender.sendMessage(VIEWKIT_USAGE + "" + ChatColor.WHITE + " - " + VIEWKIT_DESCRIPTION);
        commandSender.sendMessage(VIEWSTATS_USAGE + "" + ChatColor.WHITE + " - " + VIEWSTATS_DESCRIPTION);
        commandSender.sendMessage(TRIGGEREVENT_USAGE + "" + ChatColor.WHITE + " - " + TRIGGEREVENT_DESCRIPTION);
        commandSender.sendMessage(ECO_USAGE + "" + ChatColor.WHITE + " - " + ECO_DESCRIPTION);
        commandSender.sendMessage(EVENTECO_USAGE + "" + ChatColor.WHITE + " - " + EVENTECO_DESCRIPTION);
        commandSender.sendMessage(REFUNDKS_USAGE + "" + ChatColor.WHITE + " - " + REFUNDKS_DESCRIPTION);
        commandSender.sendMessage(SETSTATS_USAGE + "" + ChatColor.WHITE + " - " + SETSTATS_DESCRIPTION);
        commandSender.sendMessage(RESETSTATS_USAGE + "" + ChatColor.WHITE + " - " + RESETSTATS_DESCRIPTION);
        commandSender.sendMessage(RESETGANGPOINTS_USAGE + "" + ChatColor.WHITE + " - " + RESETGANGPOINTS_DESCRIPTION);
        commandSender.sendMessage(LOCK_UNLOCK_USAGE + "" + ChatColor.WHITE + " - " + LOCK_UNLOCK_DESCRIPTION);
        commandSender.sendMessage(KITSUNLOCKED_USAGE + "" + ChatColor.WHITE + " - " + KITSUNLOCKED_DESCRIPTION);
        commandSender.sendMessage(ABILITYTOGGLE_USAGE + "" + ChatColor.WHITE + " - " + ABILITYTOGGLE_DESCRIPTION);
    }
}
