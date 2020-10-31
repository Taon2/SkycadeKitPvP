package net.skycade.kitpvp;

import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import net.skycade.SkycadeCore.SkycadePlugin;
import net.skycade.SkycadeCore.utility.scoreboard.ScoreboardManager;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.region.DataPoint;
import net.skycade.kitpvp.coreclasses.region.Region;
import net.skycade.kitpvp.events.CaptureTheFlagEvent;
import net.skycade.kitpvp.events.RandomEvent;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.listeners.WorldListeners;
import net.skycade.kitpvp.listeners.chat.ChatClick;
import net.skycade.kitpvp.listeners.player.*;
import net.skycade.kitpvp.managers.RotationManager;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.GangPointsManager;
import net.skycade.kitpvp.stat.KitPvPDB;
import net.skycade.kitpvp.stat.KitPvPStats;
import net.skycade.kitpvp.stat.leaderboards.runnables.Update;
import net.skycade.kitpvp.stat.leaderboards.stats.*;
import net.skycade.kitpvp.ui.eventshopitems.EventShopManager;
import net.skycade.kitpvp.ui.prestige.PrestigeManager;
import net.skycade.skycadeleaderboards.SkycadeLeaderboards;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

public class KitPvP extends SkycadePlugin {

    private final Map<UUID, KitPvPStats> stats = new HashMap<>();

    private static KitPvP instance;
    private KitManager kitManager;
    private PrestigeManager prestigeManager;
    private EventShopManager eventShopManager;
    private RotationManager rotationManager;
    private GangPointsManager gangPointsManager;
    private ChatClick chatClick;

    private Region spawnRegion;
    private Location spawnLocation;
    private int availableKits = 0;

    private void defaults() {
        Map<String, Object> defaults = new TreeMap<>();

        defaults.put("update-delay", 2400);
        defaults.put("database.kitpvp-table", "skycade_kitpvp_members");
        defaults.put("database.kitpvp-prestige-levels", "skycade_kitpvp_prestige_levels");

        defaults.put("start-kits", Arrays.asList(KitType.ARCHER.getAlias(), KitType.CHANCE.getAlias(), KitType.DUBSTEP.getAlias()));
        defaults.put("start-coins", 3500);

        defaults.put("rotation-seconds", 3600);
        defaults.put("kits-rotation-amount", 18);
        defaults.put("kits-rotation-enabled", false);

        defaults.put("soup-price", 100);
        defaults.put("soup-cooldown", 60);
        defaults.put("refreshkit-price", 2000);
        defaults.put("refreshkit-cooldown", 10800);

        defaults.put("display-hit-damage", true);
        defaults.put("kill-coins", 15);
        defaults.put("kill-bonus-percentage", 5);
        defaults.put("coins-modifier", 100d);
        defaults.put("ks-update-time", 30);
        defaults.put("stat-refresh-time", 1800);

        defaults.put("bounties", new YamlConfiguration());

        defaults.put("scoreboard.bottom-link", "&b&lplay.skycade.net");
        defaults.put("scoreboard.name", "SkycadeKitPvP");

        defaults.put("spawn-location", new Location(Bukkit.getWorld("world"), 252.5, 73.0, -45.0, -45, 0));

        defaults.put("spawn-region.point-1", new Location(Bukkit.getWorld("world"), -100, 0, 200));
        defaults.put("spawn-region.point-2", new Location(Bukkit.getWorld("world"), 300, 250, 290));

        defaults.put("teleport-locations.point-1", new Location(Bukkit.getWorld("world"), 252.5, 73.0, -45.0, -45, 0));

        defaults.put("capturetheflag.banner-spawn", new Location(Bukkit.getWorld("world"), 252.5, 73.0, -45.0, -45, 0));
        defaults.put("capturetheflag.red-region.point-1", new Location(Bukkit.getWorld("world"), -100, 0, 200));
        defaults.put("capturetheflag.red-region.point-2", new Location(Bukkit.getWorld("world"), 300, 250, 290));
        defaults.put("capturetheflag.blue-region.point-1", new Location(Bukkit.getWorld("world"), -100, 0, 200));
        defaults.put("capturetheflag.blue-region.point-2", new Location(Bukkit.getWorld("world"), 300, 250, 290));

        setConfigDefaults(defaults);
        loadDefaultConfig();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        defaults();
        instance = this;

        KitPvPDB.getInstance();
        MemberManager.getInstance();

        this.kitManager = new KitManager(this);
        this.prestigeManager = new PrestigeManager(this);
        this.eventShopManager = new EventShopManager(this);
        this.rotationManager = new RotationManager();
        this.gangPointsManager = new GangPointsManager();
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
            if (kitType != null && kitType.getKit() != null && kitType.getKit().isEnabled()) ++i;
        }
        this.availableKits = i;

        net.skycade.SkycadeCore.utility.scoreboard.ScoreboardManager.getInstance().registerNametag(2, (p, v) -> null, (player, viewer) -> {
            Member member = MemberManager.getInstance().getMember(viewer, false);
            if (member == null) return null;
            UUID lastKiller = member.getLastKiller();
            return lastKiller != null && lastKiller.equals(player.getUniqueId()) ? Collections.singletonList(ChatColor.RED) : null;
        }, (p, v) -> null);

        net.skycade.SkycadeCore.utility.scoreboard.ScoreboardManager.getInstance().registerNametag(3, (p, v) -> null, (player, viewer) -> {
            Member member = MemberManager.getInstance().getMember(viewer, false);
            if (member == null) return null;
            Gang gang = GangsPlusApi.getPlayersGang(member.getPlayer());
            return player != null && gang != null && gang.isMember(player) ? Collections.singletonList(ChatColor.GREEN) : null;
        }, (p, v) -> null);

        // Disabled due to Capture the Flag being disabled
        /*net.skycade.SkycadeCore.utility.scoreboard.ScoreboardManager.getInstance().registerNametag(4, (p, v) -> null, (player, viewer) -> {
            Member member = MemberManager.getInstance().getMember(viewer, false);
            CaptureTheFlagEvent captureTheFlagEvent = CaptureTheFlagEvent.getInstance();
            if (member == null || player == null || captureTheFlagEvent.getBegin() == null) return null;
            return captureTheFlagEvent.isTeamRed(player) ? Collections.singletonList(ChatColor.RED) : Collections.singletonList(ChatColor.BLUE);
        }, (p, v) -> null);

         */

        ScoreboardManager.getInstance().registerNametag(4, (p, v) -> null, (p, v) -> null, (p, v) -> {
            // Bounties!
            int bounty = 0;

            int bountyLevel;
            int killStreak = getStats(p).getStreak();
            for (bountyLevel = killStreak; bountyLevel > 0; --bountyLevel) {
                bounty = KitPvP.getInstance().getConfig().getInt("bounties." + bountyLevel, 0);

                if (bounty != 0) break;
            }

            if (bounty == 0) return null;
            DecimalFormat df = new DecimalFormat("###,###,###,###.##");
            return ChatColor.GOLD + " - $" + df.format(bounty);
        });

        RandomEvent.init();
        Messages.init();

        // register stats
        if (Bukkit.getPluginManager().getPlugin("SkycadeLeaderboards") != null) {
            Set<UUID> uuids = KitPvPDB.getInstance().getTopUuids();
            // initializing ones that use UUIDs
            StatKitPvPKills.getInstance().init(uuids);
            StatKitPvPCoins.getInstance().init(uuids);
            StatKitPvPKillStreak.getInstance().init(uuids);
            StatKitPvPDeaths.getInstance().init(uuids);
            StatKitPvPKDR.getInstance().init(uuids);
            // registering via the leaderBoards API
            SkycadeLeaderboards.getAPI().register(this.getName(), StatKitPvPKills.getInstance());
            SkycadeLeaderboards.getAPI().register(this.getName(), StatKitPvPCoins.getInstance());
            SkycadeLeaderboards.getAPI().register(this.getName(), StatKitPvPKDR.getInstance());
            //SkycadeLeaderboards.getAPI().register(this.getName(), StatGangsKills.getInstance()); <-- temp disabling
            // >> register gang points later so the points load first and we don't get a 0 val
            SkycadeLeaderboards.getAPI().register(this.getName(), StatKitPvPKillStreak.getInstance());
            SkycadeLeaderboards.getAPI().register(this.getName(), StatKitPvPDeaths.getInstance());
        }

        // start update runnable for stats cache
        new Update().startTask();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        for (Member member : MemberManager.getInstance().getMembers().values()) {
            KitPvPDB.getInstance().setMemberDataSync(member);
        }

        if (RandomEvent.getCurrent() != null)
            RandomEvent.getCurrent().end();

        rotationManager.update();
        eventShopManager.save();
        gangPointsManager.save();
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
            stats.put(member.getUUID(), new KitPvPStats(member.getUUID()));
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

    public PrestigeManager getPrestigeManager() {
        return prestigeManager;
    }

    public EventShopManager getEventShopManager() {
        return eventShopManager;
    }

    public GangPointsManager getGangPointsManager() {
        return gangPointsManager;
    }

    public RotationManager getKitPvPDocManager() {
        return rotationManager;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public boolean isInSpawnArea(Player p) {
        return spawnRegion.contains(p);
    }

    public int getAvailableKits() {
        return availableKits;
    }


}
