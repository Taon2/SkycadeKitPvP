package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Color;
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

public class KitChronos extends Kit {

    public KitChronos(KitManager kitManager) {
        super(kitManager, "Chronos", KitType.CHRONOS, 44000, "Time around him slows down");
        setIcon(new ItemStack(Material.WATCH));

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("inventory.sword.material", "IRON_SWORD");
        defaultsMap.put("inventory.sword.enchantments.damage-all", 0);
        defaultsMap.put("inventory.sword.enchantments.durability", 5);

        defaultsMap.put("armor.helmet.material", "LEATHER");
        defaultsMap.put("armor.helmet.enchantments.durability", 12);
        defaultsMap.put("armor.helmet.enchantments.protection", 4);

        defaultsMap.put("armor.chestplate.material", "LEATHER");
        defaultsMap.put("armor.chestplate.enchantments.durability", 12);
        defaultsMap.put("armor.chestplate.enchantments.protection", 4);

        defaultsMap.put("armor.leggings.material", "LEATHER");
        defaultsMap.put("armor.leggings.enchantments.durability", 12);
        defaultsMap.put("armor.leggings.enchantments.protection", 4);

        defaultsMap.put("armor.boots.material", "LEATHER");
        defaultsMap.put("armor.boots.enchantments.durability", 12);
        defaultsMap.put("armor.boots.enchantments.protection", 4);

        setConfigDefaults(defaultsMap);
    }

    @Override
    public void applyKit(Player p, int level) {
        p.getInventory().addItem(new ItemBuilder(
                Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
                .addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability")).build());

        p.getInventory().setHelmet(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.helmet.material").toUpperCase() + "_HELMET"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.helmet.enchantments.durability"))
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.helmet.enchantments.protection"))
                .setColour(Color.fromBGR(102, 0, 51)).build());

        p.getInventory().setChestplate(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.chestplate.material").toUpperCase() + "_CHESTPLATE"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.chestplate.enchantments.durability"))
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.chestplate.enchantments.protection"))
                .setColour(Color.fromBGR(153, 0, 75)).build());

        p.getInventory().setLeggings(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.leggings.material").toUpperCase() + "_LEGGINGS"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.leggings.enchantments.durability"))
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.leggings.enchantments.protection"))
                .setColour(Color.fromBGR(204, 0, 100)).build());

        p.getInventory().setBoots(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.boots.material").toUpperCase() + "_BOOTS"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.boots.enchantments.durability"))
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.boots.enchantments.protection"))
                .setColour(Color.fromBGR(255, 0, 127)).build());
    }

    @Override
    public void onMove(Player p) {
        particleMoveEffect(p, ParticleEffect.CRIT, 1, 30);

        if (UtilPlayer.isMoving(p) && !getKitManager().getKitPvP().getSpawnRegion().contains(p)) {
            UtilPlayer.getNearbyPlayers(p.getLocation(), 2).forEach(target -> {
                if (target != p)
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 15, 1));
            });
        }
    }

    @Override
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7Your particle aura will", "§7slow people around you", "§7when you're moving");
    }
}
