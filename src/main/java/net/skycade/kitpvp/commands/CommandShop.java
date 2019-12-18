package net.skycade.kitpvp.commands;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.ui.ShopMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandShop extends SkycadeCommand {
    public CommandShop() {
        super("shop");
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        Member member = MemberManager.getInstance().getMember((Player) commandSender);

        new ShopMenu(KitPvP.getInstance().getKitManager(), member).open(member.getPlayer());
    }
}
