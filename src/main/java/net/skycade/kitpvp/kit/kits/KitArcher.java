package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static net.skycade.kitpvp.Messages.*;

public class KitArcher extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack bow;
    private ItemStack arrows;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    private int arrowCooldown = 1;
    private int arrowRegenSpeed = 3;
    private int arrowStartAmount = 16;
    private int arrowMaxAmount = 16;

    private List<Arrow> arrowList = new ArrayList<>();

    public KitArcher(KitManager kitManager) {
        super(kitManager, "Archer", KitType.ARCHER, 0, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        weapon = new ItemBuilder(
                Material.STONE_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5).build();
        bow = new ItemBuilder(
                Material.BOW)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.ARROW_DAMAGE, 2)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Fire 1 arrow every " + arrowCooldown + " second.").build();
        arrows = new ItemBuilder(
                Material.ARROW, arrowStartAmount)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Regain 1 arrow every " + arrowRegenSpeed + " seconds.").build();

        constantEffects.put(PotionEffectType.SPEED, 1);

        ItemStack icon = new ItemStack(Material.BOW);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(bow);
        p.getInventory().setItem(27, arrows);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        constantEffects.forEach((effect, amplifier) -> {
            p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
        });

        startItemRunnable(p, arrowRegenSpeed, getArrows(1), arrowMaxAmount, KitType.ARCHER);
    }

    public void onArrowLaunch(Player shooter, ProjectileLaunchEvent e) {
        if (!addCooldown(shooter, "Bow", arrowCooldown, true)) {
            e.setCancelled(true);
        }

        e.getEntity().setCustomName(shooter.getName());
        e.getEntity().setCustomNameVisible(false);
        arrowList.add((Arrow) e.getEntity());
    }

    public void onArrowHit(Player shooter, Player damagee, EntityDamageByEntityEvent e) {
        archerChanceEffects(shooter, damagee, e, 60, 50, 30,20);
    }

    private void archerChanceEffects(Player archer, Player target, EntityDamageByEntityEvent e, int regainHealth, int doubleDamage, int slowEffect, int blindEffect) {
        int randomNumber = UtilMath.getRandom(0, 700);
        if (randomNumber <= regainHealth) {
            archer.setHealth(archer.getMaxHealth());
            HEALTH_BOOST.msg(archer);
        } else if (randomNumber <= regainHealth + doubleDamage) {
            DOUBLE_DAMAGE.msg(archer);
            e.setDamage(e.getDamage() * 2);
            DOUBLE_DAMAGE_YOU.msg(target, "%player%", archer.getName());
        } else if (randomNumber <= regainHealth + doubleDamage + slowEffect) {
            TARGET_SLOWED.msg(archer);
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 0));
            YOURE_SLOWED.msg(target, "%player%", archer.getName());
        } else if (randomNumber <= regainHealth + doubleDamage + slowEffect + blindEffect) {
            TARGET_BLINDED.msg(archer);
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 6 * 20, 0));
            YOURE_BLINDED.msg(target, "%player%", archer.getName());
        }
    }

    private ItemStack getArrows(int amount) {
        ItemStack arrowRegen = new ItemStack(arrows);
        arrowRegen.setAmount(amount);

        return arrowRegen;
    }

    public void removeSummon(int seconds, Player p) {
        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> {
            for (Arrow arrow : arrowList)
                if (arrow.getCustomName().contains(p.getName())) {
                    arrow.remove();
                }
        }, seconds * 20);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "A default kit.");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.GOLD + "" + ChatColor.BOLD + "Ranged Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Aim for the knees!",
                "",
                ChatColor.GRAY + "Arrows have a chance to",
                ChatColor.GRAY + "give effects to your target."
        );
    }
}