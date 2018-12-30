package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitBladeMaster extends Kit {

    public KitBladeMaster(KitManager kitManager) {
        super(kitManager, "BladeMaster", KitType.BLADEMASTER, 29000, "Master of blades");

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "GOLD_SWORD");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 29000);

        defaultsMap.put("inventory.sword.material", "GOLD_SWORD");
        defaultsMap.put("inventory.sword.enchantments.damage-all", 2);
        defaultsMap.put("inventory.sword.enchantments.durability", 10);
        defaultsMap.put("inventory.sword.name", "Sword of damage");
        defaultsMap.put("inventory.sword2.material", "GOLD_SWORD");
        defaultsMap.put("inventory.sword2.enchantments.durability", 10);
        defaultsMap.put("inventory.sword2.enchantments.knockback", 2);
        defaultsMap.put("inventory.sword2.name", "Sword of knockback");
        defaultsMap.put("armor.boots.material", "IRON");
        defaultsMap.put("armor.leggings.material", "IRON");
        defaultsMap.put("armor.chestplate.material", "IRON");
        defaultsMap.put("armor.helmet.material", "GOLD");
        defaultsMap.put("armor.helmet.enchantments.durability", 2);

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
    public void applyKit(Player p, int level) {
        p.getInventory().addItem(new ItemBuilder(
                Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
                .addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
                .setName(getConfig().getString("inventory.sword.name")).build());

        p.getInventory().addItem(new ItemBuilder(
                Material.getMaterial(getConfig().getString("inventory.sword2.material").toUpperCase()))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword2.enchantments.durability"))
                .addEnchantment(Enchantment.KNOCKBACK, getConfig().getInt("inventory.sword2.enchantments.knockback"))
                .setName(getConfig().getString("inventory.sword2.name")).build());

        p.getInventory().setBoots(new ItemBuilder(Material.getMaterial(getConfig().getString("armor.boots.material").toUpperCase() + "_BOOTS")).build());
        p.getInventory().setLeggings(new ItemBuilder(Material.getMaterial(getConfig().getString("armor.leggings.material").toUpperCase() + "_LEGGINGS")).build());
        p.getInventory().setChestplate(new ItemBuilder(Material.getMaterial(getConfig().getString("armor.chestplate.material").toUpperCase() + "_CHESTPLATE")).build());
        p.getInventory().setHelmet(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.helmet.material").toUpperCase() + "_HELMET"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.helmet.enchantments.durability")).build());
    }

    @Override
    public void onMove(Player p) {
        if (p.isSneaking()) {
            if (p.hasPotionEffect(PotionEffectType.SPEED))
                p.removePotionEffect(PotionEffectType.SPEED);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, getLevel(p) * 75, 0));
        }
    }

    @Override
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7Gain a short speed buff", "§7when you're sneaking.");
    }

}
