package net.skycade.kitpvp.commands;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.ui.ViewKitMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

import static net.skycade.kitpvp.Messages.COULDNT_FIND;
import static net.skycade.kitpvp.Messages.KIT_DISABLED;

public class CommandViewKit extends SkycadeCommand {
    public CommandViewKit() {
        super("viewkit");
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        Member member = MemberManager.getInstance().getMember((Player) commandSender);

        if (strings.length < 1)
            return;
        Kit kit = null;
        for (Map.Entry<KitType, Kit> entry : KitPvP.getInstance().getKitManager().getKits().entrySet())
            if (entry.getValue().getName().equalsIgnoreCase(strings[0]))
                kit = entry.getValue();
        if (kit == null) {
            COULDNT_FIND.msg(member.getPlayer(), "%type%", "kit name", "%thing%", strings[0]);
            return;
        }
        if (!kit.isEnabled()) {
            KIT_DISABLED.msg(member.getPlayer());
            return;
        }

        new ViewKitMenu(kit).open(member.getPlayer());
    }
}
