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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitCerberus extends Kit {

	public KitCerberus(KitManager kitManager) {
		super(kitManager, "Cerberus", KitType.CERBERUS, 14000, "Lava is his home");
		setIcon(new ItemStack(Material.LAVA_BUCKET));

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("inventory.sword.material", "IRON_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.enchantments.damage-all", 0);

		defaultsMap.put("armor.material", "LEATHER");
		defaultsMap.put("armor.durability", 12);
		defaultsMap.put("armor.protection", 1);

		defaultsMap.put("potions.fire-resistance.amplifier", 0);

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
				getConfig().getInt("armor.durability"),
				getConfig().getInt("armor.protection"),
				Color.ORANGE));

		p.addPotionEffect(new PotionEffect(
				PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, getConfig().getInt("potions.fire-resistance.amplifier")));
	}

	@Override
	public void onMove(Player p) {
		if (p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
			shootParticlesFromLoc(p, ParticleEffect.FIREWORKS_SPARK, 40, 0.1F);
		if (p.getLocation().getBlock().getType() == Material.LAVA || p.getLocation().getBlock().getType() == Material.STATIONARY_LAVA) {
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
		return Arrays.asList("§7Get a strength buff when you're in lava", "§7the duration will be longer if", "§7you have a higher level");
	}

}
