package me.bukkit.kitpvp.stat;

import me.bukkit.kitpvp.KitPvP;
import me.bukkit.kitpvp.Settings;
import me.bukkit.kitpvp.coreclasses.utils.UtilMath;
import me.bukkit.kitpvp.kit.KitType;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RotationManager {

    private final Document doc;

    public RotationManager() {
        if (KitPvPDB.getInstance().getKitpvpCollection().count() == 0) {
            doc = new Document();
            doc.put("last_rotation", System.currentTimeMillis());
            KitPvPDB.getInstance().getKitpvpCollection().insertOne(doc);
        } else
            doc = KitPvPDB.getInstance().getKitpvpCollection().find().first();
        startRotationUpdater();
    }

    public long getLastRotation() {
        return doc.getLong("last_rotation");
    }

    public void setLastRotation(long time) {
        doc.put("last_rotation", time);
    }

    public void setLastRotation() {
        doc.put("last_rotation", System.currentTimeMillis());
    }

    public List<String> getCurrentKitRotation() {
        if (!doc.containsKey("current_rotation"))
            updateRotation();
        return (List<String>) doc.get("current_rotation");
    }

    public List<KitType> getCurrentKits() {
        List<KitType> kits = new ArrayList<>();
        getCurrentKitRotation().forEach(currKit -> kits.add(KitType.valueOf(currKit)));
        return kits;
    }

    private void updateRotation() {
        List<String> rotationKits = new ArrayList<>();
        fillRotationList(rotationKits);
        sort(rotationKits);
        setLastRotation();
        doc.put("current_rotation", rotationKits);
    }

    private void fillRotationList(List<String> rotationKits) {
        if (rotationKits.size() >= Settings.KITS_ROTATION_AMOUNT)
            return;
        KitType kitType = KitType.values()[UtilMath.getRandom(0, KitType.values().length - 1)];

        if(rotationKits.contains(kitType.toString()) || !kitType.getKit().isEnabled() || Arrays.asList(KitType.DEFAULT, KitType.KITMASTER).contains(kitType)) {
            fillRotationList(rotationKits);
            return;
        }

        rotationKits.add(kitType.toString());
        fillRotationList(rotationKits);
    }

    private void startRotationUpdater() {
        int updateMilliSecs = Settings.ROTATION_SECONDS * 1000;
        long difference = System.currentTimeMillis() - getLastRotation();
        long nextUpdateSecs = (updateMilliSecs - difference) / 1000;
        if (nextUpdateSecs < 0)
            updateRotation();;

        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
            updateRotation();
            startRotationUpdater();
            Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage("§7Kits in the shop have §arotated§7!"));
        }, 20 * nextUpdateSecs);
    }

    // Sort on kit price, low to high
    private void sort(List<String> rotationKits) {
        String temp;
        for (int i = 0; i < rotationKits.size(); i++) {
            for (int j = i; j > 0; j--) {
                if (KitType.valueOf(rotationKits.get(j)).getKit().getPrice()< KitType.valueOf(rotationKits.get(j - 1)).getKit().getPrice()) {
                    temp = rotationKits.get(j);
                    rotationKits.set(j, rotationKits.get(j - 1));
                    rotationKits.set(j - 1, temp);
                }
            }
        }

    }

    public void updateToDB() {
        KitPvPDB.getInstance().getKitpvpCollection().drop();
        KitPvPDB.getInstance().getKitpvpCollection().insertOne(doc);
    }

}
