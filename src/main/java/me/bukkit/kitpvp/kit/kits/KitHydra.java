package me.bukkit.kitpvp.kit.kits;

import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.coreclasses.utils.ParticleEffect;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class KitHydra extends Kit {

	public KitHydra(KitManager kitManager) {
		super(kitManager, "Hydra", KitType.HYDRA, 20000, "Wanna go for a swim?");
		setIcon(Material.WATER_BUCKET);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.KNOCKBACK, level == 1 ? 1 : 0).addEnchantment(Enchantment.DAMAGE_ALL, level == 3 ? 1 : 0).build());
		p.getInventory().setArmorContents(getArmour(Material.LEATHER_HELMET, 12, level == 1 ? 1 : 2, Color.BLUE));
		p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0));
	}
	
	@Override
	public void onMove(Player p) {
		if (p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
			shootParticlesFromLoc(p, ParticleEffect.WATER_WAKE, 40, 0.1F);
		if (p.getLocation().getBlock().getType() == Material.WATER || p.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
			p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
			p.removePotionEffect(PotionEffectType.SLOW);
			p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40 * getLevel(p), 1));
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40 * getLevel(p), 2));
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
