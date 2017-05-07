package net.skycade.kitpvp.commands;


import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.kit.KitManager;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class CommandKit extends Command<KitManager> {

	public CommandKit(KitManager module) {
		super(module, "Opens kit GUI.", new Permission("kitpvp.default", PermissionDefault.TRUE), "kits", "kit");
	}

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
		getModule().getMenu().open(member);
	}

}	