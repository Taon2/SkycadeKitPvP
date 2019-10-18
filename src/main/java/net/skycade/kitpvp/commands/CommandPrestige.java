package net.skycade.kitpvp.commands;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.ui.PrestigeMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPrestige extends SkycadeCommand {
    public CommandPrestige() {
        super("prestige");
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        Member member = MemberManager.getInstance().getMember((Player) commandSender);

        new PrestigeMenu(member, 1).open(member.getPlayer());
    }
}
