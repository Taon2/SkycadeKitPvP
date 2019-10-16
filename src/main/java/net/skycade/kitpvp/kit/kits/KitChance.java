package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.skycade.kitpvp.Messages.*;

public class KitChance extends Kit {

    public KitChance(KitManager kitManager) {
        super(kitManager, "Chance", KitType.CHANCE, 17000, "How lucky are you?");

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "NETHER_BRICK_ITEM");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 17000);

        defaultsMap.put("inventory.sword.material", "IRON_SWORD");
        defaultsMap.put("inventory.sword.enchantments.durability", 5);
        defaultsMap.put("inventory.sword.enchantments.damage-all", 1);

        defaultsMap.put("armor.material", "LEATHER");
        defaultsMap.put("armor.durability", 12);
        defaultsMap.put("armor.protection", 2);

        defaultsMap.put("armor.helmet.material", "LEATHER");
        defaultsMap.put("armor.helmet.enchantments.durability", 10);
        defaultsMap.put("armor.helmet.enchantments.protection", 1);

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

        ItemStack[] armor = getArmour(
                Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
                getConfig().getInt("armor.durability"),
                getConfig().getInt("armor.protection"),
                Color.ORANGE);

        armor[3] = new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.helmet.material").toUpperCase() + "_HELMET"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.helmet.enchantments.durability"))
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.helmet.enchantments.protection"))
                .setColour(Color.YELLOW).build();

        p.getInventory().setArmorContents(armor);
    }

    @Override
    public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
        chanceEffect(6, 5, 2, 1, 4, 2, damager, damagee, e);
    }

    private void chanceEffect(int healthPer, int doublePer, int soupPer, int swingDownPer, int swingUpPer, int backPer, Player damager, Player damagee, EntityDamageByEntityEvent e) {
        int random = UtilMath.getRandom(0, 100);

        if (random <= healthPer) {
            damager.setHealth(damager.getMaxHealth());
            HEALTH_BOOST.msg(damager);
        } else if (random <= healthPer + doublePer) {
            e.setDamage(e.getDamage() * 2);
            DOUBLE_DAMAGE.msg(damager);
        } else if (random <= healthPer + doublePer + soupPer) {
            giveSoup(damager, 5);
            SOUP_REFILL.msg(damager);
        } else if (random <= healthPer + doublePer + soupPer + swingDownPer) {
            damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 200, 1));
            SWING_SPEED_DOWN.msg(damager);
        } else if (random <= healthPer + doublePer + soupPer + swingDownPer + swingUpPer) {
            damager.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 200, 1));
            SWING_SPEED_UP.msg(damager);
        } else if (random <= healthPer + doublePer + soupPer + swingDownPer + swingUpPer + backPer) {
            damager.damage(e.getDamage());
            BACKFIRE.msg(damager);
            e.setCancelled(true);
        }

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(damager, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);
    }

    @Override
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7Your sword can give your target", "§7different effects.");
    }

}
