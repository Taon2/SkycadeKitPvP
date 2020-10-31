package net.skycade.kitpvp.kit.kits;

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static net.skycade.kitpvp.Messages.LOVE_U;

public class KitHades extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack lighter;

    private int abilityCooldown = 10;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    public KitHades(KitManager kitManager) {
        super(kitManager, "Hades", KitType.HADES, 45000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 13)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.fromBGR(0, 0, 102)).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 13)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .setColour(Color.fromBGR(0, 0, 150)).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 13)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.fromBGR(0, 0, 200)).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 13)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.fromBGR(51, 51, 255)).build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5).build();

        lighter = new ItemBuilder(Material.FLINT_AND_STEEL)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .setName(ChatColor.RED + "Lighter").addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Right-Click a player to set them on fire!").build();

        constantEffects.put(PotionEffectType.FIRE_RESISTANCE, 1);

        ItemStack icon = new ItemStack(Material.NETHERRACK);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(lighter);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        constantEffects.forEach((effect, amplifier) -> {
            p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
        });
    }

    /*@Override
    public void onMove(Player p) {
        particleMoveEffect(p, ParticleEffect.FLAME, 1, 30);
        if (UtilPlayer.isMoving(p) && !getKitManager().getKitPvP().getSpawnRegion().contains(p)) {

            UtilPlayer.getNearbyPlayers(p, p.getLocation(), 2).forEach(target -> {
                target.setFireTicks(3 * 20);
            });
        }
    }

     */

    @Override
    public void onInteract(Player p, Player target, ItemStack item) {
        if (item.getType() != Material.FLINT_AND_STEEL)
            return;
        if (!addCooldown(p, "Lighter", abilityCooldown, true))
            return;

        target.setFireTicks(20 * 8);

        p.playSound(p.getLocation(), Sound.FIRE_IGNITE, 2.0F, 1.0F);
        target.playSound(target.getLocation(), Sound.FIRE_IGNITE, 2.0F, 1.0F);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "God of the underworld.",
                "",
                ChatColor.GRAY + "Players around you are set aflame."
        );
    }
}
