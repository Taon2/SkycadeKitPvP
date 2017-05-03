package me.bukkit.kitpvp.commands.staff;

import me.bukkit.kitpvp.coreclasses.commands.Command;
import me.bukkit.kitpvp.coreclasses.member.Member;
import me.bukkit.kitpvp.coreclasses.member.MemberManager;
import me.bukkit.kitpvp.coreclasses.member.Permission;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;

public class CommandViewStats extends Command<KitManager> {

	public CommandViewStats(KitManager module) {
		super(module, "See stats for a player.", Permission.ADMIN, "viewstats", "seestats");
		setUsage("<player>");
	}

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
		if (!checkArgs(member, aliasUsed, args))
			return;
		if (!getPlayer(member, args[0])) 
			return;
		Member target = MemberManager.getInstance().getMember(Bukkit.getPlayer(args[0]));
		KitPvPStats stats = getModule().getKitPvP().getStats(target);
		member.message("§7----------§f§l" + target.getName() + "'s KitPvP stats §7----------");
		member.message("§fKit - §a" + stats.getActiveKit().getKit().getName());
		member.message("§fCoins - §a" + stats.getCoins());
		member.message("§fCrate keys - §a" + stats.getCrateKeys());
		member.message("§fDeaths - §a" + stats.getDeaths());
		member.message("§fHighest killstreak - §a" + stats.getHighestStreak());
		member.message("§fKills - §a" + stats.getKills());
		member.message("§fKillstreak - §a" + stats.getStreak());
		member.message("§fAssists - §a" + stats.getAssists());
		member.message("§fDuels - §a" + stats.getDuels());
	}

}
