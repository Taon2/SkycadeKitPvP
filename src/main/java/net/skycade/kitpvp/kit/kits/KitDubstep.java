package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class KitDubstep extends Kit {

	public KitDubstep(KitManager kitManager) {
		super(kitManager, "Dubstep", KitType.DUBSTEP, 7000, "Woop Woop Woop Womp!");
		setIcon(new ItemStack(Material.GLOWSTONE));

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("inventory.sword.material", "DIAMOND_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.enchantments.damage-all", 1);

		defaultsMap.put("armor.material", "IRON");
		defaultsMap.put("armor.enchantments.durability", 0);
		defaultsMap.put("armor.enchantments.protection", 0);

		defaultsMap.put("potions.slow-digging.amplifier", 3);

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

		p.addPotionEffect(new PotionEffect(
				PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, getConfig().getInt("potions.slow-digging.amplifier")));
	}
	
	public void onMove(Player p) {
		int level = getLevel(p);
		if (UtilMath.getRandom(0, 100) <= 5)
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 3));
	}

}
