package net.skycade.kitpvp.listeners;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.Map;

public class SignListeners implements Listener {

    private final KitManager manager;

    public SignListeners(KitManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        Player p = e.getPlayer();
        if (!(p.isOp() || MemberManager.getInstance().getMember(p).getPlayer().hasPermission(new Permission("kitpvp.admin", PermissionDefault.OP))))
            return;

        if (e.getLine(0).equalsIgnoreCase("[Unlock]")) {
            String signKit = null;
            for (Map.Entry<KitType, Kit> entry : manager.getKits().entrySet())
                if (e.getLine(1).equalsIgnoreCase(entry.getValue().getName())) {
                    signKit = entry.getValue().getName();
                    break;
                }
            if (signKit == null)
                return;
            e.setLine(0, "§4[Unlock]");
            e.setLine(1, "§c" + signKit);

        } else if (e.getLine(0).equalsIgnoreCase("[Refresh]") || e.getLine(0).equalsIgnoreCase("[Refresh kit]")) {
            e.setLine(0, "§4[Refresh kit]");
            e.setLine(1, "§c-[Free]-");
        }

    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (!(e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.WALL_SIGN))
            return;
        Member member = MemberManager.getInstance().getMember(e.getPlayer());
        KitPvPStats stats = manager.getKitPvP().getStats(member);
        Sign s = (Sign) e.getClickedBlock().getState();

        if (s.getLine(0).equalsIgnoreCase("§4[Unlock]")) {
            if (member.getPlayer().getGameMode() != GameMode.SURVIVAL) {
                member.message("You're not in the right gamemode.");
                return;
            }
            if (stats.getActiveKit() != KitType.DEFAULT) {
                member.message("You can only claim a reward when using the default kit.");
                return;
            }
            KitType kit = null;
            for (Map.Entry<KitType, Kit> entry : manager.getKits().entrySet()) {
                if (s.getLine(1).equalsIgnoreCase("§c" + entry.getValue().getName())) {
                    kit = entry.getKey();
                    break;
                }
            }
            if (kit == null)
                return;
            if (stats.hasKit(kit)) {
                member.message("§7This kit is already §aunlocked§7.");
                return;
            }
            member.message("§7You unlocked the §a" + s.getLine(1) + " §7kit.");
            stats.addKit(kit);

            if (stats.getKits().size() >= KitPvP.getInstance().getAvailableKits() - 1) {
                member.message("§7You unlocked §aall §7the kits! You unlocked the §aKitMaster §7kit.");
                stats.addKit(KitType.KITMASTER);
            }
            ScoreboardInfo.getInstance().updatePlayer(member.getPlayer());
        } else if (s.getLine(0).equalsIgnoreCase("§4[Refresh kit]")) {
            if (manager.getSignMap().containsKey(member.getUUID())) {
                member.message("You §acan't §7use the sign yet.");
                return;
            }
            manager.getSignMap().put(member.getUUID(), KitPvP.getInstance().getConfig().getInt("sign-refresh-cooldown"));
            UtilPlayer.reset(member.getPlayer());
            stats.getActiveKit().getKit().applyKit(member.getPlayer());
            stats.getActiveKit().getKit().giveSoup(member.getPlayer(), 32);
            member.message("Your kit has been §arefreshed§7!");
            ScoreboardInfo.getInstance().updatePlayer(member.getPlayer());
        }


    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        manager.getSignMap().remove(e.getPlayer().getUniqueId());
    }

}
