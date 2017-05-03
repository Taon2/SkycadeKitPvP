package me.bukkit.kitpvp.commands;


import me.bukkit.kitpvp.coreclasses.commands.Command;
import me.bukkit.kitpvp.coreclasses.member.Member;
import me.bukkit.kitpvp.coreclasses.member.Permission;
import me.bukkit.kitpvp.kit.KitManager;

public class CommandKit extends Command<KitManager> {

	public CommandKit(KitManager module) {
		super(module, "Opens kit GUI.", Permission.NONE, "kits", "kit");
	}

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
		getModule().getMenu().open(member);
	}

}	