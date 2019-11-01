package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitSniper extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack bow;
    private ItemStack arrows;

    private int arrowCooldown = 1;
    private int arrowStartAmount = 1;

    private final Map<UUID, UUID> sniperPlayerHit = new HashMap<>();
    private final Map<UUID, Integer> sniperCombo = new HashMap<>();

    public KitSniper(KitManager kitManager) {
        super(kitManager, "Sniper", KitType.SNIPER, 38000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 9)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .setColour(Color.fromRGB(0, 60, 0)).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 9)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .setColour(Color.fromRGB(0, 60, 0)).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 9)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .setColour(Color.fromRGB(0, 60, 0)).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 9)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .setColour(Color.fromRGB(0, 60, 0)).build();
        weapon = new ItemBuilder(
                Material.WOOD_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 7)
                .addEnchantment(Enchantment.DAMAGE_ALL, 3).build();
        bow = new ItemBuilder(
                Material.BOW)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.ARROW_INFINITE, 1)
                .addEnchantment(Enchantment.ARROW_KNOCKBACK, 1)
                .addEnchantment(Enchantment.ARROW_DAMAGE, 3)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Fire 1 arrow every " + arrowCooldown + " second.")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Landing successive shots deal more damage.").build();
        arrows = new ItemBuilder(
                Material.ARROW, arrowStartAmount).build();

        ItemStack icon = new ItemStack(Material.GHAST_TEAR);
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
    }

    public void onArrowLaunch(Player shooter, ProjectileLaunchEvent e) {
        if (!addCooldown(shooter, "Bow", arrowCooldown, true)) {
            e.setCancelled(true);
        }
    }

    public void onArrowHit(Player shooter, Player damagee, EntityDamageByEntityEvent e) {
        if (UtilMath.getRandom(0, 100) < 3)
            damagee.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 90, 0));

        if (!sniperPlayerHit.containsKey(shooter.getUniqueId()))
            sniperPlayerHit.put(shooter.getUniqueId(), damagee.getUniqueId());

        if (damagee.getUniqueId().equals(sniperPlayerHit.get(shooter.getUniqueId()))) {
            if (sniperCombo.containsKey(shooter.getUniqueId()))
                sniperCombo.put(shooter.getUniqueId(), sniperCombo.get(shooter.getUniqueId()) + 1);
            else
                sniperCombo.put(shooter.getUniqueId(), 1);
            shooter.setLevel(sniperCombo.get(shooter.getUniqueId()));
            if (!hasArmor(damagee))
                return;
            int combo = sniperCombo.get(shooter.getUniqueId());
            double increasedDamage = 1 + (combo < 10 ? combo * 0.05 : 0.5);
            e.setDamage(e.getDamage() * increasedDamage);
        } else {
            sniperCombo.put(shooter.getUniqueId(), 1);
            sniperPlayerHit.put(shooter.getUniqueId(), damagee.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        sniperCombo.remove(e.getEntity().getUniqueId());
        sniperPlayerHit.remove(e.getEntity().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        sniperCombo.remove(e.getPlayer().getUniqueId());
        sniperPlayerHit.remove(e.getPlayer().getUniqueId());
    }

    private boolean hasArmor(Player p) {
        for (ItemStack item : p.getInventory().getArmorContents())
            if (item != null)
                if (item.getType() != Material.AIR)
                    return true;
        return false;
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.GOLD + "" + ChatColor.BOLD + "Ranged Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Requires steady aim.",
                "",
                ChatColor.GRAY + "Arrows do more damage if",
                ChatColor.GRAY + "you hit successive shots."
        );
    }
}
