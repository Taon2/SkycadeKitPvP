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
import java.util.List;

public class KitDualBlader extends Kit {

	public KitDualBlader(KitManager kitManager) {
		super(kitManager, "DualBlader", KitType.DUALBLADER, 34000, "Use the sword of fire and ice");
		setIcon(new ItemStack(Material.PACKED_ICE));
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(level == 1 ? Material.IRON_SWORD : Material.DIAMOND_SWORD).addEnchantment(Enchantment.DURABILITY, 5).setName("§cSword of fire").build());
		p.getInventory().addItem(new ItemBuilder(level == 1 ? Material.IRON_SWORD : Material.DIAMOND_SWORD).addEnchantment(Enchantment.DURABILITY, 5).setName("§bSword of ice").build());
		p.getInventory().setArmorContents(getArmour(Material.IRON_HELMET, 1, 0));
		if (level == 3)
			p.getInventory().setLeggings(new ItemBuilder(Material.IRON_LEGGINGS).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build());
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
