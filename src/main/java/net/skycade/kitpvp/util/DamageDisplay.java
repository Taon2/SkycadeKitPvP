package net.skycade.kitpvp.util;

import net.skycade.kitpvp.KitPvP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

public class DamageDisplay {

    public static void displayDamage(Location loc, int ticks, int damage) {
        ArmorStand armorStand = createArmorStand(loc, damage);
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), armorStand::remove, ticks);
    }

    private static ArmorStand createArmorStand(Location loc, int damage) {
        ArmorStand armorStand = loc.getWorld().spawn(loc, ArmorStand.class);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setCustomName("§b" + damage);
        armorStand.setCustomNameVisible(true);
        return armorStand;
    }

}
