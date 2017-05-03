package net.skycade.kitpvp.coreclasses.member.listeners;


import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class MemberListeners implements Listener {

    public MemberListeners(MemberManager memberManager) {

        Bukkit.getScheduler().runTaskTimer(KitPvP.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (MemberManager.getInstance().getMember(player) == null) {
                    player.kickPlayer("§cSorry, your data was not loaded correctly! Please re-join!");
                }
            }
        }, 20, 20);

    }

}
