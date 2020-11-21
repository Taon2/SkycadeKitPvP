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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KitBarbarian extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack ability;

    private int cooldown = 18;

    public KitBarbarian(KitManager kitManager) {
        super(kitManager, "Barbarian", KitType.BARBARIAN, 20000, getLore());

        helmet = new ItemBuilder(
                Material.IRON_HELMET).build();
        chestplate = new ItemBuilder(
                Material.IRON_CHESTPLATE).build();
        leggings = new ItemBuilder(
                Material.IRON_LEGGINGS).build();
        boots = new ItemBuilder(
                Material.IRON_BOOTS).build();
        weapon = new ItemBuilder(
                Material.IRON_AXE)
                .addEnchantment(Enchantment.DURABILITY, 5).build();

        ability = new ItemBuilder(
                Material.REDSTONE).setName(ChatColor.translateAlternateColorCodes('&', "&cBloodlust"))
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "%click% to activate " + ChatColor.DARK_RED +
                        "Bloodlust" + ChatColor.GRAY + "" + ChatColor.ITALIC + "!")
                .addLore(" ")
                .addLore(ChatColor.DARK_RED + "Bloodlust " + ChatColor.GRAY + "grants you Strength for 3 seconds.")
                .build();

        ItemStack icon = new ItemStack(Material.IRON_AXE);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(ability);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.REDSTONE)return;

        if (!addCooldown(p, "Bloodlust", cooldown, true))
            return;

        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 3, 0));
        p.getWorld().playEffect(p.getLocation(), Effect.FLAME, 1);
        p.getWorld().playSound(p.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 1, 1);
        shootParticlesFromLoc(p, ParticleEffect.FLAME, 500, 0.3F);
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