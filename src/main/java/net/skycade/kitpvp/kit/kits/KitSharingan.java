package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
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

import java.util.*;

public class KitSharingan extends Kit {

	public KitSharingan(KitManager kitManager) {
		super(kitManager, "Sharingan", KitType.SHARINGAN, 40000, "His eyes are powerful");
		setIcon(Material.EYE_OF_ENDER);

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("inventory.sword.material", "IRON_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.enchantments.damage-all", 0);

		defaultsMap.put("armor.material", "LEATHER");
		defaultsMap.put("armor.enchantments.durability", 7);
		defaultsMap.put("armor.enchantments.protection", 3);

		defaultsMap.put("armor.boots.enchantments.protection", 4);
		defaultsMap.put("armor.chestplate.enchantments.protection", 4);

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
				getConfig().getInt("armor.enchantments.protection"),
				Color.BLACK));

		p.getInventory().getArmorContents()[0]
				.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.boots.enchantments.protection"));
		p.getInventory().getArmorContents()[2]
				.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.chestplate.enchantments.protection"));
	}

	@Override
	public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		int level = getLevel(damagee);
		Collection<PotionEffect> effects = damager.getActivePotionEffects();

		effects.forEach((eff) -> {
			if (eff.getDuration() > 1200 * 3)
				damagee.addPotionEffect(new PotionEffect(eff.getType(), 1200 * 3, eff.getAmplifier()));
			else
				damagee.addPotionEffect(eff);
		}); 
		if (damagee.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
			damagee.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);

		for (PotionEffectType effect : Arrays.asList(PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION, PotionEffectType.HARM, PotionEffectType.POISON, PotionEffectType.SLOW,	PotionEffectType.SLOW_DIGGING, PotionEffectType.WEAKNESS, PotionEffectType.WITHER))
			if (damagee.hasPotionEffect(effect))
				damagee.removePotionEffect(effect);
	}

	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Temporary copy potioneffects when you're getting hit", "§7you will only copy positive effects on level 3");
	}

}
