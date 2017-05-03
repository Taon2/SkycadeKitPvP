package me.bukkit.kitpvp.commands.staff;

import me.bukkit.kitpvp.coreclasses.commands.Command;
import me.bukkit.kitpvp.coreclasses.member.Member;
import me.bukkit.kitpvp.coreclasses.member.Permission;
import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class CommandKitsUnlocked extends Command<KitManager> implements Listener {

	public CommandKitsUnlocked(KitManager module) {
		super(module, "See the kits someone has unlocked.", Permission.ADMIN, "kitsunlocked", "seekits", "viewkits");
		setUsage("<player>");
		Bukkit.getPluginManager().registerEvents(this, getModule().getPlugin());
	}

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
		if (!checkArgs(member, aliasUsed, args))
			return;
		if (!getPlayer(member, args[0])) 
			return;
		Player target = Bukkit.getPlayer(args[0]);
		Inventory kits = Bukkit.createInventory(null, 9 * 6, target.getName() + "'s kits.");
		
		getModule().getKits().keySet().forEach(k -> {
			KitPvPStats stats = getModule().getKitPvP().getStats(target);
			Kit kit = getModule().getKits().get(k);
			if (kit.isEnabled()) {
				if (!stats.hasKit(k))
					kits.addItem(new ItemBuilder(Material.BEDROCK).setName("§c" + kit.getName()).build());
				else
					kits.addItem(new ItemBuilder(getModule().getKits().get(k).getIcon()).setName("§a" + kit.getName()).addLore("").addLore(kit.getDescription()).addLore("")
							.addLore("§7Level: §f" + stats.getKits().get(k).getLevel()).setGlow(stats.getActiveKit() == k).build());
			}
		});
		member.getPlayer().openInventory(kits);
	}
	
	@EventHandler
	public void on(InventoryClickEvent e) {
		if (e.getInventory().getName().contains("'s kits")) {
			e.setCancelled(true);
		}
	}
}