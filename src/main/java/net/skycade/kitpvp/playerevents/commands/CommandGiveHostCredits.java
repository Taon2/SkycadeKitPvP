package net.skycade.kitpvp.playerevents.commands;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.SkycadeCore.utility.command.addons.Permissible;
import net.skycade.crates.CratesPlugin;
import net.skycade.crates.crates.Crate;
import net.skycade.kitpvp.playerevents.EventType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
@Permissible("kitpvp.admin")
public class CommandGiveHostCredits extends SkycadeCommand {
    public CommandGiveHostCredits() {
        super("givehostcredit");
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] args) {
        if (args.length == 0){
            commandSender.sendMessage("/givehostcredit (player) (amount)");
            return;
        }
        if (args.length == 1){
            commandSender.sendMessage("/givehostcredit (player) (amount)");
            return;
        }
        UUID targetUUID = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
        if (targetUUID == null){
            commandSender.sendMessage("invalid player");
            return;
        }
        try {
            int amount = Integer.parseInt(args[1]);
            Crate crate = CratesPlugin.getInstance().getEditorModule().getCrate("hostcredit");
            crate.getKey().give(targetUUID, amount);
            commandSender.sendMessage("you gave " + amount + " host credits to " + Bukkit.getOfflinePlayer(targetUUID).getName());
        }catch (Exception e){
            commandSender.sendMessage("Please use a number");
            return;
        }
    }
}
