package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitTank extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    private double damageMultiplier = 1.5;

    public KitTank(KitManager kitManager) {
        super(kitManager, "Tank", KitType.TANK, 46000, getLore());

        helmet = new ItemBuilder(
                Material.DIAMOND_HELMET)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Receive 50% more damage.").build();
        chestplate = new ItemBuilder(
                Material.DIAMOND_CHESTPLATE).build();
        leggings = new ItemBuilder(
                Material.DIAMOND_LEGGINGS).build();
        boots = new ItemBuilder(
                Material.DIAMOND_BOOTS).build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.DAMAGE_ALL, 1).build();

        constantEffects.put(PotionEffectType.SLOW, 0);

        ItemStack icon = new ItemStack(Material.DIAMOND_HELMET);
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
    public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
        e.setDamage(e.getDamage() * damageMultiplier);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.AQUA + "" + ChatColor.BOLD + "Defensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "A true brute force.",
                "",
                ChatColor.GRAY + "Slow, but powerful.",
                ChatColor.GRAY + "Takes 50% more damage when hit."
        );
    }
}
