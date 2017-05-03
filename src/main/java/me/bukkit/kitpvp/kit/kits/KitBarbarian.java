package me.bukkit.kitpvp.kit.kits;

import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.coreclasses.utils.ParticleEffect;
import me.bukkit.kitpvp.coreclasses.utils.UtilMath;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class KitBarbarian extends Kit {

	public KitBarbarian(KitManager kitManager) {
		super(kitManager, "Barbarian", KitType.BARBARIAN, 7500, "RAWR!");
		setIcon(Material.IRON_AXE);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(level == 1 ? Material.IRON_AXE : Material.DIAMOND_AXE).addEnchantment(Enchantment.DURABILITY, 5).build());
		ItemStack[] armor = getArmour(Material.IRON_HELMET, 0, 0);
		
		if (level == 3)
			armor[3] = new ItemBuilder(Material.IRON_HELMET).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
		p.getInventory().setArmorContents(armor);
	}

	@Override
	public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		if (getLevel(damagee) == 1)
			return;
		int chance = getLevel(damagee) * 2;
		if (UtilMath.getRandom(0, 100) <= chance) {
			if (damagee.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
				damagee.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
			damagee.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0));
			damagee.getWorld().playEffect(damagee.getLocation(), Effect.FLAME, 1);
			damagee.getWorld().playSound(damagee.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 1, 1);
			shootParticlesFromLoc(damagee, ParticleEffect.FLAME, 500, 0.3F);
		}
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7You got a chance to gain", "§7strength when you're getting hit", "§7higher level means a higher chance");
	}

}