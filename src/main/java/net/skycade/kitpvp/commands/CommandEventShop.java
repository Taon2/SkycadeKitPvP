package net.skycade.kitpvp.commands;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.events.DoubleCoinsEvent;
import net.skycade.kitpvp.events.RandomEvent;
import net.skycade.kitpvp.ui.eventshopitems.EventShopManager;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import static net.skycade.kitpvp.Messages.CANNOT_USE;

public class CommandEventShop extends Command<EventShopManager> {

    public CommandEventShop(EventShopManager eventShopManager) {
        super(eventShopManager, "Opens the event shop GUI.", new Permission("kitpvp.default", PermissionDefault.TRUE), "eventshop");
    }

    @Override
    public void execute(Member member, String aliasUsed, String... args) {
        if (RandomEvent.getCurrent() == null || DoubleCoinsEvent.isActive()) {
            getModule().getEventShopMenu().open(member);
        } else {
            CANNOT_USE.msg(member.getPlayer(), "%thing%", "/eventshop", "%reason%", "during events");
        }
    }
}
