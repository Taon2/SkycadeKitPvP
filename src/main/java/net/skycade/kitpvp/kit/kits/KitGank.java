package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
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

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class KitGank extends Kit {

	public KitGank(KitManager kitManager) {
		super(kitManager, "Gank", KitType.GANK, 35000, "Gank your enemies");

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("kit.icon.material", "STONE_SWORD");
		defaultsMap.put("kit.icon.color", "BLACK");
		defaultsMap.put("kit.price", 35000);

		defaultsMap.put("inventory.sword.material", "STONE_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.enchantments.knockback", 1);
		defaultsMap.put("inventory.sword.enchantments.damage-all", 0);

		defaultsMap.put("potions.pot1", "INCREASE_DAMAGE:0");
		defaultsMap.put("potions.pot2", "DAMAGE_RESISTANCE:0");
		defaultsMap.put("potions.pot3", "SPEED:1");
		defaultsMap.put("potions.pot4", "REGENERATION:0");

		setConfigDefaults(defaultsMap);

		if (getConfig().getString("kit.icon.material") != null) {
			if (getConfig().getString("kit.icon.material").contains("LEATHER")) {
				setIcon(new ItemBuilder(Material.getMaterial(getConfig().getString("kit.icon.material").toUpperCase()))
						.setColour(getColor(getConfig().getString("kit.icon.color"))).build());
			} else {
				setIcon(new ItemStack(Material.getMaterial(getConfig().getString("kit.icon.material").toUpperCase())));
			}
		} else {
			setIcon(new ItemStack(Material.DIRT));
		}
		setPrice(getConfig().getInt("kit.price"));
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(
				Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
				.addEnchantment(Enchantment.KNOCKBACK, getConfig().getInt("inventory.sword.enchantments.knockback"))
				.addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

		String[] pot1 = getConfig().getString("potions.pot1").split(":");
		p.addPotionEffect(new PotionEffect(
				PotionEffectType.getByName(pot1[0]),
				Integer.MAX_VALUE,
				parseInt(pot1[1])));

		String[] pot2 = getConfig().getString("potions.pot2").split(":");
		p.addPotionEffect(new PotionEffect(
				PotionEffectType.getByName(pot2[0]),
				Integer.MAX_VALUE,
				parseInt(pot2[1])));

		String[] pot3 = getConfig().getString("potions.pot3").split(":");
		p.addPotionEffect(new PotionEffect(
				PotionEffectType.getByName(pot3[0]),
				Integer.MAX_VALUE,
				parseInt(pot3[1])));

		String[] pot4 = getConfig().getString("potions.pot4").split(":");
		p.addPotionEffect(new PotionEffect(
				PotionEffectType.getByName(pot4[0]),
				Integer.MAX_VALUE,
				parseInt(pot4[1])));
	}
	
	@Override
	public void onMove(Player p) {
		particleTracerEffect(p, Color.GRAY, 20);
	}

}
