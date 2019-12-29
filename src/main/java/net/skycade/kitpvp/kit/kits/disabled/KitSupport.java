package net.skycade.kitpvp.kit.kits.disabled;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitSupport extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private int beaconCooldown = 30;

    public KitSupport(KitManager kitManager) {
        super(kitManager, "Support", KitType.SUPPORT, 17000, false, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.fromRGB(153, 255, 153)).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.fromRGB(153, 255, 153)).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.fromRGB(153, 255, 153)).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.fromRGB(153, 255, 153)).build();
        weapon = new ItemBuilder(
                Material.STONE_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Right clicking every " + beaconCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "gives players around you speed and resistance.").build();

        ItemStack icon = new ItemStack(Material.BEACON);
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
        damagee.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 0));
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.STONE_SWORD)
            return;
        if (!addCooldown(p, "Buff", beaconCooldown, true))
            return;

        Set<Player> targetPlayers = UtilPlayer.getNearbyPlayers(p, p.getLocation(), 5);
        if (targetPlayers.size() <= 1) {
            removeCooldowns(p, getName());
            return;
        }

        targetPlayers.forEach(target -> {
            target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1));
            target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 1));
        });
        p.getWorld().playSound(p.getLocation(), Sound.WOLF_HOWL, 1.0F, 1.0F);
        shootParticlesFromLoc(p, ParticleEffect.PORTAL, 500, 1);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Unobtainable.");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.GREEN + "" + ChatColor.BOLD + "Support Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "A team player.",
                "",
                ChatColor.GRAY + "Gives players speed and resistance."
        );
    }
}
