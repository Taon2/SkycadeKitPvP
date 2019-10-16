package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static java.lang.Integer.parseInt;

public class KitKnight extends Kit {

    public KitKnight(KitManager kitManager) {
        super(kitManager, "Knight", KitType.KNIGHT, 26000, "Loyal to his king");

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "CHAINMAIL_CHESTPLATE");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 26000);

        defaultsMap.put("inventory.sword.material", "DIAMOND_SWORD");
        defaultsMap.put("inventory.sword.enchantments.durability", 5);
        defaultsMap.put("inventory.sword.enchantments.damage-all", 0);

        defaultsMap.put("armor.material", "CHAINMAIL");
        defaultsMap.put("armor.enchantments.durability", 5);
        defaultsMap.put("armor.enchantments.protection", 1);

        defaultsMap.put("armor.boots.enchantments.protection", 2);
        defaultsMap.put("armor.leggings.enchantments.protection", 2);

        defaultsMap.put("potions.pot1", "NIGHT_VISION:0");

        setConfigDefaults(defaultsMap);

        if (getConfig().getString("kit.icon.material") != null) {
            if (getConfig().getString("kit.icon.material").contains("LEATHER")) {
                setIcon(new ItemBuilder(Material.getMaterial(getConfig().getString("kit.icon.material").toUpperCase()))
                        .setColour(getColor(getConfig().getString("kit.icon.color"))).build());
            } else {
                setIcon(new ItemStack(Material.getMaterial(getConfig().getString("kit.icon.material").toUpperCase())));
            }
        } else {
            setIcon(new ItemStack(Material.DIRT));
        }
        setPrice(getConfig().getInt("kit.price"));
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(new ItemBuilder(
                Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
                .addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

        p.getInventory().setArmorContents(getArmour(
                Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
                getConfig().getInt("armor.enchantments.durability"),
                getConfig().getInt("armor.enchantments.protection")));

        //boots
        p.getInventory().getArmorContents()[0].removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
        p.getInventory().getArmorContents()[0]
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.boots.enchantments.protection"));

        //leggings
        p.getInventory().getArmorContents()[1].removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
        p.getInventory().getArmorContents()[1]
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.leggings.enchantments.protection"));

        String[] pot1 = getConfig().getString("potions.pot1").split(":");
        p.addPotionEffect(new PotionEffect(
                PotionEffectType.getByName(pot1[0]),
                Integer.MAX_VALUE,
                parseInt(pot1[1])));
    }

    @Override
    public void onMove(Player p) {
        for (Player target : UtilPlayer.getNearbyPlayers(p.getLocation(), 6)) {
            if (getKitManager().getKitPvP().getStats(target).getActiveKit() == KitType.GHOST) {
                Location location = target.getLocation();
                for (int i = 0; i < 30; i++) {
                    double angle, x, z;
                    angle = 2 * Math.PI * i / 30;
                    x = Math.cos(angle) * 1;
                    z = Math.sin(angle) * 1;
                    location.add(x, 0, z);
                    ParticleEffect.VILLAGER_HAPPY.display(0.03F, 0.02F, 0.03F, 0.05F, 1, location, Collections.singletonList(p));
                    location.subtract(x, 0, z);
                }
            }
        }
    }

    @Override
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7Ghosts around you will get", "§7a particle effect");
    }
}
