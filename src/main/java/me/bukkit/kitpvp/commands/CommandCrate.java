package me.bukkit.kitpvp.commands;

import me.bukkit.kitpvp.coreclasses.commands.Command;
import me.bukkit.kitpvp.coreclasses.member.Member;
import me.bukkit.kitpvp.coreclasses.member.Permission;
import me.bukkit.kitpvp.coreclasses.utils.Recharge;
import me.bukkit.kitpvp.coreclasses.utils.UtilMath;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
import me.bukkit.kitpvp.runnable.CrateRunnable;
import me.bukkit.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.*;

public class CommandCrate extends Command<KitManager> implements Listener {

	private final Map<UUID, Integer> crateCooldown = new HashMap<>();

	public CommandCrate(KitManager module) {
		super(module, "Randomly unlock a new kit.", Permission.NONE, "crate");
		Bukkit.getPluginManager().registerEvents(this, getModule().getPlugin());
	}

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
		KitPvPStats stats = getModule().getKitPvP().getStats(member);
		if (crateCooldown.containsKey(member.getUUID())) {
			if (!Recharge.recharge(member, "KitPvP Crate", crateCooldown.get(member.getUUID()))) {
				member.message("§7You can't use the crate yet.");
				return;
			}
		}
		if (stats.getCrateKeys() <= 0) {
			member.message("§7Not enough §akeys§7.");
			return;
		}
		List<KitType> kits = new ArrayList<>();
		int counter = 1;
		for (KitType kit : KitType.values()) {
            if (kit != KitType.KITMASTER && kit.getKit().isEnabled() && !stats.hasKit(kit)) {
                if (counter++ >= 54)
                    break;
                kits.add(kit);
            }
        }

		if (kits.isEmpty()) {
			member.message("§7All kits are §aunlocked§7.");
			return;
		}

		stats.setCrateKeys(stats.getCrateKeys() - 1);
		int randomNum = UtilMath.getRandom(0, kits.size() - 1);
		crateCooldown.put(member.getUUID(), randomNum/3);
		CrateRunnable runnable = new CrateRunnable(randomNum, member.getPlayer(), kits, stats);
		runnable.runTaskTimer(getModule().getPlugin(), 0, 7);
	}
	
	@EventHandler
	public void on(InventoryClickEvent e) {
		if (e.getClickedInventory() != null && e.getClickedInventory().getName() != null && e.getClickedInventory().getName().equalsIgnoreCase("§aCrate"))
			e.setCancelled(true);
	}

}