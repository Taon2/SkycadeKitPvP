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

import java.util.*;

public class KitDefault extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    public KitDefault(KitManager kitManager) {
        super(kitManager, "Default", KitType.DEFAULT, 5000, false, getLore());

        helmet = new ItemBuilder(
                Material.IRON_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        chestplate = new ItemBuilder(
                Material.IRON_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        boots = new ItemBuilder(
                Material.IRON_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        weapon = new ItemBuilder(
                Material.DIAMOND_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5).build();

        ItemStack icon = new ItemStack(Material.DIAMOND_SWORD);
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
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Unobtainable.");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Bog-standard.",
                "",
                ChatColor.GRAY + "A basic kit."
        );
    }
}
