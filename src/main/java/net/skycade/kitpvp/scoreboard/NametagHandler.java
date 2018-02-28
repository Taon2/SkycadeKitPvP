package net.skycade.kitpvp.scoreboard;

import net.skycade.SkycadeCore.displays.NametagType;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NametagHandler extends NametagType {
    public NametagHandler() {
        super(4);
    }

    @Override
    public String getPrefix(Player player, Player viewer) {
        return null;
    }

    @Override
    public String getSuffix(Player player, Player viewer) {
        return null;
    }

    @Override
    public ChatColor[] getColor(Player player, Player viewer) {
        Member member = MemberManager.getInstance().getMember(viewer, false);
        if (member == null) return null;
        UUID lastKiller = member.getLastKiller();
        return lastKiller != null && lastKiller.equals(player.getUniqueId()) ? new ChatColor[]{ChatColor.RED} : null;
    }

    @Override
    public boolean isVisible(Player player) {
        return player.hasPermission("kitpvp.nametag.view");
    }
}
