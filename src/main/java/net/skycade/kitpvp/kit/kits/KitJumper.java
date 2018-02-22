package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class KitJumper extends Kit {

    public KitJumper(KitManager kitManager) {
        super(kitManager, "Jumper", KitType.JUMPER, 26000, "There is no place he can't jump on");

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "LEATHER_BOOTS");
        defaultsMap.put("kit.icon.color", "BROWN");
        defaultsMap.put("kit.price", 26000);

        defaultsMap.put("inventory.sword.material", "IRON_SWORD");
        defaultsMap.put("inventory.sword.enchantments.damage-all", 1);
        defaultsMap.put("inventory.sword.enchantments.durability", 5);

        defaultsMap.put("armor.helmet.material", "LEATHER");
        defaultsMap.put("armor.helmet.enchantments.protection", 3);
        defaultsMap.put("armor.helmet.enchantments.durability", 10);

        defaultsMap.put("armor.chestplate.material", "LEATHER");
        defaultsMap.put("armor.chestplate.enchantments.protection", 3);
        defaultsMap.put("armor.chestplate.enchantments.durability", 10);

        defaultsMap.put("armor.leggings.material", "LEATHER");
        defaultsMap.put("armor.leggings.enchantments.protection", 3);
        defaultsMap.put("armor.leggings.enchantments.durability", 10);

        defaultsMap.put("armor.boots.material", "LEATHER");
        defaultsMap.put("armor.boots.enchantments.protection", 3);
        defaultsMap.put("armor.boots.enchantments.durability", 10);

        defaultsMap.put("potions.pot1", "JUMP:4");

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
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability")).build());

        p.getInventory().setHelmet(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.helmet.material").toUpperCase() + "_HELMET"))
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.helmet.enchantments.protection"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.helmet.enchantments.durability"))
                .setColour(Color.WHITE).build());

        p.getInventory().setChestplate(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.chestplate.material").toUpperCase() + "_CHESTPLATE"))
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.chestplate.enchantments.protection"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.chestplate.enchantments.durability"))
                .setColour(Color.RED).build());

        p.getInventory().setLeggings(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.leggings.material").toUpperCase() + "_LEGGINGS"))
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.leggings.enchantments.protection"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.leggings.enchantments.durability"))
                .setColour(Color.RED).build());

        p.getInventory().setBoots(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.boots.material").toUpperCase() + "_BOOTS"))
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.boots.enchantments.protection"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.boots.enchantments.durability"))
                .setColour(Color.WHITE).build());

        String[] pot1 = getConfig().getString("potions.pot1").split(":");
        p.addPotionEffect(new PotionEffect(
                PotionEffectType.getByName(pot1[0]),
                Integer.MAX_VALUE,
                parseInt(pot1[1])));
    }

    @Override
    public void onMove(Player p) {
        if (p.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR)
            return;
        for (int y = 0; y < 10; y++) {
            Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> {
                for (int i = 0; i < 5; i++)
                    ParticleEffect.SPELL_MOB.display(new ParticleEffect.OrdinaryColor(Color.RED), p.getLocation().add(0, 0.1F, 0), 1F);
            }, y);
        }
    }

}
