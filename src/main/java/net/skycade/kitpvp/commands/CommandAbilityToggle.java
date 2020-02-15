package net.skycade.kitpvp.commands;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.events.DoubleCoinsEvent;
import net.skycade.kitpvp.events.RandomEvent;
import net.skycade.kitpvp.stat.KitPvPStats;
import net.skycade.kitpvp.ui.EventShopMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.skycade.kitpvp.Messages.*;

public class CommandAbilityToggle extends SkycadeCommand {
    public CommandAbilityToggle() {
        super("abilitytoggle");
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        Member member = MemberManager.getInstance().getMember((Player) commandSender);
        KitPvPStats stats = KitPvP.getInstance().getStats(member);

        stats.setAbilityToggle(!stats.isAbilityToggle());

        if (stats.isAbilityToggle())
            SHIFT_ABILITIES.msg(commandSender);
        else
            NO_SHIFT_ABILITIES.msg(commandSender);
    }
}
