package net.skycade.kitpvp.playerevents;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.Messages;
import net.skycade.kitpvp.playerevents.events.Brackets;
import net.skycade.kitpvp.playerevents.events.LastManStanding;
import net.skycade.kitpvp.playerevents.events.SumoEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class EventManager {
    public long globalCooldown;

    private String announcement = Messages.STARTING_EVENT.getMessage();
    private String startingIn = Messages.STARTING_EVENT_TIME_LEFT.getMessage();

    public EventType currentEvent;

    public boolean cooldownOn;
    public boolean joinable;

    public UUID hoster;

    private SumoEvent sumoEvent;
    private LastManStanding lastManStanding;

    private Brackets brackets;


    public EventManager() {
        this.sumoEvent = new SumoEvent();
        this.lastManStanding = new LastManStanding();
        this.brackets = new Brackets();
        this.currentEvent = EventType.IDLE;
        this.hoster = null;
        Bukkit.getScheduler().runTaskTimer(KitPvP.getInstance(), this::eventCooldownRunnable, 0L, 20L);

    }

    public void startEvent(EventType type) {
        if (type == EventType.SUMO) {
            getSumoEvent().start();
            setJoinable(false);
            return;
        }
        if (type == EventType.LMS) {
            getLMS().start();
            setJoinable(false);
            return;
        }
        if (type == EventType.BRACKETS) {
            getBrackets().start();
            setJoinable(false);
        }
    }

    public void announceEvent(Player hoster, EventType type) {
        setCurrentEvent(type);
        setJoinable(true);
        sendAnnouncement(hoster, type, 60);

        new BukkitRunnable() {
            @Override
            public void run() {
                sendAnnouncement(hoster, type, 50);
            }
        }.runTaskLater(KitPvP.getInstance(), 20 * 10); // This will announce 50 seconds before the event starts

        new BukkitRunnable() {

            @Override
            public void run() {
                sendAnnouncement(hoster, type, 40);
            }
        }.runTaskLater(KitPvP.getInstance(), 20 * 20); // This will announce 40 seconds before the event starts

        new BukkitRunnable() {

            @Override
            public void run() {
                sendAnnouncement(hoster, type, 30);
            }
        }.runTaskLater(KitPvP.getInstance(), 20 * 30); // This will announce 30 seconds before the event starts

        new BukkitRunnable() {

            @Override
            public void run() {
                sendAnnouncement(hoster, type, 20);
            }
        }.runTaskLater(KitPvP.getInstance(), 20 * 40); // This will announce 20 seconds before the event starts

        new BukkitRunnable() {

            @Override
            public void run() {
                sendAnnouncement(hoster, type, 10);
            }
        }.runTaskLater(KitPvP.getInstance(), 20 * 50); // This will announce 10 seconds before the event starts

        new BukkitRunnable() {

            @Override
            public void run() {
                sendAnnouncement(hoster, type, 5);
            }
        }.runTaskLater(KitPvP.getInstance(), 20 * 55); // This will announce 5 seconds before the event starts
        new BukkitRunnable() {

            @Override
            public void run() {

                startEvent(currentEvent);

            }
        }.runTaskLater(KitPvP.getInstance(), 20 * 60); // this will start the event
    }

    private void sendAnnouncement(Player hoster, EventType type, int secondsUntilStarting) {
        String message = announcement;
        String starting = startingIn;

        switch (type) {
            case SUMO: {
                message = message.replaceAll("%event%", "Sumo");
            }
            case BRACKETS: {
                message = message.replaceAll("%event%", "Brackets");
            }
            case LMS: {
                message = message.replaceAll("%event%", "Last Man Standing");
            }
        }
        message = message.replaceAll("%player%", hoster.getName());

        starting = starting.replaceAll("%second%", String.valueOf(secondsUntilStarting));

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', starting));
    }

    public void eventCooldownRunnable() {
        if (globalCooldown == 0) return;
        long currentTime = System.currentTimeMillis();

        if (currentTime > getGlobalCooldown()) {
            setGlobalCooldown(0);
            setCooldownOn(false);
        }
    }

    public long getGlobalCooldown() {
        return globalCooldown;
    }

    public void setGlobalCooldown(long globalCooldown) {
        this.globalCooldown = globalCooldown;
    }

    public boolean isCooldownOn() {
        return cooldownOn;
    }

    public void setCooldownOn(boolean cooldownOn) {
        this.cooldownOn = cooldownOn;
    }

    public UUID getHoster() {
        return hoster;
    }

    public void setHoster(UUID hoster) {
        this.hoster = hoster;
    }

    public EventType getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(EventType type) {
        this.currentEvent = type;
    }

    public boolean isJoinable() {
        return joinable;
    }

    public void setJoinable(boolean joinable) {
        this.joinable = joinable;
    }

    public SumoEvent getSumoEvent() {
        return sumoEvent;
    }

    public LastManStanding getLMS() {
        return lastManStanding;
    }

    public Brackets getBrackets() {
        return brackets;
    }
}
