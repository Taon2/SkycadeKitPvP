package net.skycade.kitpvp.commands;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.Permission;
import net.skycade.kitpvp.kit.KitManager;

public class CommandAchievements extends Command<KitManager> {
	
	public CommandAchievements(KitManager module) {
		super(module, "View your achievements", Permission.NONE, "ach", "achievements");
	}

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
		getModule().getAchievementsMenu().open(member);
	}

}
