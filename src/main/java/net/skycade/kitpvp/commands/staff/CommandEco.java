package net.skycade.kitpvp.commands.staff;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.Permission;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandEco extends Command<KitManager> {

	public CommandEco(KitManager module) {
		super(module, "Manage coins", Permission.ADMIN, "economy", "eco");
		setUsage("<reset/give/take> <player/all> <amount>");
	}

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
		if (!checkArgs(member, aliasUsed, args, 2))
			return;
		int amount = 0;
		if (!args[0].equalsIgnoreCase("reset")) {
			if (!checkArgs(member, aliasUsed, args))
				return;
			if (!parseInt(member, args[2])) {
				couldNotFind(member, "amount", args[2]);
				return;
			}
			amount = Integer.parseInt(args[2]);
		}
		
		if (args[1].equalsIgnoreCase("all")) {
			if (args[0].equalsIgnoreCase("reset")) {
				Bukkit.getOnlinePlayers().forEach(p -> resetCredits(getModule().getKitPvP().getStats(p), member, p));
				return;
			}
			if (args[0].equalsIgnoreCase("give")) {
			    Bukkit.getOnlinePlayers().forEach(p -> incCredits(getModule().getKitPvP().getStats(p), member, p, Integer.parseInt(args[2])));
				return;
			}
			if (args[0].equalsIgnoreCase("take")) {
			    Bukkit.getOnlinePlayers().forEach(p -> takeCredits(getModule().getKitPvP().getStats(p), member, p, Integer.parseInt(args[2])));
				return;
			}
		}
		if (!getPlayer(member, args[1])) {
			couldNotFind(member, "player", args[1]);
			return;
		}
		Player target = Bukkit.getPlayer(args[1]);
		KitPvPStats targetStats = getModule().getKitPvP().getStats(target);
		
		if (args[0].equalsIgnoreCase("reset")) {
			resetCredits(targetStats, member, target);
			return;
		}
		if (args[0].equalsIgnoreCase("give")) 
			incCredits(targetStats, member, target, amount);			
		else if (args[0].equalsIgnoreCase("take")) 
			takeCredits(targetStats, member, target, amount);
	}
	
	private void resetCredits(KitPvPStats targetStats, Member member, Player target) {
		targetStats.setCoins(0);
		target.sendMessage("§7Your coins got §Areset§7.");
		member.message("§7" + target.getName() + "'s coins got §Areset§7." );
	}
	
	private void incCredits(KitPvPStats targetStats, Member member, Player target, int amount) {
		targetStats.setCoins(targetStats.getCoins() + amount);
		target.sendMessage("§7You got §a" + amount + "§7 coins, your total balance is now §a" + targetStats.getCoins() + "§7 coins.");
		member.message("§a" + amount + "§7 credits given to §a" + target.getName() + "§7.");
	}
	
	private void takeCredits(KitPvPStats targetStats, Member member, Player target, int amount) {
		if (targetStats.getCoins() - amount < 0)
			resetCredits(targetStats, member, target);
		else {
			targetStats.setCoins(targetStats.getCoins() - amount);
			target.sendMessage("§7Your balance got lowered with §a" + amount + "§7 credits." );
			member.message("§a" + target.getName() + "'s §7balance got lowered with §a" + amount + "§7coins to §a" + targetStats.getCoins() + "§7." );
		}
	}

}
