package net.skycade.kitpvp.commands;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.Permission;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.stat.KitPvPStats;

public class CommandKitpvpStats extends Command<KitManager> {
	
	public CommandKitpvpStats(KitManager module) {
		super(module, "Get an overview of all your KitPvP related stats", Permission.NONE, "kitpvpstats", "statskitpvp", "kitstats", "statskit");
	}

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
		KitPvPStats stats = getModule().getKitPvP().getStats(member);
		member.message("§7----------§f§l KitPvP stats §7----------");
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
