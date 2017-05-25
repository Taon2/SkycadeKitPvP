package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitZeus extends Kit {

	public KitZeus(KitManager kitManager) {
		super(kitManager, "Zeus", KitType.ZEUS, 30000, "Lightning strikes!");
		setIcon(Material.BLAZE_ROD);

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("inventory.blaze-rod.enchantments.damage-all", 5);

		defaultsMap.put("armor.material", "LEATHER");
		defaultsMap.put("armor.enchantments.durability", 12);
		defaultsMap.put("armor.enchantments.protection", 4);

		defaultsMap.put("potions.fire-resistance.amplifier", 0);

		setConfigDefaults(defaultsMap);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(
				Material.BLAZE_ROD)
				.addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.blaze-rod.enchantments.damage-all")).build());

		p.getInventory().setArmorContents(getArmour(
				Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
				getConfig().getInt("armor.enchantments.durability"),
				getConfig().getInt("armor.enchantments.protection"),
				Color.fromBGR(153, 255, 255)));

		p.addPotionEffect(new PotionEffect(
				PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, getConfig().getInt("potions.fire-resistance.amplifier")));
	}
	
	@Override
	public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		if (UtilMath.getRandom(0, 100) <= 23) {
			damagee.getWorld().strikeLightning(damagee.getLocation());
			e.setDamage(e.getDamage() * 1.4);
			damagee.setFireTicks(60);
		}
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7There is a chance to strike lightning", "§7when you hit someone");
	}
	
}
