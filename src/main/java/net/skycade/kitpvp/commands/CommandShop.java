package net.skycade.kitpvp.commands;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.Permission;
import net.skycade.kitpvp.kit.KitManager;

public class CommandShop extends Command<KitManager> {

	public CommandShop(KitManager module) {
		super(module, "Opens the shop GUI.", Permission.NONE, "shop");
	}

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
		getModule().getShopMenu().open(member);
	}

}
