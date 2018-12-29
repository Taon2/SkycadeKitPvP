package net.skycade.kitpvp.events;

import net.skycade.kitpvp.KitPvP;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class RandomEvent {

    public static void init() {
        new DoubleCoinsEvent();
        new TagEvent();
        new KillTheKingEvent();

        new Runnable();
    }

    public abstract int getFrequencyPerDay();

    public abstract void run();

    public void end() {
        if (current == this) current = null;
    }

    private static List<RandomEvent> events = new ArrayList<>();

    public RandomEvent() {
        events.add(this);
    }

    private static RandomEvent current = null;

    public static RandomEvent getCurrent() {
        return current;
    }

    private static class Runnable extends BukkitRunnable {

        private Queue<RandomSchedule> queue;

        public Runnable() {
            queue = new PriorityQueue<>(Comparator.comparingLong(RandomSchedule::getTimeToRun));

            long now = System.currentTimeMillis();
            for (RandomEvent event : events) {
                long span = (86400 * 1000L) / (event.getFrequencyPerDay() + 1);

                long last = 0L;
                for (int j = 0; j < event.getFrequencyPerDay(); ++j) {
                    long ts = ThreadLocalRandom.current().nextLong(0, span);
                    queue.add(new RandomSchedule(event, now + last + ts));
                    last += span;
                }

            }

            runTaskTimer(KitPvP.getInstance(), 100L, 100L);
        }

        @Override
        public void run() {
            if (current != null) return;

            RandomSchedule peek = queue.peek();
            if (peek == null) {
                cancel();
                return;
            }

            if (peek.getTimeToRun() < System.currentTimeMillis()) {
                queue.remove();

                RandomEvent event = peek.getEvent();
                current = event;
                event.run();
            }
        }
    }
}
