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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class KitSharingan extends Kit {

	public KitSharingan(KitManager kitManager) {
		super(kitManager, "Sharingan", KitType.SHARINGAN, 40000, "His eyes are powerful");
		setIcon(Material.EYE_OF_ENDER);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level - 1).build());
		p.getInventory().setArmorContents(getArmour(Material.LEATHER_HELMET, (level * 2) + 5, 3, Color.BLACK));
		p.getInventory().getArmorContents()[0].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		p.getInventory().getArmorContents()[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
	}

	@Override
	public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		int level = getLevel(damagee);
		Collection<PotionEffect> effects = damager.getActivePotionEffects();

		effects.forEach((eff) -> {
			if (eff.getDuration() > 1200 * level)
				damagee.addPotionEffect(new PotionEffect(eff.getType(), 1200 * level, eff.getAmplifier()));
			else
				damagee.addPotionEffect(eff);
		}); 
		if (damagee.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
			damagee.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);

		if (level == 3) 					
			for (PotionEffectType effect : Arrays.asList(PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION, PotionEffectType.HARM, PotionEffectType.POISON, PotionEffectType.SLOW,	PotionEffectType.SLOW_DIGGING, PotionEffectType.WEAKNESS, PotionEffectType.WITHER))
				if (damagee.hasPotionEffect(effect))
					damagee.removePotionEffect(effect);
	}

	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Temporary copy potioneffects when you're getting hit", "§7you will only copy positive effects on level 3");
	}

}
