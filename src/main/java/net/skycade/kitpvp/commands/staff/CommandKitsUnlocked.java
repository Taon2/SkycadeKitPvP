package net.skycade.kitpvp.commands.staff;

import com.google.common.collect.ImmutableList;
import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.SkycadeCore.utility.command.addons.Permissible;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.ui.KitMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.skycade.kitpvp.Messages.COULDNT_FIND;

@Permissible("kitpvp.admin")
public class CommandKitsUnlocked extends SkycadeCommand {
    public CommandKitsUnlocked() {
        super("kitsunlocked", ImmutableList.of("seekits", "viewkits"));
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        Member member = MemberManager.getInstance().getMember((Player) commandSender);

        if (strings.length < 1) {
            //todo usage
            return;
        }
        if (Bukkit.getPlayer(strings[0]) == null) {
            COULDNT_FIND.msg(member.getPlayer(), "%type%", "player", "%thing%", strings[0]);
            return;
        }
        Member target = MemberManager.getInstance().getMember(Bukkit.getPlayer(strings[0]));
        new KitMenu(KitPvP.getInstance().getKitManager(), target).open(member.getPlayer());
    }
}
