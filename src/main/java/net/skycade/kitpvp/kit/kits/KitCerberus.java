package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
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

public class KitCerberus extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    public KitCerberus(KitManager kitManager) {
        super(kitManager, "Cerberus", KitType.CERBERUS, 20000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .setColour(Color.ORANGE).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .setColour(Color.ORANGE).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .setColour(Color.ORANGE).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .setColour(Color.ORANGE).build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5).build();

        constantEffects.put(PotionEffectType.FIRE_RESISTANCE, 0);

        ItemStack icon = new ItemStack(Material.LAVA_BUCKET);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        constantEffects.forEach((effect, amplifier) -> {
            p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
        });
    }

    @Override
    public void onMove(Player p) {
        if (p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
            shootParticlesFromLoc(p, ParticleEffect.FIREWORKS_SPARK, 40, 0.1F);
        if (p.getLocation().getBlock().getType() == Material.LAVA || p.getLocation().getBlock().getType() == Material.STATIONARY_LAVA) {
            p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            p.removePotionEffect(PotionEffectType.SLOW);
            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40 * 3, 0));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40 * 3, 2));
        }
    }

    @Override
    public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
        if (shacoHit.contains(damagee.getUniqueId()))
            e.setDamage(1);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Enjoys the hot tub.",
                "",
                ChatColor.GRAY + "Becomes stronger when in lava."
        );
    }
}
