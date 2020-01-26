package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitElite extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    public KitElite(KitManager kitManager) {
        super(kitManager, "Elite", KitType.ELITE, 12000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 14)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Moving randomly grants you strength.")
                .setColour(Color.WHITE).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Moving randomly grants you strength.")
                .setColour(Color.BLUE).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Moving randomly grants you strength.")
                .setColour(Color.BLUE).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Moving randomly grants you strength.")
                .setColour(Color.BLUE).build();
        weapon = new ItemBuilder(
                Material.DIAMOND_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5).build();

        ItemStack icon = new ItemBuilder(
                Material.LEATHER_HELMET)
                .setColour(Color.WHITE).build();
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
    public boolean onDeath(Player died, Player killer) {
        if (killer != null) {
            if (killer.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
                killer.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            if (killer.hasPotionEffect(PotionEffectType.REGENERATION))
                killer.removePotionEffect(PotionEffectType.REGENERATION);

            killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, 0));
            killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1));
        }

        return true;
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Quite experienced.",
                "",
                ChatColor.GRAY + "Gains strength and regeneration when you kill a player."
        );
    }
}
