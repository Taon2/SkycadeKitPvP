package net.skycade.kitpvp.managers;

import net.skycade.kitpvp.KitPvP;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class DatabaseManager {

    private final JavaPlugin plugin;
    private final File file;
    private YamlConfiguration yaml;

    public DatabaseManager() {
        plugin = KitPvP.getInstance();

        file = new File(plugin.getDataFolder(), "database.yml");
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("database.name", "negativekb");
        defaults.put("database.host", "localhost");
        defaults.put("database.port", 3306);
        defaults.put("database.username", "negativekb");
        defaults.put("database.kitpvp-table", "skycade_kitpvp_members");
        defaults.put("database.kitpvp-prestige-levels", "skycade_kitpvp_prestige_levels");
        defaults.put("database.password", "uTb9jXnBKtLPwvczvkge8GFA");


        if (!file.exists()) {
            yaml = new YamlConfiguration();
            for (Map.Entry<String, Object> entry : defaults.entrySet()) {
                yaml.set(entry.getKey(), entry.getValue());
            }
            save();
        } else {
            yaml = YamlConfiguration.loadConfiguration(file);
            for (Map.Entry<String, Object> entry : defaults.entrySet()) {
                if (yaml.get(entry.getKey(), null) == null) yaml.set(entry.getKey(), entry.getValue());
            }
            save();
        }
    }
    private void save() {
        try {
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't save database yaml file.", e);
        }
    }

    public YamlConfiguration get(){
        return yaml;
    }
}
