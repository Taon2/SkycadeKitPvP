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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static net.skycade.kitpvp.Messages.LOVE_U;

public class KitLover extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack rose;

    private int loveCooldown = 15;

    public KitLover(KitManager kitManager) {
        super(kitManager, "Lover", KitType.LOVER, 19000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.RED).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.RED).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.RED).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.RED).build();
        weapon = new ItemBuilder(
                Material.WOOD_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.DAMAGE_ALL, 2)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "%click% a player every " + loveCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "gives your target debuff effects.").build();
        rose = new ItemStack(
                Material.RED_ROSE);

        ItemStack icon = new ItemStack(Material.RED_ROSE);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(rose);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);
    }

    @Override
    public void onInteract(Player p, Player target, ItemStack item) {
        if (item.getType() != Material.RED_ROSE)
            return;
        if (!addCooldown(p, "Romance", loveCooldown, true))
            return;
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 3));
        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 0));

        ParticleEffect.HEART.display(0.5F, 0.5F, 0.5F, 1, 10, target.getLocation().add(0, 2, 0), 100);
        LOVE_U.msg(p);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Unobtainable.");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "<3",
                "",
                ChatColor.GRAY + "%click% with the flower",
                ChatColor.GRAY + "gives your target debuff potion effects."
        );
    }
}
