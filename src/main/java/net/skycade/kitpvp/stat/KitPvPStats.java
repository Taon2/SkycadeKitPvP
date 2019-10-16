package net.skycade.kitpvp.stat;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.ui.eventshopitems.EventShopItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KitPvPStats {

    private Integer lastStreak;

    private int kills = 0;
    private int deaths = 0;
    private int coins = 0;
    private int eventTokens = 0;
    private int streak = 0;
    private int highestStreak = 0;
    private int assists = 0;
    private KitType activeKit = KitType.ARCHER;
    private KitType kitPreference = KitType.ARCHER;
    private Map<KitType, KitData> kits = new HashMap<>();
    private ArrayList<EventShopItem> upgrades = new ArrayList<>();


    public KitPvPStats() {
        //Unlocked from the start:
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

    public int getEventTokens() {
        return eventTokens;
    }

    public void setEventCoins(int eventCoins) {
        this.eventTokens = eventCoins;
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
        KitType kit1 = KitType.ARCHER;
        KitType kit2 = KitType.CHANCE;
        Map<KitType, KitData> map = new HashMap<>();
        map.put(kit1, new KitData(kit1));
        map.put(kit2, new KitData(kit2));
        kits = map;
    }

    public void removeKit(KitType type) {
        kits.remove(type);
    }

    public boolean hasKit(KitType kit) {
        return getKits().containsKey(kit);
    }

    public void addKit(KitType kit) {
        if (getKits().containsKey(kit))
            return;
        kits.put(kit, new KitData(kit));
    }

    public Integer getLastStreak() {
        return lastStreak;
    }

}