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

import java.util.Arrays;
import java.util.List;

public class KitHades extends Kit {

	public KitHades(KitManager kitManager) {
		super(kitManager, "Hades", KitType.HADES, 45000, "Hades has a burning aura around him");
		setIcon(Material.NETHERRACK);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level - 1).addEnchantment(Enchantment.FIRE_ASPECT, level == 3 ? 1 : 0).build());
		
		p.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).addEnchantment(Enchantment.DURABILITY, 12).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).setColour(Color.fromBGR(0, 0, 102)).build());
		p.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).addEnchantment(Enchantment.DURABILITY, 12).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level == 1 ? 3 : 4).setColour(Color.fromBGR(0, 0, 150)).build());
		p.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).addEnchantment(Enchantment.DURABILITY, 12).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).setColour( Color.fromBGR(0, 0, 200)).build());
		p.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).addEnchantment(Enchantment.DURABILITY, 12).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).setColour(Color.fromBGR(51, 51, 255)).build());
	}
	
	@Override
	public void onMove(Player p) {
		particleMoveEffect(p, ParticleEffect.FLAME, 1, 30);
		if (UtilPlayer.isMoving(p) && !getKitManager().getKitPvP().getSpawnRegion().contains(p)) {

            UtilPlayer.getNearbyPlayers(p.getLocation(), 2).forEach(target -> {
                if (target != p)
                    target.setFireTicks(3 * 20);
            });

        }
	}

	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Players around you will be set", "§7on fire if you're moving");
	}

}
