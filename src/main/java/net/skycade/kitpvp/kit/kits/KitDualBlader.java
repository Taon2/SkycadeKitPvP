package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitDualBlader extends Kit {

	public KitDualBlader(KitManager kitManager) {
		super(kitManager, "DualBlader", KitType.DUALBLADER, 34000, "Use the sword of fire and ice");
		setIcon(new ItemStack(Material.PACKED_ICE));

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("inventory.sword.material", "IRON_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.name", "§cSword of fire");

		defaultsMap.put("inventory.sword2.material", "IRON_SWORD");
		defaultsMap.put("inventory.sword2.enchantments.durability", 5);
		defaultsMap.put("inventory.sword2.name", "§bSword of ice");

		defaultsMap.put("armor.material", "IRON");
		defaultsMap.put("armor.enchantments.durability", 1);
		defaultsMap.put("armor.enchantments.protection", 0);

		setConfigDefaults(defaultsMap);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(
				Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
				.setName(getConfig().getString("inventory.sword.name")).build());

		p.getInventory().addItem(new ItemBuilder(
				Material.getMaterial(getConfig().getString("inventory.sword2.material").toUpperCase()))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword2.enchantments.durability"))
				.setName(getConfig().getString("inventory.sword2.name")).build());

		p.getInventory().setArmorContents(getArmour(
				Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
				getConfig().getInt("armor.enchantments.durability"),
				getConfig().getInt("armor.enchantments.protection")));
	}

	@Override
	public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		if (!Arrays.asList(Material.DIAMOND_SWORD, Material.IRON_SWORD).contains(damager.getItemInHand().getType()))
			return;
		int level = getLevel(damager);

		if (damager.getItemInHand().getItemMeta().getDisplayName().contains("fire")) {
			fireFreezeCalc(damagee, 25, 7, 0);
		} else if (damager.getItemInHand().getItemMeta().getDisplayName().contains("ice")) {
			/* if (level == 1)
				fireFreezeCalc(damagee, 0, 0, 4);
			else if (level == 2)
				fireFreezeCalc(damagee, 0, 0, 7);
			else */
				fireFreezeCalc(damagee, 0, 0, 10);
		}
	}

	private void fireFreezeCalc(Player damagee, int firechance, int firedur, int freezechance) {
		int random = UtilMath.getRandom(0, 100);

		if (random <= firechance)
			damagee.setFireTicks(firedur * 20);
		else if (random <= firechance + freezechance) {
			damagee.sendMessage("§bYou got frozen.");
			freezePlayer(damagee, 5);
		}
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Your fire sword has a chance", "§7to light someone on fire", "§7and your ice sword can freeze people.");
	}

}
