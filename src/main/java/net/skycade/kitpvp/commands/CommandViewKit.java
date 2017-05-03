package net.skycade.kitpvp.commands;


import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.Permission;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.ui.ViewkitMenu;
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
