package net.skycade.kitpvp.events;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.SkycadeCore.utility.command.addons.Permissible;
import org.bukkit.command.CommandSender;

@Permissible("kitpvp.admin")
public class TriggerEventCommand extends SkycadeCommand {
    public TriggerEventCommand() {
        super("triggerevent");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (RandomEvent.getCurrent() != null || args.length < 1) {
            return;
        }

        RandomEvent event = RandomEvent.getEvents().stream().filter(e -> e.getName().equalsIgnoreCase(args[0]))
                .findAny().orElse(null);

        if (event == null) {
            sender.sendMessage("No such event.");
            return;
        }

        RandomEvent.startEvent(event);
    }
}
