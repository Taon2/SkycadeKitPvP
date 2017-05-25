package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class KitTank extends Kit {

	public KitTank(KitManager kitManager) {
		super(kitManager, "Tank", KitType.TANK, 46000, "Slow but powerful");
		setIcon(Material.DIAMOND_HELMET);

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("inventory.sword.material", "IRON_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.enchantments.damage-all", 2);

		defaultsMap.put("armor.material", "DIAMOND");
		defaultsMap.put("armor.enchantments.durability", 0);
		defaultsMap.put("armor.enchantments.protection", 0);

		defaultsMap.put("armor.helmet.lore", "§FReceive 50% more damage.");

		defaultsMap.put("potions.slow.amplifier", 0);

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

		p.getInventory().setHelmet(new ItemBuilder(p.getInventory().getArmorContents()[3])
				.addLore(getConfig().getString("armor.helmet.lore")).build());

		p.addPotionEffect(new PotionEffect(
				PotionEffectType.SLOW, Integer.MAX_VALUE, getConfig().getInt("potions.slow.amplifier")));
	}

	@Override
	public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		e.setDamage(e.getDamage() * 1.5);
	}
	
}
