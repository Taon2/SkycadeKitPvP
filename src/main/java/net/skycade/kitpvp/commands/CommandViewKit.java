package net.skycade.kitpvp.commands;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.ui.ViewKitMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

import static net.skycade.kitpvp.Messages.*;

public class CommandViewKit extends SkycadeCommand {
    public CommandViewKit() {
        super("viewkit");
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        if (strings.length < 1) {
            VIEWKIT_USAGE.msg(commandSender);
            return;
        }

        Kit kit = null;
        for (Map.Entry<KitType, Kit> entry : KitPvP.getInstance().getKitManager().getKits().entrySet())
            if (entry.getKey().name().equalsIgnoreCase(strings[0]))
                kit = entry.getValue();

        if (kit == null) {
            COULDNT_FIND.msg(commandSender, "%type%", "kit name", "%thing%", strings[0]);
            return;
        }

        if (!kit.isEnabled()) {
            KIT_DISABLED.msg(commandSender);
            return;
        }

        new ViewKitMenu(kit).open((Player) commandSender);
    }
}
