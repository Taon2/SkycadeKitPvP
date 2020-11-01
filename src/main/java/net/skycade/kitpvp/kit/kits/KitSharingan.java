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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class KitSharingan extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private int copyPotionEffectsCooldown = 20;
    private int copyPotionEffectsLength = 30;

    public KitSharingan(KitManager kitManager) {
        super(kitManager, "Sharingan", KitType.SHARINGAN, 40000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Copies potion effects when hit.")
                .setColour(Color.BLACK).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Copies potion effects when hit.")
                .setColour(Color.BLACK).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Copies potion effects when hit.")
                .setColour(Color.BLACK).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Copies potion effects when hit.")
                .setColour(Color.BLACK).build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5).build();

        ItemStack icon = new ItemStack(Material.EYE_OF_ENDER);
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
        Collection<PotionEffect> effects = damager.getActivePotionEffects();

        if (effects.size() < 1)
            return; // no effects, so don't copy effects

        if (!addCooldown(damagee, "Copy Potion Effects", copyPotionEffectsCooldown, true))
            return;

        effects.forEach((eff) -> {
            if (eff.getDuration() > 1200 * 3)
                damagee.addPotionEffect(new PotionEffect(eff.getType(), copyPotionEffectsLength * 20, eff.getAmplifier()));
            else
                damagee.addPotionEffect(eff);
        });

        if (damagee.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
            damagee.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);

        for (PotionEffectType effect : Arrays.asList(PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION, PotionEffectType.HARM, PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.WEAKNESS, PotionEffectType.WITHER))
            if (damagee.hasPotionEffect(effect))
                damagee.removePotionEffect(effect);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Giving you 'the look'.",
                "",
                ChatColor.GRAY + "Copies potion effects when hit."
        );
    }
}
