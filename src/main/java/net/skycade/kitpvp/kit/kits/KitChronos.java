package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class KitChronos extends Kit {

    public KitChronos(KitManager kitManager) {
        super(kitManager, "Chronos", KitType.CHRONOS, 44000, "Time around him slows down");
        setIcon(new ItemStack(Material.WATCH));
    }

    @Override
    public void applyKit(Player p, int level) {
        p.getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DAMAGE_ALL, level - 1)
                .addEnchantment(Enchantment.DURABILITY, 5).build());

        p.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).setColour(Color.fromBGR(102, 0, 51)).build());
        p.getInventory()
                .setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).addEnchantment(Enchantment.DURABILITY, 12)
                        .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).setColour(Color.fromBGR(153, 0, 75))
                        .build());
        p.getInventory()
                .setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).addEnchantment(Enchantment.DURABILITY, 12)
                        .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).setColour(Color.fromBGR(204, 0, 100))
                        .build());
        p.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).setColour(Color.fromBGR(255, 0, 127)).build());
    }

    @Override
    public void onMove(Player p) {
        particleMoveEffect(p, ParticleEffect.CRIT, 1, 30);

        if (UtilPlayer.isMoving(p) && !getKitManager().getKitPvP().getSpawnRegion().contains(p)) {
            UtilPlayer.getNearbyPlayers(p.getLocation(), 2).forEach(target -> {
                if (target != p)
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, getLevel(p) * 15, 1));
            });
        }
    }

    @Override
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7Your particle aura will", "§7slow people around you", "§7when you're moving");
    }
}
