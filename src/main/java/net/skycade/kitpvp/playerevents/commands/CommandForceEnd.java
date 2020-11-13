package net.skycade.kitpvp.playerevents.commands;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.SkycadeCore.utility.command.addons.Permissible;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.playerevents.EventManager;
import net.skycade.kitpvp.playerevents.EventType;
import net.skycade.kitpvp.ui.HostMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
@Permissible("kitpvp.admin")
public class CommandForceEnd extends SkycadeCommand {

    public CommandForceEnd() {
        super("forceend");
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof Player){
            Player p = (Player) commandSender;
            if (getEventManager().getCurrentEvent() == EventType.IDLE){
                p.sendMessage("no event running");
                return;
            }
            EventType type = getEventManager().getCurrentEvent();
            if (type == EventType.SUMO){
                p.sendMessage(ChatColor.GREEN + "You have ended the Sumo event.");
                getEventManager().getSumoEvent().forceEnd();
            }
            if (type == EventType.LMS){
                p.sendMessage(ChatColor.GREEN + "You have ended the LMS event.");
                getEventManager().getLMS().forceEnd();
            }
            if (type == EventType.BRACKETS){
                p.sendMessage(ChatColor.GREEN + "You have ended the Brackets event.");
                getEventManager().getBrackets().forceEnd();
            }
        }
    }

    private EventManager getEventManager(){
        return KitPvP.getInstance().getEventManager();
    }
}