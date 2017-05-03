package me.bukkit.kitpvp.commands;


import me.bukkit.kitpvp.coreclasses.commands.Command;
import me.bukkit.kitpvp.coreclasses.member.Member;
import me.bukkit.kitpvp.coreclasses.member.Permission;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
import me.bukkit.kitpvp.ui.ViewkitMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;

public class CommandViewKit extends Command<KitManager> implements Listener {

	public CommandViewKit(KitManager module) {
		super(module, "View a kit.", Permission.NONE, "viewkit");
		setUsage("<kitname>");
	}

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
		if (!checkArgs(member, aliasUsed, args, 1))
			return;
		Kit kit = null;
		for (Map.Entry<KitType, Kit> entry : getModule().getKits().entrySet())
			if (entry.getValue().getName().equalsIgnoreCase(args[0]))
				kit = entry.getValue();
		if (kit == null) {
			couldNotFind(member, "kitname", args[0]);
			return;
		}
		if (!kit.isEnabled()) {
			member.message("This kit is §adisabled§7.");
			return;
		}
		new ViewkitMenu(getModule(), kit).open(member);
	}

	@EventHandler
	public void onShopClick(InventoryClickEvent e) {
		if (e.getClickedInventory() != null && e.getClickedInventory().getName() != null && e.getClickedInventory().getName().contains("§aView"))
			e.setCancelled(true);
	}

}
