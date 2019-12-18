package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KitWarrior extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private double damageMultiplier = 1.4;

    public KitWarrior(KitManager kitManager) {
        super(kitManager, "Warrior", KitType.WARRIOR, 50000, getLore());

        helmet = new ItemBuilder(
                Material.DIAMOND_HELMET)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Damage you receive is multiplied by " + damageMultiplier + ".").build();
        chestplate = new ItemBuilder(
                Material.DIAMOND_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 1)
                .addEnchantment(Enchantment.THORNS, 1)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Damage you receive is multiplied by " + damageMultiplier + ".").build();
        leggings = new ItemBuilder(
                Material.DIAMOND_LEGGINGS)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Damage you receive is multiplied by " + damageMultiplier + ".").build();
        boots = new ItemBuilder(
                Material.DIAMOND_BOOTS)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Damage you receive is multiplied by " + damageMultiplier + ".").build();
        weapon = new ItemBuilder(
                Material.STONE_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.DAMAGE_ALL, 1).build();


        ItemStack icon = new ItemStack(Material.DIAMOND_CHESTPLATE);
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
    public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
        e.setDamage(e.getDamage() * damageMultiplier);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.AQUA + "" + ChatColor.BOLD + "Defensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "A softie on the inside!",
                "",
                ChatColor.GRAY + "Strong, but takes 1.4x damage when hit."
        );
    }
}
