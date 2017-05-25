package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitTeleporter extends Kit {

	public KitTeleporter(KitManager kitManager) {
		super(kitManager, "Teleporter", KitType.TELEPORTER, 32000, "Where did he go?");
		setIcon(Material.ENDER_PEARL);

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("inventory.sword.material", "DIAMOND_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.enchantments.damage-all", 0);

		defaultsMap.put("inventory.ender-pearl.amount", 5);
		defaultsMap.put("inventory.ender-pearl.regen-speed", 30);
		defaultsMap.put("inventory.ender-pearl.max-amount", 8);

		defaultsMap.put("armor.helmet.material", "IRON");
		defaultsMap.put("armor.helmet.enchantments.protection", 0);

		defaultsMap.put("armor.chestplate.material", "CHAINMAIL");
		defaultsMap.put("armor.chestplate.enchantments.durability", 1);

		defaultsMap.put("armor.leggings.material", "CHAINMAIL");
		defaultsMap.put("armor.leggings.enchantments.durability", 1);

		defaultsMap.put("armor.boots.material", "IRON");

		setConfigDefaults(defaultsMap);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(
				Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
				.addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

		p.getInventory().addItem(new ItemBuilder(
				Material.ENDER_PEARL, getConfig().getInt("inventory.ender-pearl.amount")).build());

		p.getInventory().setHelmet(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.helmet.material").toUpperCase() + "_HELMET"))
				.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.helmet.enchantments.protection")).build());

		p.getInventory().setChestplate(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.chestplate.material").toUpperCase() + "_CHESTPLATE"))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.chestplate.enchantments.durability")).build());

		p.getInventory().setLeggings(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.leggings.material").toUpperCase() + "_LEGGINGS"))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.leggings.enchantments.durability")).build());

		p.getInventory().setBoots(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.boots.material").toUpperCase() + "_BOOTS")).build());

		startItemRunnable(p, getConfig().getInt("inventory.ender-pearl.regen-speed"), new ItemBuilder(
				Material.ENDER_PEARL).build(), getConfig().getInt("inventory.ender-pearl.max-amount"), KitType.TELEPORTER);
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Collections.singletonList("§7You will regain epearls overtime");
	}

}
