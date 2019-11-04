package net.skycade.kitpvp;

import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import net.skycade.SkycadeCore.SkycadePlugin;
import net.skycade.SkycadeCore.utility.scoreboard.ScoreboardManager;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.region.DataPoint;
import net.skycade.kitpvp.coreclasses.region.Region;
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
import net.skycade.kitpvp.ui.prestige.PrestigeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class KitPvP extends SkycadePlugin {

    private final Map<UUID, KitPvPStats> stats = new HashMap<>();

    private static KitPvP instance;
    private KitManager kitManager;
    private PrestigeManager prestigeManager;
    private EventShopManager eventShopManager;
    private RotationManager rotationManager;
    private HighestKsUpdater ksUpdater;
    private ChatClick chatClick;

    private Region spawnRegion;
    private Location spawnLocation;
    private int availableKits = 0;

    private void defaults() {
        Map<String, Object> defaults = new TreeMap<>();

        defaults.put("database.kitpvp-table", "skycade_kitpvp_members");
        defaults.put("database.kitpvp-prestige-levels", "skycade_kitpvp_prestige_levels");
        defaults.put("start-coins", 3500);
        defaults.put("start-kits", Arrays.asList(KitType.ARCHER.getAlias(), KitType.CHANCE.getAlias()));
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
        defaults.put("soup-price", 100);
        defaults.put("soup-cooldown", 60);
        defaults.put("refreshkit-price", 2000);
        defaults.put("refreshkit-cooldown", 10800);
        defaults.put("kits-rotation-enabled", "false");
        defaults.put("event-shop-enabled", "true");
        defaults.put("bounties", new YamlConfiguration());

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

        net.skycade.SkycadeCore.utility.scoreboard.ScoreboardManager.getInstance().registerNametag(3, (p, v) -> null, (player, viewer) -> {
            Member member = MemberManager.getInstance().getMember(viewer, false);
            if (member == null) return null;
            Gang gang = GangsPlusApi.getPlayersGang(member.getPlayer());
            return player != null && gang.isMember(player) ? Collections.singletonList(ChatColor.GREEN) : null;
        }, (p, v) -> null);

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

            return ChatColor.GOLD + " - $" + bounty;
        });

        RandomEvent.init();
        Messages.init();
    }

    @Override
    public void onDisable() {
        super.onDisable();
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
            stats.put(member.getUUID(), new KitPvPStats());
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

    public RotationManager getKitPvPDocManager() {
        return rotationManager;
    }

    public HighestKsUpdater getKsUpdater() {
        return ksUpdater;
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
