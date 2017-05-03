package me.bukkit.kitpvp.commands;

import me.bukkit.kitpvp.coreclasses.commands.Command;
import me.bukkit.kitpvp.coreclasses.member.Member;
import me.bukkit.kitpvp.coreclasses.member.Permission;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.managers.PageManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

// Not well coded, improve if you have the time to.
public class CommandKitPvPHelp extends Command<KitManager> {

	private PageManager pageManager;

	public CommandKitPvPHelp(KitManager module) {
		super(module, "Get an overview of the KitPvP commands", Permission.NONE, "kitpvphelp", "kithelp", "kitpvpcommands", "kitcommands");
        Bukkit.getScheduler().runTaskLater(getModule().getKitPvP(),
                () -> this.pageManager = new PageManager("KitPvP commands ", "/kitpvphelp ", getPageElements(), 9, 6)
        , 100);
	}

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
	    if (pageManager == null) {
	        member.message("Something went wrong.");
	        return;
        }

		int page = 1;
		if (args.length > 0) {
            if (!parseInt(member, args[0]))
                return;
            page = Integer.parseInt(args[0]);
        }
        pageManager.sendToPlayer(member.getPlayer(), page);
	}


    private List<BaseComponent[]> getPageElements() {
        List<BaseComponent[]> elements = new ArrayList<>();
        for (Command command : getModule().getCommands())
            elements.add(TextComponent.fromLegacyText("§a/" + command.getAliases()[0] + " " + command.getUsageToString() + "- §7" + command.getDescription().toLowerCase()));
        return elements;
    }

}
