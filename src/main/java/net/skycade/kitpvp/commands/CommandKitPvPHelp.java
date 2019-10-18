package net.skycade.kitpvp.commands;

import com.google.common.collect.ImmutableList;
import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandKitPvPHelp extends SkycadeCommand {

    public CommandKitPvPHelp() {
        super("kitpvphelp", ImmutableList.of("kithelp", "kitpvpcommands", "kitcommands"));
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        Member member = MemberManager.getInstance().getMember((Player) commandSender);

        //todo send usages
    }
}
