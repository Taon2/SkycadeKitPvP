package me.bukkit.kitpvp.commands;

import me.bukkit.kitpvp.coreclasses.commands.Command;
import me.bukkit.kitpvp.coreclasses.member.Member;
import me.bukkit.kitpvp.coreclasses.member.Permission;
import me.bukkit.kitpvp.kit.KitManager;

public class CommandShop extends Command<KitManager> {

	public CommandShop(KitManager module) {
		super(module, "Opens the shop GUI.", Permission.NONE, "shop");
	}

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
		getModule().getShopMenu().open(member);
	}

}
