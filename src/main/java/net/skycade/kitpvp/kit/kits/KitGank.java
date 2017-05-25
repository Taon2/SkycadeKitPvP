package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class KitGank extends Kit {

	public KitGank(KitManager kitManager) {
		super(kitManager, "Gank", KitType.GANK, 35000, "Gank your enemies");
		setIcon(Material.STONE_SWORD);

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("inventory.sword.material", "STONE_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.enchantments.knockback", 1);
		defaultsMap.put("inventory.sword.enchantments.damage-all", 0);

		defaultsMap.put("potions.damage.amplifier", 0);
		defaultsMap.put("potions.resistance.amplifier", 0);
		defaultsMap.put("potions.speed.amplifier", 1);
		defaultsMap.put("potions.regeneration.amplifier", 0);

		setConfigDefaults(defaultsMap);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(
				Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
				.addEnchantment(Enchantment.KNOCKBACK, getConfig().getInt("inventory.sword.enchantments.knockback"))
				.addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

		p.addPotionEffect(new PotionEffect(
				PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, getConfig().getInt("potions.damage.amplifier")));

		p.addPotionEffect(new PotionEffect(
				PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, getConfig().getInt("potions.resistance.amplifier")));

		p.addPotionEffect(new PotionEffect(
				PotionEffectType.SPEED, Integer.MAX_VALUE, getConfig().getInt("potions.speed.amplifier")));

		p.addPotionEffect(new PotionEffect(
				PotionEffectType.REGENERATION, Integer.MAX_VALUE, getConfig().getInt("potions.regeneration.amplifier")));
	}
	
	@Override
	public void onMove(Player p) {
		particleTracerEffect(p, Color.GRAY, 20);
	}

}
