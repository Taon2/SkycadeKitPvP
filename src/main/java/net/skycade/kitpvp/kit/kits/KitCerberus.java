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
import java.util.List;

public class KitCerberus extends Kit {

	public KitCerberus(KitManager kitManager) {
		super(kitManager, "Cerberus", KitType.CERBERUS, 14000, "Lava is his home");
		setIcon(new ItemStack(Material.LAVA_BUCKET));
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level <= 2 ? 0 : 1).build());
		p.getInventory().setArmorContents(getArmour(Material.LEATHER_HELMET, 12, level, Color.ORANGE));
		p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
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
