package net.skycade.kitpvp.kit.kits.disabled;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitNinja extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    private int dashCooldown = 60;

    public KitNinja(KitManager kitManager) {
        super(kitManager, "Ninja", KitType.NINJA, 32000, false, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 8)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.BLACK).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 8)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.BLACK).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 8)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.BLACK).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 8)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.BLACK).build();
        weapon = new ItemBuilder(
                Material.STONE_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.DAMAGE_ALL, 3)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Right clicking every " + dashCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "lets you dash around and have extra strength.").build();

        constantEffects.put(PotionEffectType.SPEED, 1);

        ItemStack icon = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .setColour(Color.BLACK).build();
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        constantEffects.forEach((effect, amplifier) -> {
            p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
        });
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.STONE_SWORD)
            return;
        if (!addCooldown(p, "Dash", dashCooldown, false))
            return;
        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0));
        tpDash(p, 6);
    }

    private void tpDash(Player p, int range) {
        final Location playerLoc = p.getLocation();
        double nX;
        double nZ;
        float nang = playerLoc.getYaw() + 90;
        if (nang < 0)
            nang += 360;
        nX = Math.cos(Math.toRadians(nang)) * range;
        nZ = Math.sin(Math.toRadians(nang)) * range;

        Location newLoc = new Location(playerLoc.getWorld(), playerLoc.getX() + nX, playerLoc.getY(), playerLoc.getZ() + nZ, playerLoc.getYaw(), playerLoc.getPitch());
        if (!isValidBlock(newLoc.getBlock().getType()) || (newLoc.getBlock().getType() != Material.AIR && newLoc.add(0, 1, 0).getBlock().getType() != Material.AIR && newLoc.add(0, 2, 0).getBlock().getType() != Material.AIR)) {
            if (range <= 2) {
                return;
            } else
                tpDash(p, range - 1);
        }
        p.teleport(newLoc);
        p.getWorld().playEffect(p.getLocation(), Effect.ENDER_SIGNAL, 1);
    }

    @Override
    public void onMove(Player p) {
        particleTracerEffect(p, Color.PURPLE, 30);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Unobtainable.");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Sneaky!",
                "",
                ChatColor.GRAY + "Your sword lets you dash around.",
                ChatColor.GRAY + "Dashing also gives you strength."
        );
    }
}
