package me.bukkit.kitpvp.stat;

import me.bukkit.kitpvp.Settings;
import me.bukkit.kitpvp.coreclasses.member.Member;
import me.bukkit.kitpvp.kit.KitType;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public class KitPvPStats {

    private final Member member;
    private Integer lastStreak;

    public KitPvPStats(Member member) {
        this.member = member;
    }

    public Document getDocument() {
        if (member.get("kitpvp") == null)
            member.put("kitpvp", new Document());
        return (Document) member.get("kitpvp");
    }

    public int getKills() {
        return getInt("kills");
    }

    public void setKills(int kills) {
        set("kills", kills);
    }

    public int getDeaths() {
        return getInt("deaths");
    }

    public void setDeaths(int deaths) {
        set("deaths", deaths);
    }

    public int getDuels() {
        return getInt("duels");
    }

    public void setDuels(int duels) {
        set("duels", duels);
    }

    public int getCoins() {
        if (!getDocument().containsKey("coins"))
            set("coins", Settings.START_COINS);
        return getInt("coins");
    }

    public void setCoins(int coins) {
        set("coins", coins);
    }

    public int getStreak() {
        return getInt("streak");
    }

    public void setStreak(int streak) {
        if (streak < getStreak())
            lastStreak = getStreak();
        set("streak", streak);
    }

    public int getHighestStreak() {
        return getInt("highest_streak");
    }

    public void setHighestStreak(int highestStreak) {
        set("highest_streak", highestStreak);
    }

    public int getCrateKeys() {
        if (!getDocument().containsKey("keys"))
            set("keys", Settings.START_KEYS);
        return getInt("keys");
    }

    public void setCrateKeys(int keys) {
        set("keys", keys);
    }

    public int getAssists() {
        return getInt("assists");
    }

    public void setAssists(int assists) {
        set("assists", assists);
    }

    public KitType getActiveKit() {
        if (!getDocument().containsKey("kit"))
            getDocument().put("kit", KitType.DEFAULT.toString());
        return KitType.valueOf(getDocument().getString("kit"));
    }

    public void setActiveKit(KitType kitType) {
        set("kit", kitType.toString());
    }

    public KitType getKitPreference() {
        if (getDocument().containsKey("kit_preference"))
            return KitType.valueOf(getDocument().getString("kit_preference"));
        return KitType.DEFAULT;
    }

    public void setKitPreference(KitType kitType) {
        getDocument().put("kit_preference", kitType.toString());
    }

    public boolean applyKitPreference() {
        if (getKitPreference() == null)
            return false;
        setActiveKit(getKitPreference());
        return true;
    }

    public Map<KitType, KitData> getKits() {
        Document document = getDocument();
        if (!document.containsKey("kits")) {
            document.put("kits", new Document());

            //Unlocked from the start:
            KitType def = KitType.DEFAULT;
            ((Document) document.get("kits")).put(def.toString(), new KitData(def).getDocument());

            for (KitType kitType : Settings.START_KITS) {
                ((Document) document.get("kits")).put(kitType.toString(), new KitData(kitType).getDocument());
                ((Document) document.get("kits")).put(kitType.toString(), new KitData(kitType).getDocument());
            }

        }
        Document kits = (Document) document.get("kits");
        Map<KitType, KitData> kitDatas = new HashMap<>();
        kits.keySet().forEach(kit -> {
            KitData data = new KitData((Document) kits.get(kit));
            kitDatas.put(KitType.valueOf(kit), data);
        });
        return kitDatas;
    }

    public void resetKits() {
        Document document = getDocument();
        document.put("kits", new Document());
        KitType kit = KitType.DEFAULT;
        ((Document) document.get("kits")).put(kit.toString(), new KitData(kit).getDocument());
    }

    public void removeKit(KitType type) {
        Document document = getDocument();
        Document kits = (Document) document.get("kits");
        for (String kit : kits.keySet()) {
            if (type.equals(KitType.valueOf(kit))) {
                ((Document) document.get("kits")).remove(kit);
                break;
            }
        }
    }

    public boolean hasKit(KitType kit) {
        return getKits().containsKey(kit);
    }

    public void addKit(KitType kit) {
        if (getKits().containsKey(kit))
            return;
        ((Document) getDocument().get("kits")).put(kit.toString(), new KitData(kit).getDocument());
    }

    private int getInt(String key) {
        Document document = getDocument();
        if (!document.containsKey(key))
            document.put(key, 0);
        return document.getInteger(key);
    }

    private void set(String key, Object value) {
        Document document = getDocument();
        document.put(key, value);
        member.put("kitpvp", document);
    }

    public Integer getLastStreak() {
        return lastStreak;
    }

}