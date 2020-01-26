package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitDubstep extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    public KitDubstep(KitManager kitManager) {
        super(kitManager, "Dubstep", KitType.DUBSTEP, 0, getLore());

        helmet = new ItemBuilder(
                Material.IRON_HELMET)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Moving randomly grants you resistance.").build();
        chestplate = new ItemBuilder(
                Material.IRON_CHESTPLATE)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Moving randomly grants you resistance.").build();
        leggings = new ItemBuilder(
                Material.GOLD_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 7)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Moving randomly grants you resistance.").build();
        boots = new ItemBuilder(
                Material.GOLD_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 7)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Moving randomly grants you resistance.").build();
        weapon = new ItemBuilder(
                Material.DIAMOND_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.DAMAGE_ALL, 1).build();

        ItemStack icon = new ItemStack(Material.RECORD_4);
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

    public void onMove(Player p) {
        if (UtilMath.getRandom(0, 100) <= 5)
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 50, 3));
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "A default kit.");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.AQUA + "" + ChatColor.BOLD + "Defensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Drop the bass.",
                "",
                ChatColor.GRAY + "Randomly gains damage resistance."
        );
    }
}
