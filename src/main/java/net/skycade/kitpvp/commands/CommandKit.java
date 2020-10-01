package net.skycade.kitpvp.commands;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.SkycadeCore.utility.command.addons.Permissible;
import net.skycade.SkycadeCore.utility.command.addons.SubCommand;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.events.KillTheKingEvent;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPStats;
import net.skycade.kitpvp.ui.KitMenu;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.command.ColouredConsoleSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;

import static net.skycade.kitpvp.Messages.*;

public class CommandKit extends SkycadeCommand {
    public CommandKit() {
        super("kit", Collections.singletonList("kits"));

        addSubCommands(
                new CommandKit.Unlock(),
                new CommandKit.Lock()
        );
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        Member member = MemberManager.getInstance().getMember((Player) commandSender);

        if (KillTheKingEvent.getInstance() != null && KillTheKingEvent.getInstance().getCurrentKing() != null) {
            if (member.getUUID().equals(KillTheKingEvent.getInstance().getCurrentKing())) {
                CANNOT_USE.msg(member.getPlayer(), "%thing%", "/kit", "%reason%", "as the King");
                return;
            }
        }

        if (strings.length > 0) {
            KitType kit = KitType.getTypeFromString(strings[0].toLowerCase());

            if (kit == null) {
                COULDNT_FIND.msg(commandSender, "%type%", "kit", "%thing%", strings[0]);
                return;
            }

            KitPvPStats stats = KitPvP.getInstance().getStats(member);
            Player p = member.getPlayer();
            KitManager kitManager = KitPvP.getInstance().getKitManager();

            if (!kit.getKit().isEnabled()) {
                KIT_DISABLED.msg(member.getPlayer());
                p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
                return;
            }

            if (!stats.hasKit(kit)) {
                DONT_OWN.msg(member.getPlayer(), "%kit%", "kit " + kit.getKit().getName());
                p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
                return;
            }

            if (stats.getActiveKit() == kit) {
                ALREADY_USING.msg(member.getPlayer(), "%kit%", kit.getKit().getKitType().getKit().getName());
                p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
                return;
            }

            if (kitManager.getKitPvP().getSpawnRegion().contains(member.getPlayer())) {
                KIT_EQUIPPED.msg(member.getPlayer(), "%kit%", kit.getKit().getName());
                UtilPlayer.reset(member.getPlayer());
                stats.getActiveKit().getKit().cancelRunnables(member.getPlayer());
                kitManager.getKitPvP().getStats(member).setActiveKit(kit);
                kitManager.getKitPvP().getStats(member).setKitPreference(kit);
                kit.getKit().beginApplyKit(member.getPlayer());
                kit.getKit().giveSoup(member.getPlayer(), 32);
                p.playSound(p.getLocation(), Sound.LEVEL_UP, 1f, 2f);
                ScoreboardInfo.getInstance().updatePlayer(p);
            } else {
                KIT_EQUIPPED_RESPAWN.msg(member.getPlayer(), "%kit%", kit.getKit().getName());
                kitManager.getKitPvP().getStats(member).setKitPreference(kit);
                p.playSound(p.getLocation(), Sound.LEVEL_UP, 1f, 2f);
            }

            return;
        }

        new KitMenu(KitPvP.getInstance().getKitManager(), member).open(member.getPlayer());
    }

    @SubCommand
    @Permissible("kitpvp.admin")
    private class Unlock extends SkycadeCommand {
        Unlock() {
            super("unlock");
        }

        @Override
        public void onCommand(CommandSender commandSender, String[] strings) {
            Member member;
            if (commandSender instanceof ColouredConsoleSender)
                member = new Member(commandSender.getName());
            else
                member = MemberManager.getInstance().getMember((Player) commandSender);

            if (strings.length < 2) {
                LOCK_UNLOCK_USAGE.msg(commandSender);
                return;
            }
            if (Bukkit.getPlayer(strings[0]) == null) {
                COULDNT_FIND.msg(commandSender, "%type%", "player", "%thing%", strings[0]);
                return;
            }

            Player target = Bukkit.getPlayer(strings[0]);
            KitPvPStats stats = KitPvP.getInstance().getStats(target);

            if (strings[1].equalsIgnoreCase("all")) {
                KitPvP.getInstance().getKitManager().getKits().forEach((kitType, kit) -> {
                    if (kit.isEnabled())
                        stats.addKit(kitType);
                });
                ScoreboardInfo.getInstance().updatePlayer(target);
                KIT_UNLOCKED.msg(commandSender, "%player%", target.getName(), "%kit%", "every kit");
                if (!member.getName().equals(target.getName()))
                    YOUR_KIT_UNLOCKED.msg(target, "%kit%", "Every kit");
                return;
            }

            Kit kit = getKit(strings[1], member);
            if (kit == null)
                return;
            stats.addKit(kit.getKitType());

            KIT_UNLOCKED.msg(commandSender, "%player%", target.getName(), "%kit%", kit.getName());
            YOUR_KIT_UNLOCKED.msg(target, "%kit%", kit.getName());

            ScoreboardInfo.getInstance().updatePlayer(target);
        }
    }

    @SubCommand
    @Permissible("kitpvp.admin")
    private class Lock extends SkycadeCommand {
        Lock() {
            super("lock");
        }

        @Override
        public void onCommand(CommandSender commandSender, String[] strings) {
            Member member;
            if (commandSender instanceof ColouredConsoleSender)
                member = new Member(commandSender.getName());
            else
                member = MemberManager.getInstance().getMember((Player) commandSender);

            if (strings.length < 2) {
                LOCK_UNLOCK_USAGE.msg(commandSender);
                return;
            }
            if (Bukkit.getPlayer(strings[0]) == null) {
                COULDNT_FIND.msg(commandSender, "%type%", "player", "%thing%", strings[0]);
                return;
            }

            Player target = Bukkit.getPlayer(strings[0]);
            KitPvPStats stats = KitPvP.getInstance().getStats(target);

            if (strings[1].equalsIgnoreCase("all")) {
                stats.resetKits();
                KIT_LOCKED.msg(commandSender, "%player%", target.getName(), "%kit%", "every kit");
                return;
            }
            Kit kit = getKit(strings[1], member);
            if (kit == null)
                return;
            stats.removeKit(kit.getKitType());

            if (stats.getActiveKit().equals(kit.getKitType())) {
                stats.setKitPreference(KitType.CHANCE);
                stats.applyKitPreference();
            }

            KIT_LOCKED.msg(commandSender, "%player%", target.getName(), "%kit%", kit.getName());
            YOUR_KIT_LOCKED.msg(target, "%kit%", kit.getName());

            ScoreboardInfo.getInstance().updatePlayer(target);
        }
    }

    private Kit getKit(String name, Member member) {
        Kit kit = null;
        for (Map.Entry<KitType, Kit> entry : KitPvP.getInstance().getKitManager().getKits().entrySet())
            if (entry.getKey().name().equalsIgnoreCase(name))
                kit = entry.getValue();
        if (kit == null) {
            COULDNT_FIND.msg(member.getPlayer(), "%type%", "kit", "%thing%", name);
            return null;
        }
        return kit;
    }
}
