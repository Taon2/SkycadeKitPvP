package net.skycade.kitpvp.kit.kits.disabled;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static net.skycade.kitpvp.Messages.BIT_BY;
import static net.skycade.kitpvp.Messages.HEALED;

public class KitVampire extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private int healCooldown = 30;

    public KitVampire(KitManager kitManager) {
        super(kitManager, "Vampire", KitType.VAMPIRE, 25000, false, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .setColour(Color.BLACK).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .setColour(Color.RED).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .setColour(Color.RED).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .setColour(Color.RED).build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "%click% every " + healCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "and being near bleeding players heals you.")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Damaging players makes them bleed.").build();

        ItemStack icon = new ItemStack(Material.REDSTONE);
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
    public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
        int random = UtilMath.getRandom(0, 100);
        int chance = 7;

        if (random < chance) {
            double healAmount = 2.5;
            if (damager.getHealth() + healAmount < damager.getMaxHealth())
                damager.setHealth(damager.getHealth() + healAmount);
            else if (damager.getHealth() == damager.getMaxHealth())
                damager.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 60, 0));
            else
                damager.setHealth(damager.getMaxHealth());
            HEALED.msg(damager);

        } else if (random < chance * 2) {
            damagee.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80 + 20 * 3 - 1, 0));
            BIT_BY.msg(damagee, "%player%", damager.getName());
        }
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.IRON_SWORD)
            return;
        if (!addCooldown(p, "Suck Blood", healCooldown, true))
            return;

        Set<Player> targetPlayers = UtilPlayer.getNearbyPlayers(p, p.getLocation(), 4);
        if (targetPlayers.size() <= 1) {
            removeCooldowns(p, getName());
            return;
        }

        double healAmount = 0.0;
        for (Player target : targetPlayers) {
            startBleed(p, target, 7);
            healAmount += 4;
        }

        if (healAmount > 0) {
            if (p.getHealth() + healAmount > p.getMaxHealth()) {
                healAmount -= (p.getMaxHealth() - p.getHealth());
                if (healAmount > 0)
                    p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, (int) (20 * Math.floor(healAmount)), 0));
            } else
                p.setHealth(p.getHealth() + healAmount);
            HEALED.msg(p);
        }
    }

    @SuppressWarnings("deprecation")
    private void startBleed(Player vampire, Player p, int seconds) {
        ParticleEffect.REDSTONE.display(0.3F, 0.3F, 0.3F, 0, 5, p.getEyeLocation(), 40);

        if (seconds > 0)
            Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> {
                if (getKitManager().getKitPvP().getSpawnRegion().contains(p))
                    return;
                p.setLastDamageCause(new EntityDamageByEntityEvent(vampire, p, DamageCause.ENTITY_ATTACK, 6));
                p.damage(4);
                startBleed(vampire, p, seconds - 1);
            }, 15);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Unobtainable.");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Dracula!",
                "",
                ChatColor.GRAY + "Makes players bleed.",
                ChatColor.GRAY + "Bleeding players heal you."
        );
    }
}
