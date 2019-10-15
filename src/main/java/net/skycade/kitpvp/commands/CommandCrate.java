package net.skycade.kitpvp.commands;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.utils.Recharge;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.runnable.CrateRunnable;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.*;

import static net.skycade.kitpvp.Messages.*;

public class CommandCrate extends Command<KitManager> implements Listener {

    private final Map<UUID, Integer> crateCooldown = new HashMap<>();

    public CommandCrate(KitManager module) {
        super(module, "Randomly unlock a new kit.", new Permission("kitpvp.default", PermissionDefault.TRUE), "crate");
        Bukkit.getPluginManager().registerEvents(this, getModule().getPlugin());
    }

    @Override
    public void execute(Member member, String aliasUsed, String... args) {
        KitPvPStats stats = getModule().getKitPvP().getStats(member);
        if (crateCooldown.containsKey(member.getUUID())) {
            if (!Recharge.recharge(member, "KitPvP Crate", crateCooldown.get(member.getUUID()))) {
                //CANT_USE_YET.msg(member.getPlayer());
                return;
            }
        }
        if (stats.getCrateKeys() <= 0) {
            NOT_ENOUGH.msg(member.getPlayer(), "%thing%", "keys");
            return;
        }
        List<KitType> kits = new ArrayList<>();
        int counter = 1;
        for (KitType kit : KitType.values()) {
            if (kit != KitType.KITMASTER && kit.getKit().isEnabled() && !stats.hasKit(kit)) {
                if (counter++ >= 54)
                    break;
                kits.add(kit);
            }
        }

        if (kits.isEmpty()) {
            ALL_KITS_UNLOCKED.msg(member.getPlayer());
            return;
        }

        stats.setCrateKeys(stats.getCrateKeys() - 1);
        int randomNum = UtilMath.getRandom(0, kits.size() - 1);
        crateCooldown.put(member.getUUID(), randomNum / 3);
        CrateRunnable runnable = new CrateRunnable(randomNum, member.getPlayer(), member, kits, stats);
        runnable.runTaskTimer(getModule().getPlugin(), 0, 7);
    }
}