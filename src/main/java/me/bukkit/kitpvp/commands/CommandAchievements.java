package me.bukkit.kitpvp.commands;

import me.bukkit.kitpvp.coreclasses.commands.Command;
import me.bukkit.kitpvp.coreclasses.member.Member;
import me.bukkit.kitpvp.coreclasses.member.Permission;
import me.bukkit.kitpvp.kit.KitManager;

public class CommandAchievements extends Command<KitManager> {
	
	public CommandAchievements(KitManager module) {
		super(module, "View your achievements", Permission.NONE, "ach", "achievements");
	}

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
		getModule().getAchievementsMenu().open(member);
	}

}
