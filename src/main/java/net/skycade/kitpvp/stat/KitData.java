package net.skycade.kitpvp.stat;

import net.skycade.kitpvp.kit.KitType;

import java.util.HashMap;
import java.util.Map;

public class KitData {

    private final Map<String, Integer> map;

    public KitData(KitType kitType) {
        this(new HashMap<>());
    }

    public KitData(Map<String, Integer> map) {
        this.map = map;
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