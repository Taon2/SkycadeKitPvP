package net.skycade.kitpvp.ui.eventshopitems;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.ui.eventshopitems.items.ItemCoinBoost;
import net.skycade.kitpvp.ui.eventshopitems.items.ItemKeepKillstreak;
import net.skycade.kitpvp.ui.eventshopitems.items.ItemPotionEffect;
import net.skycade.kitpvp.ui.eventshopitems.items.ItemProtUpgrade;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

public class EventShopManager {

    private File file;
    private YamlConfiguration yaml;
    private final KitPvP plugin;
    private final Map<String, EventShopItem> eventShopItems = new LinkedHashMap<>();

    public EventShopManager (KitPvP plugin) {
        this.plugin = plugin;

        configManager();

        registerEventShopItems();
    }

    private void registerEventShopItems(){
        registerEventShopItem(new ItemCoinBoost(this));
        registerEventShopItem(new ItemKeepKillstreak(this));
        registerEventShopItem(new ItemPotionEffect(this));
        registerEventShopItem(new ItemProtUpgrade(this));
    }

    private void registerEventShopItem(EventShopItem item) {
        eventShopItems.put(item.getName(), item);
        Bukkit.getPluginManager().registerEvents(item, plugin);
    }

    public Map<String, EventShopItem> getEventShopItems() {
        return eventShopItems;
    }

    public KitPvP getKitPvP() {
        if (plugin == null)
            return KitPvP.getInstance();
        return plugin;
    }

    public EventShopItem getTypeFromString (String name) {
        if (eventShopItems.containsKey(name))
            return eventShopItems.get(name);
        return null;
    }

    public void reapplyUpgrades(Player p) {
        eventShopItems.forEach((key, item) -> {
            if (yaml.contains(p.getUniqueId().toString()) && yaml.contains(p.getUniqueId().toString() + "." + item.getName()) && (System.currentTimeMillis() - yaml.getLong(p.getUniqueId().toString() + "." + item.getName())) / 1000L < item.getDuration()) {
                item.reapplyReward(p);
            }
        });
    }

    public boolean isKeepingKs(Player p) {
        boolean keepKs = false;
        for (Map.Entry<String, EventShopItem> entry : eventShopItems.entrySet()) {
            String key = entry.getKey();
            EventShopItem item = entry.getValue();
            if (key.contains("Killstreak")) {
                keepKs = getYaml().contains(p.getUniqueId().toString()) &&
                        getYaml().contains(p.getUniqueId().toString() + "." + item.getName()) &&
                        getYaml().getBoolean(p.getUniqueId().toString() + "." + item.getName());
            }
        }

        return keepKs;
    }

    public YamlConfiguration getYaml() {
        return yaml;
    }

    public void setYaml(YamlConfiguration yaml) {
        this.yaml = yaml;
    }

    private void configManager() {
        file = new File(plugin.getDataFolder(), "purchasedupgrades.yml");

        if (!file.exists()) {
            yaml = new YamlConfiguration();
            save();
        } else {
            yaml = YamlConfiguration.loadConfiguration(file);
            save();
        }
    }

    public void save() {
        try {
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't save purchasedupgrades yaml file.", e);
        }
    }
}