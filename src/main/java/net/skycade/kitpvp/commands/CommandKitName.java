package net.skycade.kitpvp.commands;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.Permission;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandKitName extends Command<KitManager> {

	public CommandKitName(KitManager module) {
		super(module, "Get the kitname from a player", Permission.NONE, "kitname");
		setUsage("<player>");
	}

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
		if (!checkArgs(member, aliasUsed, args, 1))
			return;
		if (!getPlayer(member, args[0])) 
			return;
		Player target = Bukkit.getPlayer(args[0]);
		KitPvPStats stats = getModule().getKitPvP().getStats(target);
		member.message("§a" + target.getName() + "§7 is using the §a" + stats.getActiveKit().getKit().getName() + "§7 kit at level §a" + stats.getActiveKit().getKit().getLevel(target) + "§7.");
	}

}
