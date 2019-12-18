package net.skycade.kitpvp.events.capturetheflag;

import net.skycade.SkycadeCore.utility.CoreUtil;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.events.RandomEvent;
import net.skycade.kitpvp.events.CaptureTheFlagEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static net.skycade.kitpvp.Messages.CAPTURETHEFLAG_STARTING;

public class CaptureTheFlagStartDelay extends BukkitRunnable {

    private final Long start;
    private List<Integer> announced = new ArrayList<>();
    private static int[] announce = new int[]{60, 30, 15, 10, 5, 4, 3, 2, 1};

    public CaptureTheFlagStartDelay(Long start) {
        this.start = start;
        runTaskTimer(KitPvP.getInstance(), 20L, 20L);
    }

    @Override
    public void run() {
        if (System.currentTimeMillis() > start) {
            cancel();

            RandomEvent current = RandomEvent.getCurrent();
            if (!(current instanceof CaptureTheFlagEvent)) return;

            ((CaptureTheFlagEvent) current).start();
        } else {
            int diff = ((Long) ((start - System.currentTimeMillis()) / 1000L)).intValue();

            for (int i : announce) {
                if (diff <= i && !announced.contains(i)) {
                    CAPTURETHEFLAG_STARTING.broadcast("%time%", CoreUtil.niceFormat(i));
                    announced.add(i);
                    break;
                }
            }

        }
    }
}
