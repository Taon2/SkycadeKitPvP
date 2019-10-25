package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitMultishot extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack bow;
    private ItemStack arrows;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    private int arrowRegenSpeed = 12;
    private int arrowStartAmount = 4;
    private int arrowMaxAmount = 6;

    private List<UUID> running = new ArrayList<>();

    public KitMultishot(KitManager kitManager) {
        super(kitManager, "Multishot", KitType.MULTISHOT, 50000, getLore());

        helmet = new ItemBuilder(
                Material.CHAINMAIL_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 6)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 6)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 6)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 6)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build();
        weapon = new ItemBuilder(
                Material.STONE_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.DAMAGE_ALL, 1).build();
        bow = new ItemBuilder(
                Material.BOW)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.ARROW_DAMAGE, 1).build();
        arrows = new ItemBuilder(
                Material.ARROW, arrowStartAmount)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Regain 1 arrow every " + arrowRegenSpeed + " seconds.").build();

        constantEffects.put(PotionEffectType.SPEED, 0);

        ItemStack icon = new ItemStack(Material.ARROW);
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

        startItemRunnable(p, arrowRegenSpeed, getArrows(1), arrowMaxAmount, KitType.MULTISHOT);
    }

    public void onArrowLaunch(Player shooter, ProjectileLaunchEvent e) {
        if (running.contains(shooter.getUniqueId())) {
            return;
        }
        if (!addCooldown(shooter, "bow", 2, true)) {
            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);
        running.add(shooter.getUniqueId());
        double initialArrowAngle = 70;
        double arrowAngle = initialArrowAngle;
        for (int i = 0; i < 5; i++) {
            double totalAngle = shooter.getLocation().getYaw() + arrowAngle;
            double arrowDirX = Math.cos(Math.toRadians(totalAngle));
            double arrowDirZ = Math.sin(Math.toRadians(totalAngle));
            org.bukkit.util.Vector arrowDir = e.getEntity().getVelocity();
            arrowDir.setX(arrowDirX);
            arrowDir.setY(shooter.getLocation().getDirection().getY());
            arrowDir.setZ(arrowDirZ);
            arrowDir.multiply(e.getEntity().getVelocity().length());

            shooter.launchProjectile(Arrow.class, arrowDir);
            arrowAngle += ((90 - initialArrowAngle)/2);
        }

        running.remove(shooter.getUniqueId());
    }

    private ItemStack getArrows(int amount) {
        ItemStack arrowRegen = new ItemStack(arrows);
        arrowRegen.setAmount(amount);

        return arrowRegen;
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.GOLD + "" + ChatColor.BOLD + "Ranged Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "No aim necessary.",
                "",
                ChatColor.GRAY + "Shooting an arrow fires 4 additional",
                ChatColor.GRAY + "arrows spread out in front of you."
        );
    }
}