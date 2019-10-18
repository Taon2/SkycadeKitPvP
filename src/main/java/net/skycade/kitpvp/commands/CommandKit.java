package net.skycade.kitpvp.commands;


import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.events.KillTheKingEvent;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.ui.KitMenu;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import static net.skycade.kitpvp.Messages.CANNOT_USE;

public class CommandKit extends Command<KitManager> {

    public CommandKit(KitManager module) {
        super(module, "Opens kit GUI.", new Permission("kitpvp.default", PermissionDefault.TRUE), "kits", "kit");
    }

    @Override
    public void execute(Member member, String aliasUsed, String... args) {
        if (KillTheKingEvent.getInstance() != null && KillTheKingEvent.getInstance().getCurrentKing() != null) {
            if (member.getUUID().equals(KillTheKingEvent.getInstance().getCurrentKing())) {
                CANNOT_USE.msg(member.getPlayer(), "%thing%", "/kit", "%reason%", "as the King");
                return;
            }
        }

        new KitMenu(getModule().getKitPvP().getKitManager(), member).open(member.getPlayer());
    }

}	