package net.skycade.kitpvp.commands;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.events.DoubleCoinsEvent;
import net.skycade.kitpvp.events.RandomEvent;
import net.skycade.kitpvp.ui.EventShopMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.skycade.kitpvp.Messages.CANNOT_USE;

public class CommandEventShop extends SkycadeCommand {
    public CommandEventShop() {
        super("eventshop");
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        Member member = MemberManager.getInstance().getMember((Player) commandSender);

        if (RandomEvent.getCurrent() == null || DoubleCoinsEvent.isActive()) {
            new EventShopMenu(KitPvP.getInstance().getEventShopManager(), member).open(member.getPlayer());
        } else {
            CANNOT_USE.msg(member.getPlayer(), "%thing%", "/eventshop", "%reason%", "during events");
        }
    }
}
