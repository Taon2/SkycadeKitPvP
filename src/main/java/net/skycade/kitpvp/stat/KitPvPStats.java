package net.skycade.kitpvp.stat;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.kit.KitType;
import org.bson.Document;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class KitPvPStats {

    private final Member member;
    private Integer lastStreak;

    public KitPvPStats(Member member) {
        this.member = member;
    }

    public Map<String, Object> getProperties() {
        return member.getProperties();
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
        if (!getProperties().containsKey("keys"))
            set("keys", KitPvP.getInstance().getConfig().getInt("start-keys"));
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
        if (!getProperties().containsKey("kit"))
            getProperties().put("kit", KitType.DEFAULT.toString());
        return KitType.valueOf((String) getProperties().get("kit"));
    }

    public void setActiveKit(KitType kitType) {
        set("kit", kitType.toString());
    }

    public KitType getKitPreference() {
        if (getProperties().containsKey("kit_preference"))
            return KitType.valueOf((String) getProperties().get("kit_preference"));
        return KitType.DEFAULT;
    }

    public void setKitPreference(KitType kitType) {
        getProperties().put("kit_preference", kitType.toString());
    }

    public boolean applyKitPreference() {
        if (getKitPreference() == null)
            return false;
        setActiveKit(getKitPreference());
        return true;
    }

    public Map<KitType, KitData> getKits() {
        Map<String, Object> properties = getProperties();
        if (!properties.containsKey("kits")) {
            properties.put("kits", new Document());

            //Unlocked from the start:
            KitType def = KitType.DEFAULT;
            ((Document) properties.get("kits")).put(def.toString(), new KitData(def).getDocument());

            for (String kitType : KitPvP.getInstance().getConfig().getStringList("start-kits")) {
                ((Document) properties.get("kits")).put(KitType.byAlias(kitType).toString(), new KitData(KitType.byAlias(kitType)).getDocument());
                ((Document) properties.get("kits")).put(KitType.byAlias(kitType).toString(), new KitData(KitType.byAlias(kitType)).getDocument());
            }

        }
        Document kits = (Document) properties.get("kits");
        Map<KitType, KitData> kitDatas = new HashMap<>();
        kits.keySet().forEach(kit -> {
            KitData data = new KitData(new Document((JSONObject) kits.get(kit)));
            kitDatas.put(KitType.valueOf(kit), data);
        });
        return kitDatas;
    }

    public void resetKits() {
        Map<String, Object> properties = getProperties();
        properties.put("kits", new Document());
        KitType kit = KitType.DEFAULT;
        ((Document) properties.get("kits")).put(kit.toString(), new KitData(kit).getDocument());
    }

    public void removeKit(KitType type) {
        Map<String, Object> properties = getProperties();
        Document kits = (Document) properties.get("kits");
        for (String kit : kits.keySet()) {
            if (type.equals(KitType.valueOf(kit))) {
                ((Document) properties.get("kits")).remove(kit);
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
        ((Document) getProperties().get("kits")).put(kit.toString(), new KitData(kit).getDocument());
    }

    private int getInt(String key) {
        Object value = member.getProperties().getOrDefault(key, 0);
        return (int) value;
    }

    private void set(String key, Object value) {
        member.getProperties().put(key, value);
    }

    public Integer getLastStreak() {
        return lastStreak;
    }

}