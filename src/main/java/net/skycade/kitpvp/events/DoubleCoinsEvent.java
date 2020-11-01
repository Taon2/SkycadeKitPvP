package net.skycade.kitpvp.events;

import net.skycade.kitpvp.KitPvP;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static net.skycade.kitpvp.Messages.DOUBLECREDITS_ENDED;
import static net.skycade.kitpvp.Messages.DOUBLECREDITS_START;

public class DoubleCoinsEvent extends RandomEvent {

    @Override
    public int getFrequencyPerDay() {
        return 2;
    }

    private Long begin;

    @Override
    public void run() {
        begin = System.currentTimeMillis();

        DOUBLECREDITS_START.broadcast();
        for (Player pl : Bukkit.getOnlinePlayers()) {
            pl.playSound(pl.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - begin > 30 * 60 * 1000L) {
                    end();
                    begin = null;
                    DOUBLECREDITS_ENDED.broadcast();
                    cancel();
                }
            }
        }.runTaskTimer(KitPvP.getInstance(), 20L, 20L);
    }

    @Override
    public String getName() {
        return "doublecoins";
    }

    public static boolean isActive() {
        return RandomEvent.getCurrent() instanceof DoubleCoinsEvent;
    }
}
