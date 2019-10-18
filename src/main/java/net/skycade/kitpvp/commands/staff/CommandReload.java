package net.skycade.kitpvp.commands.staff;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.SkycadeCore.utility.command.addons.Permissible;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.kit.Kit;
import org.bukkit.command.CommandSender;

import static net.skycade.kitpvp.Messages.RELOADED;

@Permissible("kitpvp.admin")
public class CommandReload extends SkycadeCommand {
    public CommandReload() {
        super("kitpvpreload");
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        for (Kit kit : KitPvP.getInstance().getKitManager().getKits().values()) {
            if (kit.isEnabled()) kit.reloadConfig();
        }
        RELOADED.msg(commandSender);
    }
}
