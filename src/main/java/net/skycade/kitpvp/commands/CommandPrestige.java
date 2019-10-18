package net.skycade.kitpvp.commands;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.ui.PrestigeMenu;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class CommandPrestige extends Command<KitManager> {

    public CommandPrestige(KitManager module) {
        super(module, "Opens the prestige GUI.", new Permission("kitpvp.default", PermissionDefault.TRUE), "prestige", "rankup", "levelup", "prestigegui");
    }

    @Override
    public void execute(Member member, String aliasUsed, String... args) {
        new PrestigeMenu(member, 1).open(member.getPlayer());
    }

}
