package net.skycade.kitpvp.events;

import net.skycade.kitpvp.KitPvP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DoubleCoinsEvent extends RandomEvent {

    @Override
    public int getFrequencyPerDay() {
        return 2;
    }

    private Long begin;

    @Override
    public void run() {
        begin = System.currentTimeMillis();

        Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "DOUBLE CREDITS! " + ChatColor.GREEN + "For the next 30 minutes, everybody earns double the amount of coins!");
        for(Player pl: Bukkit.getOnlinePlayers()){
            pl.playSound(pl.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - begin > 30 * 60 * 1000L) {
                    end();
                    begin = null;
                    Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "DOUBLE CREDITS " + ChatColor.GREEN + "is over!");
                    cancel();
                }
            }
        }.runTaskTimer(KitPvP.getInstance(), 20L, 20L);
    }

    public static boolean isActive() {
        return RandomEvent.getCurrent() instanceof DoubleCoinsEvent;
    }
}
