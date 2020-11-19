package net.skycade.kitpvp.playerevents.commands;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.SkycadeCore.utility.command.addons.Permissible;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.playerevents.EventManager;
import org.bukkit.command.CommandSender;

@Permissible("kitpvp.admin")
public class CommandResetCooldown extends SkycadeCommand {
    public CommandResetCooldown() {
        super("resetcooldown");
    }


    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        if (!getEventManager().isCooldownOn()){
            commandSender.sendMessage("An event has not been hosted recently.");
            return;
        }
        getEventManager().setCooldownOn(false);
        getEventManager().setGlobalCooldown(0);
        commandSender.sendMessage("The Event cooldown has been wiped!");
        return;
    }

    private EventManager getEventManager(){
        return KitPvP.getInstance().getEventManager();
    }

}
