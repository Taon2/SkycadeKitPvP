package me.bukkit.kitpvp.kit.kits;

import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.coreclasses.utils.ParticleEffect;
import me.bukkit.kitpvp.coreclasses.utils.UtilPlayer;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KitKnight extends Kit {

	public KitKnight(KitManager kitManager) {
		super(kitManager, "Knight", KitType.KNIGHT, 26000, "Loyal to his king");
		setIcon(Material.CHAINMAIL_CHESTPLATE);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.DIAMOND_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level == 3 ? 1 : 0).build());
		p.getInventory().setArmorContents(getArmour(Material.CHAINMAIL_HELMET, 5, level));
		if (level == 1) {
			p.getInventory().getArmorContents()[0].removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
			p.getInventory().getArmorContents()[0].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			p.getInventory().getArmorContents()[1].removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
			p.getInventory().getArmorContents()[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		}
		p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
	}
	
	@Override
	public void onMove(Player p) {
		for (Player target : UtilPlayer.getNearbyPlayers(p.getLocation(), 3 + getLevel(p))) {
			if (getKitManager().getKitPvP().getStats(target).getActiveKit() == KitType.GHOST) {
				Location location = target.getLocation();
				for (int i = 0; i < 30; i++) {
					double angle, x, z;
					angle = 2 * Math.PI * i / 30;
					x = Math.cos(angle) * 1;
					z = Math.sin(angle) * 1;
					location.add(x, 0, z);
					ParticleEffect.VILLAGER_HAPPY.display(0.03F, 0.02F, 0.03F, 0.05F, 1, location, Collections.singletonList(p));
					location.subtract(x, 0, z);
				}
			}
		}
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Ghosts around you will get", "§7a particle effect");
	}
}
