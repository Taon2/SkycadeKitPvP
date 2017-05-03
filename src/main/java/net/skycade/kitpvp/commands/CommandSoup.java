package net.skycade.kitpvp.commands;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.Permission;
import net.skycade.kitpvp.duel.Duel;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandSoup extends Command<KitManager> {
	
	private final static int COST = 20;
	
	public CommandSoup(KitManager module) {
		super(module, "Buy soup for " + COST + " credits", Permission.NONE, "soup");
	}

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
	    if (inDuel(member)) {
	        member.message("You can't use this command when you're in a duel.");
	        return;
	    }
		KitPvPStats stats = getModule().getKitPvP().getStats(member);
		int coins = stats.getCoins();
		if (coins - COST < 0) {
			member.message("§7You don't have enough §acoins§7.");
			return;
		}
		if(!hasSpace(member.getPlayer())) {
			member.message("§7You don't have enough §aspace §7in your inventory.");
			return;
		}
		stats.getActiveKit().getKit().giveSoup(member.getPlayer(), 30);
		stats.setCoins(coins - COST);
		member.message("§7You bought soup for §a" + COST + " credits§7.");
	}
	
	private boolean hasSpace(Player p) {
		for (ItemStack item : p.getInventory())
			if (item == null || item.getType() == Material.AIR) 
				return true;
		return false;
	}
	
	private boolean inDuel(Member mem) {
        for (Duel duel : getModule().getDuels())
            if (duel.getPlayers().contains(mem.getUUID())) 
                return true;
        return false;
    }

}
