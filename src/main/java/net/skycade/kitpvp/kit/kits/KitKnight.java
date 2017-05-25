package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitKnight extends Kit {

	public KitKnight(KitManager kitManager) {
		super(kitManager, "Knight", KitType.KNIGHT, 26000, "Loyal to his king");
		setIcon(Material.CHAINMAIL_CHESTPLATE);

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("inventory.sword.material", "DIAMOND_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.enchantments.damage-all", 0);

		defaultsMap.put("armor.material", "CHAINMAIL");
		defaultsMap.put("armor.enchantments.durability", 5);
		defaultsMap.put("armor.enchantments.protection", 1);

		defaultsMap.put("armor.boots.enchantments.protection", 2);
		defaultsMap.put("armor.leggings.enchantments.protection", 2);

		defaultsMap.put("potions.night-vision.amplifier", 0);

		setConfigDefaults(defaultsMap);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(
				Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
				.addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

		p.getInventory().setArmorContents(getArmour(
				Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
				getConfig().getInt("armor.enchantments.durability"),
				getConfig().getInt("armor.enchantments.protection")));

		//boots
		p.getInventory().getArmorContents()[0].removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
		p.getInventory().getArmorContents()[0]
				.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.boots.enchantments.protection"));

		//leggings
		p.getInventory().getArmorContents()[1].removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
		p.getInventory().getArmorContents()[1]
				.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.leggings.enchantments.protection"));

		p.addPotionEffect(new PotionEffect(
				PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, getConfig().getInt("potions.night-vision.amplifier")));
	}
	
	@Override
	public void onMove(Player p) {
		for (Player target : UtilPlayer.getNearbyPlayers(p.getLocation(), 6)) {
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
