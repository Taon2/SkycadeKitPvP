package me.bukkit.kitpvp.commands.staff;

import me.bukkit.kitpvp.coreclasses.commands.Command;
import me.bukkit.kitpvp.coreclasses.member.Member;
import me.bukkit.kitpvp.coreclasses.member.Permission;
import me.bukkit.kitpvp.kit.KitManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandTp extends Command<KitManager> {

	public CommandTp(KitManager module) {
		super(module, "Tp commands", Permission.SR_MOD, "tpo");
		setUsage("<player/all>", "<here>");
	}

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
		if (!checkArgs(member, aliasUsed, args, getUsage().length - 1))
			return;
		if (args[0].equalsIgnoreCase("all")) {
			Bukkit.getOnlinePlayers().forEach(p -> p.teleport(member.getPlayer().getLocation()));
			return;
		} 
		if (!getPlayer(member, args[0])) {
			return;
		} 
		Player target = Bukkit.getPlayer(args[0]);
		if (args.length == 1) {
			member.getPlayer().teleport(target.getLocation());
		} else
			target.teleport(member.getPlayer().getLocation());
	}

}
