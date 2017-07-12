package net.skycade.kitpvp;

import net.skycade.SkycadeCore.SkycadePlugin;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.region.DataPoint;
import net.skycade.kitpvp.coreclasses.region.Region;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.listeners.WorldListeners;
import net.skycade.kitpvp.listeners.chat.ChatClick;
import net.skycade.kitpvp.listeners.player.*;
import net.skycade.kitpvp.scoreboard.HighestKsUpdater;
import net.skycade.kitpvp.scoreboard.KitPvPScoreboard;
import net.skycade.kitpvp.stat.KitPvPDB;
import net.skycade.kitpvp.stat.KitPvPStats;
import net.skycade.kitpvp.stat.RotationManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class KitPvP extends SkycadePlugin {

    private final Map<UUID, KitPvPStats> stats = new HashMap<>();

    private static KitPvP instance;
    private KitManager kitManager;
    private RotationManager rotationManager;
    private HighestKsUpdater ksUpdater;
    private ChatClick chatClick;

    private Region spawnRegion;
    private Location spawnLocation;
    private int availableKits = 0;

    private void defaults() {
        Map<String, Object> defaults = new TreeMap<>();

        defaults.put("start-coins", 3500);
        defaults.put("start-kits", Arrays.asList(KitType.ARCHER.getAlias(), KitType.CHANCE.getAlias()));
        defaults.put("start-keys", 1);
        defaults.put("rotation-seconds", 3600);
        defaults.put("kits-rotation-amount", 18);
        defaults.put("required-xp-multiplier", 1);
        defaults.put("display-hit-damage", true);
        defaults.put("ks-update-time", 30);
        defaults.put("kill-credits", 15);
        defaults.put("credits-modifier", 100d);
        defaults.put("chest-cooldown", 30);
        defaults.put("sign-refresh-cooldown", 120);
        defaults.put("stat-refresh-time", 1800);
        defaults.put("scoreboard.name", "SkycadeKitPvP");
        defaults.put("spawn-region.point-1", new Location(Bukkit.getWorld("world"),-100, 0, 200));
        defaults.put("spawn-region.point-2", new Location(Bukkit.getWorld("world"), 300, 250, 290));
        defaults.put("spawn-location", new Location(Bukkit.getWorld("world"), 252.5, 73.0, -45.0, -45, 0));
        defaults.put("scoreboard.bottom-link", "play.skycade.net");
        defaults.put("kill-bonus-percentage", 5);


        defaults.put("database.host", "localhost");
        defaults.put("database.port", 3306);
        defaults.put("database.name", "skycade");
        defaults.put("database.username", "skycade");
        defaults.put("database.password", "h1ghl1s3cur3pa55");
        defaults.put("database.kitpvp-table", "skycade_KitPvPMembers");
        defaults.put("database.previous-names-table", "skycade_PreviousNames");
        defaults.put("database.properties-table", "skycade_KitPvPProperties");

        setConfigDefaults(defaults);
        loadDefaultConfig();
    }

    @Override
    public void onEnable() {
        defaults();
        instance = this;

        KitPvPDB.getInstance();
        MemberManager.getInstance();

        this.kitManager = new KitManager(this);
        this.rotationManager = new RotationManager();
        this.ksUpdater = new HighestKsUpdater(this);
        Bukkit.getPluginManager().registerEvents(chatClick = new ChatClick(), this);
        new KitPvPScoreboard(this);

        //Change the datapoint locations!
        Location location1 = (Location) getConfig().get("spawn-region.point-1");
        Location location2 = (Location) getConfig().get("spawn-region.point-2");
        this.spawnRegion = new Region("spawn", new DataPoint(location1.getBlockX(), location1.getBlockY(), location1.getBlockZ()), new DataPoint(location2.getBlockX(), location2.getBlockY(), location2.getBlockZ()));
        this.spawnLocation = (Location) getConfig().get("spawn-location");
        registerListeners();

        int i = 0;
        for (KitType kitType : KitType.values()) {
            if (kitType.getKit().isEnabled()) ++i;
        }
        this.availableKits = i;
    }

    @Override
    public void onDisable() {
        //Bukkit.getOnlinePlayers().forEach(p -> p.teleport(getSpawnpoint()));
        for (Map.Entry<UUID, Member> entry : MemberManager.getInstance().getMembers().entrySet()) {
            Member member = entry.getValue();
            KitPvPDB.getInstance().setMemberDataSync(entry.getKey(), member.getName(), member.getPreviousNames(), member.getKills(), member.getHighestStreak(), member.getDeaths(), member.getProperties());
        }
        KitPvPDB.getInstance().closeConnection();
        rotationManager.update();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerDamageListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinQuitListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListeners(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        Bukkit.getPluginManager().registerEvents(new WorldListeners(this), this);
    }

    public KitPvPStats getStats(Player p) {
        return getStats(MemberManager.getInstance().getMember(p));
    }

    public KitPvPStats getStats(Member member) {
        if (!stats.containsKey(member.getUUID()))
            stats.put(member.getUUID(), new KitPvPStats(member));
        return stats.get(member.getUUID());
    }

    public Map<UUID, KitPvPStats> getStats() {
        return stats;
    }

    public Region getSpawnRegion() {
        return spawnRegion;
    }

    public ChatClick getChatClick() {
        return chatClick;
    }

    public World getWorld() {
        return Bukkit.getWorlds().get(0);
    }

    public static KitPvP getInstance() {
        return instance;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public RotationManager getKitPvPDocManager() {
        return rotationManager;
    }

    public HighestKsUpdater getKsUpdater() {
        return ksUpdater;
    }

    // Not the best place for this method..
    public void respawn(Player p) {
        Bukkit.getScheduler().runTaskLater(this, () -> UtilPlayer.reset(p), 1);
        p.setHealth(p.getMaxHealth());
        p.setVelocity(new Vector(0, 0, 0));
        p.setGameMode(GameMode.SURVIVAL);
        p.teleport(spawnLocation);
        Bukkit.getScheduler().runTaskLater(this, p::updateInventory, 10);
        Bukkit.getScheduler().runTaskLater(this, () -> p.setVelocity(new org.bukkit.util.Vector(0, 0, 0)), 5);
        KitPvPStats stats = getStats(p);
        Bukkit.getScheduler().runTaskLater(this, () -> stats.getActiveKit().getKit().giveSoup(p, 32), 5);
        stats.applyKitPreference();
        Bukkit.getScheduler().runTaskLater(this, () -> stats.getActiveKit().getKit().applyKit(p), 3);
    }

    public boolean isInSpawnArea(Player p) {
        return spawnRegion.contains(p);
    }

    public int getAvailableKits() {
        return availableKits;
    }
}
