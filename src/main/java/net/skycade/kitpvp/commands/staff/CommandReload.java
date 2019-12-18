package net.skycade.kitpvp.commands.staff;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class CommandReload extends Command<KitManager> {
    public CommandReload(KitManager module) {
        super(module, "Reloads configuration files.", new Permission("kitpvp.admin", PermissionDefault.OP), "kitpvpreload");
    }

    @Override
    public void execute(Member member, String aliasUsed, String... args) {
        for (Kit kit : getModule().getKits().values()) {
            if (kit.isEnabled()) kit.reloadConfig();
        }
        member.getPlayer().sendMessage("Reloaded!");
    }
}
