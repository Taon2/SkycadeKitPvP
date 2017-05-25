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

public class KitElite extends Kit {

	public KitElite(KitManager kitManager) {
		super(kitManager, "Elite", KitType.ELITE, 16000, "Speed is everything");
		setIcon(new ItemStack(Material.LEATHER_HELMET));

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("inventory.sword.material", "DIAMOND_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.enchantments.damage-all", 0);

		defaultsMap.put("armor.material", "LEATHER");
		defaultsMap.put("armor.enchantments.durability", 10);
		defaultsMap.put("armor.enchantments.protection", 3);

		defaultsMap.put("armor.helmet.material", "LEATHER");
		defaultsMap.put("armor.helmet.enchantments.protection", 2);
		defaultsMap.put("armor.helmet.enchantments.durability", 13);

		defaultsMap.put("potions.fast-digging.amplifier", 2);

		setConfigDefaults(defaultsMap);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(
				Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
				.addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());
		
		ItemStack[] armor = getArmour(
				Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
				getConfig().getInt("armor.enchantments.durability"),
				getConfig().getInt("armor.enchantments.protection"),
				Color.BLUE);

		armor[3] = new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.helmet.material").toUpperCase() + "_HELMET"))
				.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.helmet.enchantments.protection"))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.helmet.enchantments. durability"))
				.setColour(Color.WHITE).build();

		p.getInventory().setArmorContents(armor);
		
		p.addPotionEffect(new PotionEffect(
				PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, getConfig().getInt("potions.fast-digging.amplifier")));
	}

}
