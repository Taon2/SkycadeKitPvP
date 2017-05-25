package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
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

public class KitHydra extends Kit {

	public KitHydra(KitManager kitManager) {
		super(kitManager, "Hydra", KitType.HYDRA, 20000, "Wanna go for a swim?");
		setIcon(Material.WATER_BUCKET);

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("inventory.sword.material", "IRON_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.enchantments.knockback", 1);
		defaultsMap.put("inventory.sword.enchantments.damage-all", 0);

		defaultsMap.put("armor.material", "LEATHER");
		defaultsMap.put("armor.enchantments.durability", 12);
		defaultsMap.put("armor.enchantments.protection", 1);

		defaultsMap.put("potions.water-breathing.amplifier", 0);

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
				getConfig().getInt("armor.enchantments.protection"),
				Color.BLUE));

		p.addPotionEffect(new PotionEffect(
				PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, getConfig().getInt("potions.water-breathing.amplifier")));
	}
	
	@Override
	public void onMove(Player p) {
		if (p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
			shootParticlesFromLoc(p, ParticleEffect.WATER_WAKE, 40, 0.1F);
		if (p.getLocation().getBlock().getType() == Material.WATER || p.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
			p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
			p.removePotionEffect(PotionEffectType.SLOW);
			p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40 * 3, 1));
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40 * 3, 2));
		}
	}
	
	@Override
	public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		if (shacoHit.contains(damagee.getUniqueId()))
			e.setDamage(1);
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7You will get a strength buff", "§7when you're in water");
	}

}
