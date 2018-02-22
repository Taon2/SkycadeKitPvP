package net.skycade.kitpvp.commands;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.kit.KitManager;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class CommandShop extends Command<KitManager> {

    public CommandShop(KitManager module) {
        super(module, "Opens the shop GUI.", new Permission("kitpvp.default", PermissionDefault.TRUE), "shop");
    }

    @Override
    public void execute(Member member, String aliasUsed, String... args) {
        getModule().getShopMenu().open(member);
    }

}
