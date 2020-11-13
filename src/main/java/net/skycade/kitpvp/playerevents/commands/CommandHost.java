package net.skycade.kitpvp.playerevents.commands;

import net.skycade.SkycadeCombat.SkycadeCombatPlugin;
import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.kitpvp.ui.HostMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class CommandHost extends SkycadeCommand {
    public CommandHost() {
        super("host");
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof Player){
            Player p = (Player) commandSender;
            new HostMenu().open(p);
        }
    }
}