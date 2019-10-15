package net.skycade.kitpvp.commands.staff;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.events.RandomEvent;
import net.skycade.kitpvp.kit.KitManager;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import static net.skycade.kitpvp.Messages.NO_SUCH_EVENT;

public class TriggerEventCommand extends Command<KitManager> {
    public TriggerEventCommand(KitManager module) {
        super(module, "Starts a KitPvP event.", new Permission("kitpvp.admin", PermissionDefault.OP), "triggerevent", "startevent");
        setUsage("<player>");
    }

    @Override
    public void execute(Member member, String aliasUsed, String... args) {
        if (RandomEvent.getCurrent() != null || args.length < 1) {
            return;
        }

        RandomEvent event = RandomEvent.getEvents().stream().filter(e -> e.getName().equalsIgnoreCase(args[0]))
                .findAny().orElse(null);

        if (event == null) {
            NO_SUCH_EVENT.msg(member.getPlayer());
            return;
        }

        RandomEvent.startEvent(event);
    }
}
