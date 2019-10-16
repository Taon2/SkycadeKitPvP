package net.skycade.kitpvp.kit.kits.disabled;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KitDefault extends Kit {

    public KitDefault(KitManager kitManager) {
        super(kitManager, "Default", KitType.DEFAULT, 5000, false, "Default kit");

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "DIAMOND_SWORD");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 5000);

        defaultsMap.put("inventory.sword.material", "DIAMOND_SWORD");
        defaultsMap.put("inventory.sword.enchantments.durability", 5);
        defaultsMap.put("inventory.sword.enchantments.damage-all", 0);

        defaultsMap.put("armor.helmet.material", "IRON");
        defaultsMap.put("armor.helmet.enchantments.projectile-protection", 0);

        defaultsMap.put("armor.chestplate.material", "IRON");

        defaultsMap.put("armor.leggings.material", "LEATHER");

        defaultsMap.put("armor.boots.material", "IRON");
        defaultsMap.put("armor.boots.enchantments.protection", 0);

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

        p.getInventory().setHelmet(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.helmet.material").toUpperCase() + "_HELMET"))
                .addEnchantment(Enchantment.PROTECTION_PROJECTILE, getConfig().getInt("armor.helmet.enchantments.projectile-protection")).build());

        p.getInventory().setChestplate(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.chestplate.material").toUpperCase() + "_CHESTPLATE")).build());

        p.getInventory().setLeggings(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.leggings.material").toUpperCase() + "_LEGGINGS")).build());

        p.getInventory().setBoots(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.boots.material").toUpperCase() + "_BOOTS"))
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.boots.enchantments.protection")).build());
    }

}
