package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitGank extends Kit {

    private ItemStack weapon;
    private ItemStack boots;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    public KitGank(KitManager kitManager) {
        super(kitManager, "Gank", KitType.GANK, 50000, getLore());

        weapon = new ItemBuilder(
                Material.STONE_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 6)
                .addEnchantment(Enchantment.DAMAGE_ALL, 2).build();

        constantEffects.put(PotionEffectType.DAMAGE_RESISTANCE, 1);
        constantEffects.put(PotionEffectType.SPEED, 1);
        constantEffects.put(PotionEffectType.REGENERATION, 1);

        ItemStack icon = new ItemStack(Material.STONE_SWORD);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().setBoots(boots);

        constantEffects.forEach((effect, amplifier) -> {
            p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
        });
    }

    @Override
    public void onMove(Player p) {
        particleTracerEffect(p, Color.GRAY, 5);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Strafe's little brother. RIP Strafe.",
                "",
                ChatColor.GRAY + "Lots of potion effects,",
                ChatColor.GRAY + "but no armor."
        );
    }
}
