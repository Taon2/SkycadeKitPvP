package net.skycade.kitpvp.commands.staff;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.SkycadeCore.utility.command.addons.Permissible;
import net.skycade.kitpvp.events.RandomEvent;
import org.bukkit.command.CommandSender;

import java.util.Collections;

import static net.skycade.kitpvp.Messages.*;

@Permissible("kitpvp.admin")
public class CommandTriggerEvent extends SkycadeCommand {
    public CommandTriggerEvent() {
        super("triggerevent", Collections.singletonList("startevent"));
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        if (strings.length < 1) {
            TRIGGEREVENT_USAGE.msg(commandSender);
            return;
        }

        if (RandomEvent.getCurrent() != null) {
            EVENT_ALREADY_RUNNING.msg(commandSender);
            return;
        }

        RandomEvent event = RandomEvent.getEvents().stream().filter(e -> e.getName().equalsIgnoreCase(strings[0]))
                .findAny().orElse(null);

        if (event == null) {
            NO_SUCH_EVENT.msg(commandSender);
            return;
        }

        RandomEvent.startEvent(event);
    }
}
