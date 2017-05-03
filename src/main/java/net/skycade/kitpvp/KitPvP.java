package net.skycade.kitpvp;

import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.region.DataPoint;
import net.skycade.kitpvp.coreclasses.region.Region;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.listeners.WorldListeners;
import net.skycade.kitpvp.listeners.chat.ChatClick;
import net.skycade.kitpvp.listeners.player.*;
import net.skycade.kitpvp.scoreboard.HighestKsUpdater;
import net.skycade.kitpvp.stat.KitPvPDB;
import net.skycade.kitpvp.scoreboard.KitPvPScoreboard;
import net.skycade.kitpvp.stat.RotationManager;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KitPvP extends JavaPlugin {

    private final Map<UUID, KitPvPStats> stats = new HashMap<>();

    private static KitPvP instance;
    private KitManager kitManager;
    private RotationManager rotationManager;
    private HighestKsUpdater ksUpdater;
    private ChatClick chatClick;

    private Region spawnRegion;

    private Location spawnLocation;

    @Override
    public void onEnable() {
        instance = this;

        KitPvPDB.getInstance();
        MemberManager.getInstance();

        this.kitManager = new KitManager(this);
        this.rotationManager = new RotationManager();
        this.ksUpdater = new HighestKsUpdater(this);
        Bukkit.getPluginManager().registerEvents(chatClick = new ChatClick(), this);
        new KitPvPScoreboard(this);

        //Change the datapoint locations!
        this.spawnRegion = new Region("spawn", new DataPoint(0, 60, 0), new DataPoint(0, 60, 0));

        //TODO: set spawnlocation
        this.spawnLocation = new Location(getWorld(), -198, 72, 236);

       registerListeners();
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(p -> p.teleport(getSpawnpoint()));
        MemberManager.getInstance().getMembers().entrySet().forEach(entry -> {
                Member member = entry.getValue();
                MemberManager.getInstance().getCollection().updateOne(new Document("_id", member.getDocument().get("_id")),
                        new Document("$set", member.getDocument()));

        });
        rotationManager.updateToDB();
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

    public Location getSpawnpoint() {
        return spawnLocation;
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

}
