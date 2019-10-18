package net.skycade.kitpvp.commands;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.SkycadeCore.vanish.VanishStatus;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.skycade.kitpvp.Messages.COULDNT_FIND;
import static net.skycade.kitpvp.Messages.USING_KIT;

public class CommandKitName extends SkycadeCommand {
    public CommandKitName() {
        super("kitname");
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        if (strings.length < 1)
            return;
        if (Bukkit.getPlayer(strings[0]) == null)
            return;

        Member member = MemberManager.getInstance().getMember((Player) commandSender);
        Player target = Bukkit.getPlayer(strings[0]);
        if (VanishStatus.isVanished(target.getUniqueId())) {
            COULDNT_FIND.msg(member.getPlayer(), "%type%", "player", "%thing%", strings[0]);
            return;
        }
        KitPvPStats stats = KitPvP.getInstance().getStats(target);
        USING_KIT.msg(member.getPlayer(), "%player%", target.getName(), "%kitname%", stats.getActiveKit().getKit().getName());
    }
}
