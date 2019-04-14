package net.skycade.kitpvp.ui.eventshopitems;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.commands.CommandEventShop;
import net.skycade.kitpvp.commands.staff.CommandEventEco;
import net.skycade.kitpvp.coreclasses.commands.Module;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.ui.EventShopMenu;
import net.skycade.kitpvp.ui.eventshopitems.items.ItemCoinBoost;
import net.skycade.kitpvp.ui.eventshopitems.items.ItemPotionEffect;
import net.skycade.kitpvp.ui.eventshopitems.items.ItemProtUpgrade;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

public class EventShopManager extends Module {

    private File file;
    private YamlConfiguration yaml;
    private final KitPvP plugin;
    private final Map<String, EventShopItem> eventShopItems = new LinkedHashMap<>();
    private final EventShopMenu eventShopMenu;

    public EventShopManager (KitPvP plugin) {
        this.plugin = plugin;

        configManager();

        eventShopMenu = new EventShopMenu(this);
        registerEventShopItems();

        registerCommand(new CommandEventShop(this));
        registerCommand(new CommandEventEco(this));

        registerListener(eventShopMenu);
    }

    private void registerEventShopItems(){
        registerEventShopItem(new ItemCoinBoost(this));
        registerEventShopItem(new ItemPotionEffect(this));
        registerEventShopItem(new ItemProtUpgrade(this));
    }

    private void registerEventShopItem(EventShopItem item) {
        eventShopItems.put(item.getName(), item);
        //Bukkit.getPluginManager().registerEvents(item, plugin);
    }

    public Map<String, EventShopItem> getEventShopItems() {
        return eventShopItems;
    }

    public EventShopMenu getEventShopMenu() {
        return eventShopMenu;
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
