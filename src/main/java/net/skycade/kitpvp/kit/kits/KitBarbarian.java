package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitBarbarian extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    public KitBarbarian(KitManager kitManager) {
        super(kitManager, "Barbarian", KitType.BARBARIAN, 12000, getLore());

        helmet = new ItemBuilder(
                Material.IRON_HELMET)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Gains strength when hit by enemies.").build();
        chestplate = new ItemBuilder(
                Material.IRON_CHESTPLATE)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Gains strength when hit by enemies.").build();
        leggings = new ItemBuilder(
                Material.IRON_LEGGINGS)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Gains strength when hit by enemies.").build();
        boots = new ItemBuilder(
                Material.IRON_BOOTS)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Gains strength when hit by enemies.").build();
        weapon = new ItemBuilder(
                Material.IRON_AXE)
                .addEnchantment(Enchantment.DURABILITY, 5).build();

        ItemStack icon = new ItemStack(Material.IRON_AXE);
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
        if (UtilMath.getRandom(0, 150) <= 4) {
            if (damagee.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
                damagee.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            damagee.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0));
            damagee.getWorld().playEffect(damagee.getLocation(), Effect.FLAME, 1);
            damagee.getWorld().playSound(damagee.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 1, 1);
            shootParticlesFromLoc(damagee, ParticleEffect.FLAME, 500, 0.3F);
        }
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Full of bloodlust and rage.",
                "",
                ChatColor.GRAY + "A chance to gain strength when hit."
        );
    }
}