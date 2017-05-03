package me.bukkit.kitpvp.commands.staff;

import me.bukkit.kitpvp.coreclasses.commands.Command;
import me.bukkit.kitpvp.coreclasses.member.Member;
import me.bukkit.kitpvp.coreclasses.member.Permission;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
import me.bukkit.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class CommandUnlock extends Command<KitManager> {

	public CommandUnlock(KitManager module) {
		super(module, "Unlock kits for a player", Permission.ADMIN, "unlock", "unlockkit", "lock", "lockkit" );
		setUsage("<player>", "<kit/all>", "<lock/unlock>");
	}

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
		if (!checkArgs(member, aliasUsed, args, getUsage().length))
			return;
		if (!getPlayer(member, args[0])) 
			return;
		Player target = Bukkit.getPlayer(args[0]);
		KitPvPStats stats = getModule().getKitPvP().getStats(target);
		
		if (args[2].equalsIgnoreCase("unlock")) {
			if (args[1].equalsIgnoreCase("all")) {
				getModule().getKits().keySet().forEach(stats::addKit);
				member.message("§a" + target.getName() + " §7now has all kits unlocked.");
				if (!member.getPlayer().equals(target))
					target.sendMessage("§aAll §7kits got unlocked.");
				return;
			}
			Kit kit = getKit(stats, args, member);
			if (kit == null)
				return;
			stats.addKit(kit.getKitType());
			member.message("§a" + target.getName() + " §7now has the §a" + kit.getName() + " §7kit unlocked.");
			if (!member.getPlayer().equals(target))
				target.sendMessage("§a" + kit.getName() + " §7got unlocked.");
			return;
			
		} else if (args[2].equalsIgnoreCase("lock")) {
			if (args[1].equalsIgnoreCase("all")) {
				stats.resetKits();
				member.message("§a" + target.getName() + " §7now has all kits locked.");
				return;
			}
			Kit kit = getKit(stats, args, member);
			if (kit == null)
				return;
			stats.removeKit(kit.getKitType());
			
			member.message("§a" + target.getName() + " §7now has the §a" + kit.getName() + " §7kit locked.");
			if (!member.getPlayer().equals(target))
				target.sendMessage("§a" + kit.getName() + " §7got locked.");
			return;
		}
		member.message(getUsageToString());
	}
	
	private Kit getKit(KitPvPStats stats, String[] args, Member member) {
		Kit kit = null;
		for (Map.Entry<KitType, Kit> entry : getModule().getKits().entrySet())
			if (entry.getValue().getName().equalsIgnoreCase(args[1]))
				kit = entry.getValue();
		if (kit == null) {
			couldNotFind(member, "kit", args[1]); 
			return null;
		}
		return kit;
	}

}
