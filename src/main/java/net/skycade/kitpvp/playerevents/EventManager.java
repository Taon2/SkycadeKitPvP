package net.skycade.kitpvp.playerevents;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import net.skycade.SkycadeCore.vanish.VanishStatus;
import net.skycade.crates.CratesPlugin;
import net.skycade.crates.crates.Crate;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.Messages;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.kit.kits.*;
import net.skycade.kitpvp.playerevents.events.Brackets;
import net.skycade.kitpvp.playerevents.events.LastManStanding;
import net.skycade.kitpvp.playerevents.events.Sumo;
import net.skycade.kitpvp.playerevents.listeners.EventOutOfBounds;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.security.MessageDigest;
import java.util.ArrayList;
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

    private Sumo sumoEvent;
    private LastManStanding lastManStanding;

    private Brackets brackets;

    public ArrayList<UUID> spectators = new ArrayList<>();


    public EventManager() {
        this.sumoEvent = new Sumo();
        this.lastManStanding = new LastManStanding();
        this.brackets = new Brackets();
        this.currentEvent = EventType.IDLE;
        this.hoster = null;
        this.stopped = false;
        Bukkit.getScheduler().runTaskTimer(KitPvP.getInstance(), this::eventCooldownRunnable, 0L, 20L);
        Bukkit.getScheduler().runTaskTimer(KitPvP.getInstance(), this::eventCountdown, 0L, 20L);
        Bukkit.getScheduler().runTaskTimer(KitPvP.getInstance(), this::eventSpectator, 0L, 20L);

        new EventOutOfBounds();
    }

    public void startEvent(EventType type) {
        if (type == EventType.SUMO) {
            getSumo().start();
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
        switch (type) {
            case SUMO: {
                Messages.SUMO_EVENT_ANNOUNCEMENT.broadcast("%player%", hoster.getName());
                Messages.STARTING_EVENT_TIME_LEFT.broadcast("%second%", String.valueOf(secondsUntilStarting));
                return;
            }
            case BRACKETS: {
                KitType kitType = getBrackets().getChosenKit();
                String kit = null;
                
                if (kitType == KitType.KNIGHT){
                    kit = new KitKnight(KitPvP.getInstance().getKitManager()).getName();
                }
                if (kitType == KitType.ZEUS){
                    kit = new KitZeus(KitPvP.getInstance().getKitManager()).getName();
                }

                if (kitType == KitType.PAINTBALL){
                    kit = new KitPaintball(KitPvP.getInstance().getKitManager()).getName();
                }
                if (kitType == KitType.BUILDUHC){
                    kit = new KitBuildUHC(KitPvP.getInstance().getKitManager()).getName();
                }
                if (kitType == KitType.GANK){
                    kit = new KitGank(KitPvP.getInstance().getKitManager()).getName();
                }
                if (kitType == KitType.TELEPORTER){
                    kit = new KitTeleporter(KitPvP.getInstance().getKitManager()).getName();
                }
                
                Messages.BRACKETS_EVENT_ANNOUNCEMENT.broadcast("%player%", hoster.getName(), "%kit%", kit);
                Messages.STARTING_EVENT_TIME_LEFT.broadcast("%second%", String.valueOf(secondsUntilStarting));
                return;
            }
            case LMS: {
                KitType kitType = getLMS().getChosenKit();
                String kit = null;

                if (kitType == KitType.KNIGHT){
                    kit = new KitKnight(KitPvP.getInstance().getKitManager()).getName();
                }
                if (kitType == KitType.GANK){
                    kit = new KitGank(KitPvP.getInstance().getKitManager()).getName();
                }
                if (kitType == KitType.PAINTBALL){
                    kit = new KitPaintball(KitPvP.getInstance().getKitManager()).getName();
                }
                if (kitType == KitType.BUILDUHC){
                    kit = new KitBuildUHC(KitPvP.getInstance().getKitManager()).getName();
                }
                if (kitType == KitType.ZEUS){
                    kit = new KitZeus(KitPvP.getInstance().getKitManager()).getName();
                }
                if (kitType == KitType.TELEPORTER){
                    kit = new KitTeleporter(KitPvP.getInstance().getKitManager()).getName();
                }

                Messages.LMS_EVENT_ANNOUNCEMENT.broadcast("%kit%", kit,
                        "%player%", hoster.getName());
                Messages.STARTING_EVENT_TIME_LEFT.broadcast("%second%", String.valueOf(secondsUntilStarting));
            }
        }
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

        sendTitle(p, "&a&lEVENT WINNER", "&fYou have won &61,500 Coins &fand &d1 Upgrade Crate Key&f!");
        p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
        new BukkitRunnable(){

            @Override
            public void run() {
                p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);

                new BukkitRunnable(){

                    @Override
                    public void run() {
                        p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
                    }
                }.runTaskLater(KitPvP.getInstance(), 3);
            }
        }.runTaskLater(KitPvP.getInstance(), 3);
    }

    public void sendTitle(Player p, String h, String f){
        p.sendTitle(ChatColor.translateAlternateColorCodes('&', h),
                ChatColor.translateAlternateColorCodes('&', f));
    }

    public void join(Player player){
        switch (currentEvent){
            case SUMO:{
                getSumo().join(player);
                return;
            }
            case BRACKETS:{
                getBrackets().join(player);
                return;
            }
            case LMS:{
                getLMS().join(player);
                return;
            }
        }
    }
    public void quit(Player player){
        switch (currentEvent){
            case SUMO:{
                getSumo().quit(player);
                return;
            }
            case LMS:{
                getLMS().quit(player);
                return;
            }
            case BRACKETS:{
                getBrackets().quit(player);
            }
        }
    }

    public void spectate(Player player){
        Location lobby = null;
        if (getCurrentEvent() == EventType.SUMO){
            lobby = getSumo().getLobbyLocation();
        }
        if (getCurrentEvent() == EventType.LMS){
            lobby = getLMS().getLobbyLocation();
        }
        if (getCurrentEvent() == EventType.BRACKETS){
            lobby = getBrackets().getLobbyLocation();
        }

        spectators.add(player.getUniqueId());

        player.teleport(new Location(lobby.getWorld(), lobby.getX(), lobby.getY(), lobby.getZ(), lobby.getYaw(), lobby.getPitch()));
        player.setGameMode(GameMode.SPECTATOR);
        if (!VanishStatus.isVanished(player.getUniqueId())) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (isSpectating(p) || getSumo().isPlaying(p) || getBrackets().isPlaying(p) || getLMS().isPlaying(p)) {
                    Messages.STARTED_SPECTATING.msg(p, "%player%", player.getName());
                }
            }
        }
    }

    public void eventSpectator(){
        for (Player player : Bukkit.getOnlinePlayers()){
            if (isSpectating(player)){
                ActionBarAPI.sendActionBar(player, ChatColor.translateAlternateColorCodes('&',
                        "&f&lYOU ARE CURRENTLY SPECTATING &b&l" + getCurrentEvent().toString().toUpperCase() +
                                "&f&l, DO &b&l/event leave &f&lTO QUIT"));
            }
        }
    }

    public void removeFromEvent(Player player){
        UtilPlayer.reset(player);
        KitPvPStats stats = KitPvP.getInstance().getStats(player);
        stats.setKitPreference(KitType.DUBSTEP);
        Location spawn = KitPvP.getInstance().getSpawnLocation();
        player.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
        stats.applyKitPreference();
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

    public Sumo getSumo() {
        return sumoEvent;
    }

    public LastManStanding getLMS() {
        return lastManStanding;
    }

    public Brackets getBrackets() {
        return brackets;
    }


    public boolean isSpectating(Player player){
        return spectators.contains(player.getUniqueId());
    }

    public void addSpectator(Player player){
        spectators.add(player.getUniqueId());
    }
    public void removeSpectator(Player player){
        spectators.remove(player.getUniqueId());

        UtilPlayer.reset(player);
        KitPvPStats stats = KitPvP.getInstance().getStats(player);
        stats.setKitPreference(KitType.DUBSTEP);
        Location spawn = KitPvP.getInstance().getSpawnLocation();
        player.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
        stats.applyKitPreference();

        if (!VanishStatus.isVanished(player.getUniqueId())) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (isSpectating(p) || getSumo().isPlaying(p) || getBrackets().isPlaying(p) || getLMS().isPlaying(p)) {
                    Messages.STOPPED_SPECTATING.msg(p, "%player%", player.getName());
                }
            }
        }
    }
}
