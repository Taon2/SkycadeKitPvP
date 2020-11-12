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

public class CommandJoin extends SkycadeCommand {
    public CommandJoin() {
        super("join");
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof Player){
            Player p = (Player) commandSender;
            if (!KitPvP.getInstance().isInSpawnArea(p)){
                Messages.MUST_BE_AT_SPAWN.msg(p);
                return;
            }
            EventManager manager = KitPvP.getInstance().getEventManager();
            if (manager.getCurrentEvent() == null){
                Messages.NO_EVENT_RUNNING.msg(p);
                return;
            }
            if (!manager.isJoinable()){
                Messages.CANNOT_JOIN_EVNET.msg(p);
                return;
            }
            if (manager.getCurrentEvent() == EventType.SUMO){
                SumoEvent event = KitPvP.getInstance().getEventManager().getSumoEvent();
                if (event.isPlaying(p)){
                    Messages.ALREADY_IN_EVENT.msg(p);
                    return;
                }
                event.join(p);
            }
            if (manager.getCurrentEvent() == EventType.LMS){
                LastManStanding event = KitPvP.getInstance().getEventManager().getLMS();
                if (event.isPlaying(p)){
                    Messages.ALREADY_IN_EVENT.msg(p);
                    return;
                }
                event.join(p);
            }
            if (manager.getCurrentEvent() == EventType.BRACKETS){
                Brackets event = KitPvP.getInstance().getEventManager().getBrackets();
                if (event.isPlaying(p)){
                    Messages.ALREADY_IN_EVENT.msg(p);
                    return;
                }
                event.join(p);
            }
        }
    }
}
