package net.skycade.kitpvp;

import net.skycade.SkycadeCore.SkycadePlugin;
import net.skycade.SkycadeCore.utility.scoreboard.ScoreboardManager;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.region.DataPoint;
import net.skycade.kitpvp.coreclasses.region.Region;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.events.RandomEvent;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.listeners.WorldListeners;
import net.skycade.kitpvp.listeners.chat.ChatClick;
import net.skycade.kitpvp.listeners.player.*;
import net.skycade.kitpvp.scoreboard.HighestKsUpdater;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPDB;
import net.skycade.kitpvp.stat.KitPvPStats;
import net.skycade.kitpvp.stat.RotationManager;
import net.skycade.kitpvp.ui.eventshopitems.EventShopManager;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class KitPvP extends SkycadePlugin {

    private final Map<UUID, KitPvPStats> stats = new HashMap<>();

    private static KitPvP instance;
    private KitManager kitManager;
    private EventShopManager eventShopManager;
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
        defaults.put("kill-coins", 15);
        defaults.put("coins-modifier", 100d);
        defaults.put("chest-cooldown", 30);
        defaults.put("sign-refresh-cooldown", 120);
        defaults.put("stat-refresh-time", 1800);
        defaults.put("scoreboard.name", "SkycadeKitPvP");
        defaults.put("spawn-region.point-1", new Location(Bukkit.getWorld("world"), -100, 0, 200));
        defaults.put("spawn-region.point-2", new Location(Bukkit.getWorld("world"), 300, 250, 290));
        defaults.put("spawn-location", new Location(Bukkit.getWorld("world"), 252.5, 73.0, -45.0, -45, 0));
        defaults.put("scoreboard.bottom-link", "play.skycade.net");
        defaults.put("kill-bonus-percentage", 5);
        defaults.put("soup-price", 300);
        defaults.put("soup-cooldown", 60);
        defaults.put("refreshkit-price", 2500);
        defaults.put("refreshkit-cooldown", 86400);
        defaults.put("kits-rotation-enabled", "false");
        defaults.put("event-shop-enabled", "true");
        //looks in RotationManager for rotation settings in rotation.yml

        defaults.put("bounties", new YamlConfiguration());

        defaults.put("database.kitpvp-table", "skycade_KitPvPMembers");

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
        this.eventShopManager = new EventShopManager(this);
        this.rotationManager = new RotationManager();
        this.ksUpdater = new HighestKsUpdater(this);
        Bukkit.getPluginManager().registerEvents(chatClick = new ChatClick(), this);

        ScoreboardInfo.getInstance();

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

        net.skycade.SkycadeCore.utility.scoreboard.ScoreboardManager.getInstance().registerNametag(2, (p, v) -> null, (player, viewer) -> {
            Member member = MemberManager.getInstance().getMember(viewer, false);
            if (member == null) return null;
            UUID lastKiller = member.getLastKiller();
            return lastKiller != null && lastKiller.equals(player.getUniqueId()) ? Collections.singletonList(ChatColor.RED) : null;
        }, (p, v) -> null);

        ScoreboardManager.getInstance().registerNametag(3, (p, v) -> null, (p, v) -> null, (p, v) -> {
            // Bounties!
            int bounty = 0;

            int bountyLevel;
            int killStreak = getStats(p).getStreak();
            for (bountyLevel = killStreak; bountyLevel > 0; --bountyLevel) {
                bounty = KitPvP.getInstance().getConfig().getInt("bounties." + bountyLevel, 0);

                if (bounty != 0) break;
            }

            if (bounty == 0) return null;

            return ChatColor.GOLD + " - $" + bounty;
        });

        RandomEvent.init();
        Messages.init();
    }

    @Override
    public void onDisable() {
        for (Member member : MemberManager.getInstance().getMembers().values()) {
            KitPvPDB.getInstance().setMemberDataSync(member);
        }

        rotationManager.update();
        eventShopManager.save();
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
        if (member == null) return null;
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

    public EventShopManager getEventShopManager() {
        return eventShopManager;
    }

    public RotationManager getKitPvPDocManager() {
        return rotationManager;
    }

    public HighestKsUpdater getKsUpdater() {
        return ksUpdater;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    // Not the best place for this method..
    public void respawn(Player p) {
        Bukkit.getScheduler().runTaskLater(this, () -> UtilPlayer.reset(p), 1);
        p.setHealth(p.getMaxHealth());
        p.setVelocity(new Vector(0, 0, 0));
        p.setGameMode(GameMode.SURVIVAL);
        p.setGameMode(GameMode.SURVIVAL);
        p.teleport(KitPvP.getInstance().getSpawnLocation());
        Bukkit.getScheduler().runTaskLater(this, p::updateInventory, 10);
        Bukkit.getScheduler().runTaskLater(this, () -> p.setVelocity(new org.bukkit.util.Vector(0, 0, 0)), 5);
        KitPvPStats stats = getStats(p);
        Bukkit.getScheduler().runTaskLater(this, () -> {
            stats.getActiveKit().getKit().giveSoup(p, 32);
        }, 5);
        stats.applyKitPreference();
        Bukkit.getScheduler().runTaskLater(this, () -> {
            stats.getActiveKit().getKit().applyKit(p);
            eventShopManager.reapplyUpgrades(p);
        }, 3);
    }

    public boolean isInSpawnArea(Player p) {
        return spawnRegion.contains(p);
    }

    public int getAvailableKits() {
        return availableKits;
    }
}
