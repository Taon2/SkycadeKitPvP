package net.skycade.kitpvp.commands.staff;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class CommandSetStats extends Command<KitManager> {

	public CommandSetStats(KitManager module) {
		super(module, "Change stats for a player", new Permission("kitpvp.admin", PermissionDefault.OP), "setstats", "setstat");
		setUsage("<player>", "<stats>", "<amount>");
	}

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
		if (!checkArgs(member, aliasUsed, args))
			return;
		if (!getPlayer(member, args[0])) 
			return;
		if (!parseInt(member, args[2])) {
			couldNotFind(member, "amount", args[2]);
			return;
		}
		int amount = Integer.parseInt(args[2]);
		Player target = Bukkit.getPlayer(args[0]);
		KitPvPStats stats = getModule().getKitPvP().getStats(target);
		
		if (args[1].equalsIgnoreCase("kills") || args[1].equalsIgnoreCase("kill")) {
			stats.setKills(amount);
			sendMsg("kills", stats, member, target, amount);
		} else if (args[1].equalsIgnoreCase("deaths")) {
			stats.setDeaths(amount);
			sendMsg("deaths", stats, member, target, amount);
		} else if (args[1].equalsIgnoreCase("killstreak") || args[1].equalsIgnoreCase("ks")) {
			stats.setStreak(amount);
			sendMsg("killstreak", stats, member, target, amount);
		} else if (args[1].equalsIgnoreCase("highestkillstreak") || args[1].equalsIgnoreCase("highks") || args[1].equalsIgnoreCase("highkillstreak") || args[1].equalsIgnoreCase("highestks")) {
			stats.setHighestStreak(amount);
			sendMsg("highestks", stats, member, target, amount);
		} else if (args[1].equalsIgnoreCase("assist") || args[0].equalsIgnoreCase("assists")) {
			stats.setAssists(amount);
			sendMsg("assist", stats, member, target, amount);
		} else if (args[1].equalsIgnoreCase("duel") || args[1].equalsIgnoreCase("duels")) {
		    stats.setDuels(amount);
		    sendMsg("duels", stats, member, target, amount);
		} else 
			couldNotFind(member, "stats", args[1]);
	}
	
	private void sendMsg(String stat, KitPvPStats stats, Member member, Player target, int amount) {
	    target.sendMessage("§7Your " + stat + " has been set to " + amount + ".");
        member.message(target.getName() + " " + stat + "  has been set to " + amount + ".");
	}

}
