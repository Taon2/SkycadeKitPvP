package net.skycade.kitpvp.stat;

import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import net.skycade.crates.CrateUser;
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
    private long coins = 0;
    private int eventTokens = 0;
    private int streak = 0;
    private int highestStreak = 0;
    private int assists = 0;
    private boolean abilityToggle = false;
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

    public long getCoins() {
        return coins;
    }

    public void setCoins(long coins) {
        this.coins = coins;
    }

    public void giveCoins(long coins) {
        this.coins += coins;

        //Adds the amount of coins to the gang points
        Gang gang = null;
        if (Bukkit.getOfflinePlayer(uuid).isOnline())
            gang = GangsPlusApi.getPlayersGang(Bukkit.getPlayer(uuid));
        if (gang != null)
            KitPvP.getInstance().getGangPointsManager().addPoints(gang.getName(), (long) coins);
    }

    public void takeCoins(long coins) {
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
            KitPvP.getInstance().getGangPointsManager().addPoints(gang.getName(), (long) (eventTokens * 200));
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
        kits.forEach(((kitType, kitData) -> {
            CrateUser crateUser = CrateUser.get(uuid);
            crateUser.removeReward("skycade.crates.reward." + kitType.name().toLowerCase());
        }));

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

        CrateUser crateUser = CrateUser.get(uuid);
        crateUser.removeReward("skycade.crates.reward." + kit.name().toLowerCase());
    }

    public boolean hasKit(KitType kit) {
        return getKits().containsKey(kit);
    }

    public void addKit(KitType kit) {
        if (getKits().containsKey(kit))
            return;
        kits.put(kit, new KitData(kit));

        boolean hasReward = false;
        // Checks all active rewards to see if the player has the reward
        CrateUser crateUser = CrateUser.get(uuid);

        if (crateUser != null) {
            if (crateUser.getRewards(false).containsKey("skycade.crates.reward." + kit.name().toLowerCase())) {
                hasReward = true;
            }

            // Stops from being applied if already applied
            if (!hasReward) {
                crateUser.addReward("skycade.crates.reward." + kit.name().toLowerCase());
            }
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

    public boolean isAbilityToggle() {
        return abilityToggle;
    }

    public void setAbilityToggle(boolean abilityToggle) {
        this.abilityToggle = abilityToggle;
    }
}