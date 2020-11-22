package net.skycade.kitpvp.playerevents;

import net.skycade.crates.CratesPlugin;
import net.skycade.crates.crates.Crate;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.Messages;
import net.skycade.kitpvp.playerevents.events.Brackets;
import net.skycade.kitpvp.playerevents.events.LastManStanding;
import net.skycade.kitpvp.playerevents.events.SumoEvent;
import net.skycade.kitpvp.stat.KitPvPStats;
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

    public boolean stopped;
    public long countdown;

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
        this.stopped = false;
        Bukkit.getScheduler().runTaskTimer(KitPvP.getInstance(), this::eventCooldownRunnable, 0L, 20L);
        Bukkit.getScheduler().runTaskTimer(KitPvP.getInstance(), this::eventCountdown, 0L, 20L);

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
        setStopped(false);
        setCountdown(System.currentTimeMillis() + (60 * 1000L));
        sendAnnouncement(hoster, getCurrentEvent(), 60);
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

    public void eventCountdown(){
        if (isStopped())return;
        if (getCountdown() == 0) return;

        long currentTime = System.currentTimeMillis();

        if (currentTime > getCountdown()) {
            setCountdown(0);
        }

        long newTime = getCountdown() - currentTime;
        int sec = (int) (newTime / 1000);
        if (sec == 50 || sec == 40 || sec == 30 || sec == 20 || sec == 10 || sec == 5){
            sendAnnouncement(Bukkit.getPlayer(getHoster()), getCurrentEvent(), sec);
        }
         if (sec == 1){
             startEvent(getCurrentEvent());
        }
    }

    public void rewardPlayer(Player p){
        KitPvPStats stats = KitPvP.getInstance().getStats(p);
        stats.giveCoins(1500);
        Crate crate = CratesPlugin.getInstance().getEditorModule().getCrate("upgradecrate");
        crate.getKey().give(p.getUniqueId(), 1);
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You have won the following rewards for winning an Event:"));
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f* &61,500 Coins"));
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f* &d1 Upgrade Crate Key"));
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

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
    public long getCountdown() {
        return countdown;
    }

    public void setCountdown(long countdown) {
        this.countdown = countdown;
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
