package net.skycade.kitpvp.kit.kits.disabled;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
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

public class KitBladeMaster extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack weapon2;

    public KitBladeMaster(KitManager kitManager) {
        super(kitManager, "BladeMaster", KitType.BLADEMASTER, 29000, false, getLore());

        helmet = new ItemBuilder(
                Material.GOLD_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 2).build();
        chestplate = new ItemBuilder(
                Material.IRON_CHESTPLATE).build();
        leggings = new ItemBuilder(
                Material.IRON_LEGGINGS).build();
        boots = new ItemBuilder(
                Material.IRON_BOOTS).build();
        weapon = new ItemBuilder(
                Material.GOLD_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.DAMAGE_ALL, 3)
                .setName(ChatColor.RED + "Sword of Damage")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Deals heavy damage to enemies.").build();
        weapon2 = new ItemBuilder(
                Material.GOLD_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.KNOCKBACK, 2)
                .setName(ChatColor.DARK_AQUA + "Sword of Knockback")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Throws your enemies away from you.").build();

        ItemStack icon = new ItemStack(Material.GOLD_SWORD);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(weapon2);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);
    }

    @Override
    public void onMove(Player p) {
        if (p.isSneaking()) {
            if (p.hasPotionEffect(PotionEffectType.SPEED))
                p.removePotionEffect(PotionEffectType.SPEED);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 75, 0));
        }
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Studied the blade.",
                "",
                ChatColor.GRAY + "Uses two swords to defeat your enemies.",
                ChatColor.GRAY + "Gains speed when sneaking."
        );
    }
}
