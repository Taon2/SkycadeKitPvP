package net.skycade.kitpvp.stat;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.kit.KitType;

import java.util.HashMap;
import java.util.Map;

public class KitPvPStats {

    private final Member member;
    private Integer lastStreak;

    private int kills = 0;
    private int deaths = 0;
    private int keys =  KitPvP.getInstance().getConfig().getInt("start-keys");
    private int duels = 0;
    private int coins = 0;
    private int streak = 0;
    private int highestStreak = 0;
    private int assists = 0;
    private KitType activeKit = KitType.DEFAULT;
    private KitType kitPreference = KitType.DEFAULT;
    private Map<KitType, KitData> kits = new HashMap<>();


    public KitPvPStats(Member member) {
        this.member = member;

        //Unlocked from the start:
        KitType def = KitType.DEFAULT;
        kits.put(def, new KitData(def));

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

    public int getDuels() {
        return duels;
    }

    public void setDuels(int duels) {
        this.duels = duels;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        if (streak < getStreak())
            lastStreak = getStreak();
        this.streak = streak;
    }

    public int getHighestStreak() {
        return highestStreak;
    }

    public void setHighestStreak(int highestStreak) {
        this.highestStreak = highestStreak;
    }

    public int getCrateKeys() {
        return keys;
    }

    public void setCrateKeys(int keys) {
        this.keys = keys;
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

    public KitType getKitPreference() {
        return kitPreference;
    }

    public void setKitPreference(KitType kitType) {
        this.kitPreference = kitType;
    }

    public boolean applyKitPreference() {
        if (getKitPreference() == null)
            return false;
        setActiveKit(getKitPreference());
        return true;
    }

    public Map<KitType, KitData> getKits() {
        return kits;
    }

    public void resetKits() {
        KitType kit = KitType.DEFAULT;
        Map<KitType, KitData> map = new HashMap<>();
        map.put(kit, new KitData(kit));
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