package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;

public class KitWarrior extends Kit {

	public KitWarrior(KitManager kitManager) {
		super(kitManager, "Warrior", KitType.WARRIOR, 50000, "This warrior can survive a hit!");
		setIcon(Material.DIAMOND_CHESTPLATE);

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("inventory.sword.material", "STONE_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.enchantments.knockback", 1);
		defaultsMap.put("inventory.sword.enchantments.damage-all", 1);

		defaultsMap.put("armor.material", "DIAMOND");
		defaultsMap.put("armor.enchantments.durability", 0);
		defaultsMap.put("armor.enchantments.protection", 0);

		defaultsMap.put("armor.chestplate.enchantments.thorns", 1);
		defaultsMap.put("armor.chestplate.enchantments.durability", 1);

		defaultsMap.put("armor.helmet.lore", "§FDamage you receive is multiplied by");

		setConfigDefaults(defaultsMap);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(
				Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
				.addEnchantment(Enchantment.KNOCKBACK, getConfig().getInt("inventory.sword.enchantments.knockback"))
				.addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

		p.getInventory().setArmorContents(getArmour(
				Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
				getConfig().getInt("armor.enchantments.durability"),
				getConfig().getInt("armor.enchantments.protection")));

		p.getInventory().getArmorContents()[2]
				.addEnchantment(Enchantment.THORNS, getConfig().getInt("armor.chestplate.enchantments.thorns"));

		p.getInventory().getArmorContents()[2]
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.chestplate.enchantments.durability"));

		p.getInventory().setHelmet(new ItemBuilder(p.getInventory().getArmorContents()[3])
				.addLore(getConfig().getString("armor.helmet.lore") + " " + getDamageMultiplier(level)).build());
	}
	
	@Override
	public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		e.setDamage(e.getDamage() * getDamageMultiplier(3));
	}
	
	private double getDamageMultiplier(int level) {
		/* if (level == 1)
			return 2;
		else if (level == 2)
			return 1.7;
		else */
			return 1.4;
	}

}
