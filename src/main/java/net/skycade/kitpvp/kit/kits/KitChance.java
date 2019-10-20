package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static net.skycade.kitpvp.Messages.*;

public class KitChance extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    public KitChance(KitManager kitManager) {
        super(kitManager, "Chance", KitType.CHANCE, 17000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .setColour(Color.YELLOW).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .setColour(Color.ORANGE).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .setColour(Color.ORANGE).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .setColour(Color.ORANGE).build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.DAMAGE_ALL, 1).build();

        ItemStack icon = new ItemStack(Material.NETHER_STAR);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);
    }

    @Override
    public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
        chanceEffect(6, 5, 2, 1, 4, 2, damager, e);
    }

    private void chanceEffect(int healthPer, int doublePer, int soupPer, int swingDownPer, int swingUpPer, int backPer, Player damager, EntityDamageByEntityEvent e) {
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
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "A default kit.");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Test your luck.",
                "",
                ChatColor.GRAY + "Gives you random buffs."
        );
    }
}
