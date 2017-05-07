package net.skycade.kitpvp.commands;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.kit.KitManager;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class CommandAchievements extends Command<KitManager> {
	
	public CommandAchievements(KitManager module) {
		super(module, "View your achievements", new Permission("kitpvp.default", PermissionDefault.TRUE), "ach", "achievements");
	}

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
		getModule().getAchievementsMenu().open(member);
	}

}
