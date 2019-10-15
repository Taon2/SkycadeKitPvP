package net.skycade.kitpvp.events.teamfight;

import net.skycade.SkycadeCore.utility.CoreUtil;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.events.RandomEvent;
import net.skycade.kitpvp.events.TeamFightEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static net.skycade.kitpvp.Messages.TEAMFIGHT_STARTING;

public class TeamFightStartDelay extends BukkitRunnable {

    private final Long start;
    private List<Integer> announced = new ArrayList<>();
    private static int[] announce = new int[]{60, 30, 15, 10, 5, 4, 3, 2, 1};

    public TeamFightStartDelay(Long start, int i) {
        this.start = start;
        runTaskTimer(KitPvP.getInstance(), 20L, 20L);

//        STARTING.broadcast("%time%", CoreUtil.niceFormat(i));
    }

    @Override
    public void run() {
        if (System.currentTimeMillis() > start) {
            cancel();

            RandomEvent current = RandomEvent.getCurrent();
            if (!(current instanceof TeamFightEvent)) return;

            ((TeamFightEvent) current).start();
        } else {
            int diff = ((Long) ((start - System.currentTimeMillis()) / 1000L)).intValue();

            for (int i : announce) {
                if (diff <= i && !announced.contains(i)) {
                    TEAMFIGHT_STARTING.broadcast("%time%", CoreUtil.niceFormat(i));
                    announced.add(i);
                    break;
                }
            }

        }
    }
}
