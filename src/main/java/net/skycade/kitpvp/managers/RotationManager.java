package net.skycade.kitpvp.managers;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import static net.skycade.kitpvp.Messages.KITS_ROTATED;

public class RotationManager {

    private final JavaPlugin plugin;
    private final File file;
    private YamlConfiguration yaml;

    public RotationManager() {
        plugin = KitPvP.getInstance();

        file = new File(plugin.getDataFolder(), "rotation.yml");
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("last-rotation", System.currentTimeMillis());
        defaults.put("current-rotation", new ArrayList<String>());
        defaults.put("no-rotation", new ArrayList<String>());

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
        if (!KitPvP.getInstance().getConfig().getBoolean("kits-rotation-enabled"))
            updateNoRotation();
        else
            startRotationUpdater();
    }

    private long getLastRotation() {
        return yaml.getLong("last-rotation");
    }

    private void setLastRotation() {
        yaml.set("last-rotation", System.currentTimeMillis());
        save();
    }

    private List<String> getCurrentKitRotation() {
        if (!KitPvP.getInstance().getConfig().getBoolean("kits-rotation-enabled")){
            List<String> list = yaml.getStringList("no-rotation");
            if (list == null || list.isEmpty()) updateNoRotation();
            return yaml.getStringList("no-rotation") == null ? new ArrayList<>() : yaml.getStringList("no-rotation");
        }
        else{
            List<String> list = yaml.getStringList("current-rotation");
            if (list == null || list.isEmpty()) updateRotation();
            return yaml.getStringList("current-rotation") == null ? new ArrayList<>() : yaml.getStringList("current-rotation");
        }
    }

    public List<KitType> getCurrentKits() {
        List<KitType> kits = new ArrayList<>();
        getCurrentKitRotation().forEach(currKit -> kits.add(KitType.getTypeFromString(currKit)));
        return kits;
    }

    private void updateRotation() {
        List<String> rotationKits = new ArrayList<>();
        fillRotationList(rotationKits);
        sort(rotationKits);
        setLastRotation();
        setRotationKits(rotationKits);
    }

    private void updateNoRotation(){
        List<String> kits = new ArrayList<>();
        fillNoRotationList(kits);
        sort(kits);
        setNoRotationKits(kits);
    }

    private void setRotationKits(List<String> rotationKits) {
        yaml.set("current-rotation", rotationKits);
        save();
    }

    private void setNoRotationKits(List<String> kits) {
        yaml.set("no-rotation", kits);
        save();
    }

    private void fillNoRotationList(List<String> kits) {
        if (kits.size() >= KitPvP.getInstance().getAvailableKits()-4)
            return;
        List<KitType> available = new ArrayList<>();
        for (KitType kitType : KitType.values()) {
            if (kitType.getKit().isEnabled()) available.add(kitType);
        }

        KitType kitType = available.get(UtilMath.getRandom(0, available.size() - 1));

        if (kits.contains(kitType.toString()) || !kitType.getKit().isEnabled() || Arrays.asList(KitType.DEFAULT, KitType.KITMASTER, KitType.CHANCE, KitType.ARCHER, KitType.DUBSTEP).contains(kitType)) {
            fillNoRotationList(kits);
            return;
        }

        kits.add(kitType.toString());
        fillNoRotationList(kits);

    }

    private void fillRotationList(List<String> rotationKits) {
        if (rotationKits.size() >= KitPvP.getInstance().getConfig().getInt("kits-rotation-amount"))
            return;
        List<KitType> available = new ArrayList<>();
        for (KitType kitType : KitType.values()) {
            if (kitType.getKit().isEnabled()) available.add(kitType);
        }

        KitType kitType = available.get(UtilMath.getRandom(0, available.size() - 1));

        if (rotationKits.contains(kitType.toString()) || !kitType.getKit().isEnabled() || Arrays.asList(KitType.DEFAULT, KitType.KITMASTER, KitType.CHANCE, KitType.ARCHER).contains(kitType)) {
            fillRotationList(rotationKits);
            return;
        }

        rotationKits.add(kitType.toString());
        fillRotationList(rotationKits);
    }

    private void startRotationUpdater() {
        int updateMilliSecs = KitPvP.getInstance().getConfig().getInt("rotation-seconds") * 1000;
        long difference = System.currentTimeMillis() - getLastRotation();
        long nextUpdateSecs = (updateMilliSecs - difference) / 1000;
        if (nextUpdateSecs < 0)
            updateRotation();

        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
            updateRotation();
            startRotationUpdater();
            Bukkit.getOnlinePlayers().forEach(p -> KITS_ROTATED.msg(p));
        }, 20 * nextUpdateSecs);
    }

    // Sort on kit price, low to high
    private void sort(List<String> rotationKits) {
        String temp;
        for (int i = 0; i < rotationKits.size(); i++) {
            for (int j = i; j > 0; j--) {
                if (Objects.requireNonNull(KitType.getTypeFromString(rotationKits.get(j))).getKit().getPrice() < Objects.requireNonNull(KitType.getTypeFromString(rotationKits.get(j - 1))).getKit().getPrice()) {
                    temp = rotationKits.get(j);
                    rotationKits.set(j, rotationKits.get(j - 1));
                    rotationKits.set(j - 1, temp);
                }
            }
        }

    }

    private void save() {
        try {
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't save rotation yaml file.", e);
        }
    }

    public void update() {
        save();
    }

}
