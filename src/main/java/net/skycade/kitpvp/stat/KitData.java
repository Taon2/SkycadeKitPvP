package net.skycade.kitpvp.stat;

import net.skycade.kitpvp.kit.KitType;

import java.util.HashMap;
import java.util.Map;

public class KitData {

    private final Map<String, Integer> map;

    public KitData(KitType kitType) {
        this(new HashMap<>());
        setLevel(1);
    }

    public KitData(Map<String, Integer> map) {
        this.map = map;
    }

    public int getLevel() {
        return getInt("level");
    }

    public void setLevel(int level) {
        set("level", level);
    }

    public int getXp() {
        return getInt("xp");
    }

    public void setXp(int xp) {
        set("xp", xp);
    }

    private int getInt(String key) {
        if (!map.containsKey(key))
            map.put(key, 0);
        return map.get(key);
    }

    private void set(String key, int value) {
        map.put(key, value);
    }

    public Map<String, Integer> getMap() {
        return map;
    }

}