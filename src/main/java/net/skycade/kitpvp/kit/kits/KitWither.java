package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KitWither extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    public KitWither(KitManager kitManager) {
        super(kitManager, "Wither", KitType.WITHER, 36000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 11)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Explodes on death.")
                .setColour(Color.BLACK).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 11)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Explodes on death.")
                .setColour(Color.BLACK).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 11)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Explodes on death.")
                .setColour(Color.BLACK).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 11)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Explodes on death.")
                .setColour(Color.BLACK).build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.DAMAGE_ALL, 1)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Damaging players has a chance to give them withering.").build();

        ItemStack icon = new ItemStack(Material.NETHER_STAR);
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
        witherEffect(damagee);
    }

    @Override
    public boolean onDeath(Player died, Player killer) {
        // died.getLocation().getWorld().createExplosion(died.getLocation().getBlockX(), died.getLocation().getBlockY(), died.getLocation().getBlockZ(), 2F, false, false);
        for (Entity entity : died.getNearbyEntities(5, 5, 5)) {
            if (entity instanceof Player) {
                Player affected = (Player) entity;
                affected.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 10, 1));
                affected.playSound(affected.getLocation(), Sound.GHAST_SCREAM, 1, 1);
            }
        }
        return true;
    }

    private void witherEffect(Player p) {
        int random = UtilMath.getRandom(0, 100);
        if (random <= 12) {
            //For missions
            KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
            Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

            p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 120, 1));
        }
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "A powerful opponent.",
                "",
                ChatColor.GRAY + "Chance to give people wither effect.",
                ChatColor.GRAY + "On death you effect the players around you with",
                ChatColor.GRAY + "Wither 2."
        );
    }
}
