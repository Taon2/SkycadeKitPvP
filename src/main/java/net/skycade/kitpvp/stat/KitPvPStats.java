package net.skycade.kitpvp.stat;

import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.ui.eventshopitems.EventShopItem;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KitPvPStats {

    private Integer lastStreak;

    private UUID uuid;
    private int prestigeLevel = 0;
    private int kills = 0;
    private int deaths = 0;
    private int coins = 0;
    private int eventTokens = 0;
    private int streak = 0;
    private int highestStreak = 0;
    private int assists = 0;
    private KitType activeKit = KitType.CHANCE;
    private KitType kitPreference = KitType.CHANCE;
    private Map<KitType, KitData> kits = new HashMap<>();
    private ArrayList<EventShopItem> upgrades = new ArrayList<>();

    public KitPvPStats(UUID uuid) {
        //Unlocked from the start
        this.uuid = uuid;

        for (String kitType : KitPvP.getInstance().getConfig().getStringList("start-kits")) {
            kits.put(KitType.byAlias(kitType), new KitData(KitType.byAlias(kitType)));
        }
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void giveCoins(int coins) {
        this.coins += coins;

        //Adds the amount of coins to the gang points
        Gang gang = GangsPlusApi.getPlayersGang(Bukkit.getPlayer(uuid));
        if (gang != null)
            KitPvP.getInstance().getGangPointsManager().addPoints(gang.getName(), coins);
    }

    public void takeCoins(int coins) {
        this.coins -= coins;
    }

    public int getEventTokens() {
        return eventTokens;
    }

    public void giveEventTokens(int eventTokens) {
        this.eventTokens += eventTokens;

        //Adds the amount of event tokens times 200 to the gang points
        Gang gang = GangsPlusApi.getPlayersGang(Bukkit.getPlayer(uuid));
        if (gang != null)
            KitPvP.getInstance().getGangPointsManager().addPoints(gang.getName(), eventTokens * 200);
    }

    public void takeEventTokens(int eventTokens) {
        this.eventTokens -= eventTokens;
    }

    public void setEventTokens(int newTokens) {
        this.eventTokens = newTokens;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        if (streak < getStreak())
            lastStreak = getStreak();
        this.streak = streak;
        if (streak > highestStreak) {
            highestStreak = streak;
        }
    }

    public int getHighestStreak() {
        return highestStreak;
    }

    public void setHighestStreak(int highestStreak) {
        this.highestStreak = highestStreak;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public KitType getActiveKit() {
        return activeKit;
    }

    public void setActiveKit(KitType kitType) {
        this.activeKit = kitType;
    }

    KitType getKitPreference() {
        return kitPreference;
    }

    public void setKitPreference(KitType kitType) {
        this.kitPreference = kitType;
    }

    public void applyKitPreference() {
        if (getKitPreference() == null)
            return;
        setActiveKit(getKitPreference());
    }

    public Map<KitType, KitData> getKits() {
        return kits;
    }

    public ArrayList<EventShopItem> getUpgrades() {
        return upgrades;
    }

    public void resetKits() {
        kits.keySet().forEach(kit -> {
            String nodeCommand = "addtempperm " + uuid + " skycade.crates.reward." + kit.name().toLowerCase() + " false";
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), nodeCommand);
        });

        kits.clear();

        //Keep starting kits
        for (String kitType : KitPvP.getInstance().getConfig().getStringList("start-kits")) {
            kits.put(KitType.byAlias(kitType), new KitData(KitType.byAlias(kitType)));
        }

        setKitPreference(KitType.CHANCE);
        applyKitPreference();
    }

    public void removeKit(KitType kit) {
        kits.remove(kit);

        String nodeCommand = "addtempperm " + uuid + " skycade.crates.reward." + kit.name().toLowerCase() + " false";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), nodeCommand);
    }

    public boolean hasKit(KitType kit) {
        return getKits().containsKey(kit);
    }

    public void addKit(KitType kit) {
        if (getKits().containsKey(kit))
            return;
        kits.put(kit, new KitData(kit));

        boolean hasPermission = false;
        // Checks all active permissions to see if the player has the reward node
        if (Bukkit.getPlayer(uuid).hasPermission("skycade.crates.reward." + kit.name().toLowerCase())) {
            Bukkit.getLogger().info("has");
            hasPermission = true;
        }

        // Stops from being applied if already applied
        if (!hasPermission) {
            String nodeCommand = "addtempperm " + uuid + " skycade.crates.reward." + kit.name().toLowerCase() + " true";
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), nodeCommand);
        }
    }

    public Integer getLastStreak() {
        return lastStreak;
    }

    public int getPrestigeLevel() {
        return prestigeLevel;
    }

    public void setPrestigeLevel(int prestigeLevel) {
        this.prestigeLevel = prestigeLevel;
    }
}