package me.bukkit.kitpvp.commands;

import me.bukkit.kitpvp.coreclasses.commands.Command;
import me.bukkit.kitpvp.coreclasses.member.Member;
import me.bukkit.kitpvp.coreclasses.member.Permission;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
import me.bukkit.kitpvp.stat.KitPvPStats;

import java.util.Map;

public class CommandUpgrade extends Command<KitManager> {

	public CommandUpgrade(KitManager module) {
		super(module, "Upgrade your kits.", Permission.NONE, "upgrade", "upgradekit");
	}

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
	    if (args.length <= 0) {
	        member.message("The upgrade GUI isn't implemented yet.");
//	        getModule().getUpgradeMenu().open(member);
	        return;
	    }
	    Kit kit = null;
        for (Map.Entry<KitType, Kit> entry : getModule().getKits().entrySet())
            if (entry.getValue().getName().equalsIgnoreCase(args[0]))
                kit = entry.getValue();
        if (kit == null) {
            couldNotFind(member, "kitname", args[0]);
            return;
        }
        if (kit.getKitType() == KitType.KITMASTER) {
            member.message("KitMaster can't be upgraded.");
            return;
        }
        if (kit.getLevel(member.getPlayer()) >= 3) {
            member.message("Your kit is already §amax level§7.");
            return;
        }
        
        KitPvPStats stats = getModule().getKitPvP().getStats(member);
        int cost = kit.getLevel(member.getPlayer()) == 1 ? kit.getPrice() / 5 : kit.getPrice() / 3;
        if (stats.getCoins() - cost < 0) {
            member.message("§7You don't have enough §amoney §7to upgrade §a" + kit.getName() + "§7 to level " +  kit.getLevel(member.getPlayer()) + ".");
            return;
        }
        getModule().getKitPvP().getStats(member).getKits().get(kit.getKitType()).setLevel(kit.getLevel(member.getPlayer()) + 1);
        member.message("§7You upgraded §a" + kit.getName() + "§7 for §a" + cost + "§7 coins.");
        stats.setCoins(stats.getCoins() - cost);
	    
	}
}
