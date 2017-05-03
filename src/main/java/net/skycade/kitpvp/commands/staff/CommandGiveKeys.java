package net.skycade.kitpvp.commands.staff;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.Permission;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandGiveKeys extends Command<KitManager> {

	public CommandGiveKeys(KitManager module) {
		super(module, "Manage keys", Permission.DEV, "keys", "givekey", "givekeys");
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
				Bukkit.getOnlinePlayers().forEach(p -> resetKeys(getModule().getKitPvP().getStats(p), member, p));
				return;
			}
			if (args[0].equalsIgnoreCase("give")) {
			    Bukkit.getOnlinePlayers().forEach(p -> incKeys(getModule().getKitPvP().getStats(p), member, p, Integer.parseInt(args[2])));
				return;
			}
			if (args[0].equalsIgnoreCase("take")) {
			    Bukkit.getOnlinePlayers().forEach(p -> takeKeys(getModule().getKitPvP().getStats(p), member, p, Integer.parseInt(args[2])));
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
			resetKeys(targetStats, member, target);
			return;
		}
		if (args[0].equalsIgnoreCase("give")) 
			incKeys(targetStats, member, target, amount);			
		else if (args[0].equalsIgnoreCase("take")) 
			takeKeys(targetStats, member, target, amount);
	}
	
	private void resetKeys(KitPvPStats targetStats, Member member, Player target) {
		targetStats.setCrateKeys(0);
		target.sendMessage("§7Your keys got §Areset§7.");
		member.message("§7" + target.getName() + "'s keys got §Areset§7." );
	}
	
	private void incKeys(KitPvPStats targetStats, Member member, Player target, int amount) {
		targetStats.setCrateKeys(targetStats.getCrateKeys() + amount);
		if (amount == 1) {
			target.sendMessage("§7You got §a" + amount + "§7 key, your total amount is now §a" + targetStats.getCrateKeys() + "§7 keys.");
			member.message("§a" + amount + "§7 key given to §a" + target.getName() + "§7.");
		} else { 
			target.sendMessage("§7You got §a" + amount + "§7 keys, your total amount is now §a" + targetStats.getCrateKeys() + "§7 keys.");
			member.message("§a" + amount + "§7 keys given to §a" + target.getName() + "§7.");
		}
	}
	
	private void takeKeys(KitPvPStats targetStats, Member member, Player target, int amount) {
		if (targetStats.getCrateKeys() - amount < 0)
			resetKeys(targetStats, member, target);
		else {
			targetStats.setCrateKeys(targetStats.getCrateKeys() - amount);
			if (amount == 1)
				member.message("§a" + target.getName() + "'s §7keys got lowered with §a" + amount + " §7key to §a" + targetStats.getCrateKeys() + "§7." );
			else
				member.message("§a" + target.getName() + "'s §7keys got lowered with §a" + amount + " §7keys to §a" + targetStats.getCrateKeys() + "§7." );
		}
	}

}
