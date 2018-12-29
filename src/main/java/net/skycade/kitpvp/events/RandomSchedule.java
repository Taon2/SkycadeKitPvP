package net.skycade.kitpvp.events;

public class    RandomSchedule {
    private RandomEvent event;
    private Long timeToRun;

    public RandomSchedule(RandomEvent event, Long timeToRun) {
        this.event = event;
        this.timeToRun = timeToRun;
    }

    public RandomEvent getEvent() {
        return event;
    }

    public Long getTimeToRun() {
        return timeToRun;
    }
}
