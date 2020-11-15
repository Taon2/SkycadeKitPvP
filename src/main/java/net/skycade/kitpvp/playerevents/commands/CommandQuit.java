package net.skycade.kitpvp.playerevents.commands;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.Messages;
import net.skycade.kitpvp.playerevents.EventManager;
import net.skycade.kitpvp.playerevents.EventType;
import net.skycade.kitpvp.playerevents.events.Brackets;
import net.skycade.kitpvp.playerevents.events.LastManStanding;
import net.skycade.kitpvp.playerevents.events.SumoEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandQuit extends SkycadeCommand {

    public CommandQuit() {
        super("quit");
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            EventManager manager = KitPvP.getInstance().getEventManager();
            if (manager.getCurrentEvent() == null) {
                Messages.NO_EVENT_RUNNING.msg(p);
                return;
            }
            if (manager.getCurrentEvent() == EventType.SUMO) {
                SumoEvent event = KitPvP.getInstance().getEventManager().getSumoEvent();
                if (event.isPlaying(p) || event.isSpectating(p)) {
                    event.leaveEvent(p);
                }
            }
            if (manager.getCurrentEvent() == EventType.LMS) {
                LastManStanding event = KitPvP.getInstance().getEventManager().getLMS();
                if (event.isPlaying(p) || event.isSpectating(p)) {
                    event.quit(p);
                }
            }
            if (manager.getCurrentEvent() == EventType.BRACKETS) {
                Brackets event = KitPvP.getInstance().getEventManager().getBrackets();
                if (event.isPlaying(p) || event.isSpectating(p)) {
                    event.quit(p);
                }
            }
        }
    }
}
