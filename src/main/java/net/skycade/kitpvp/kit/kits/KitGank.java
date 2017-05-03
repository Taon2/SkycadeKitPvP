package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class KitGank extends Kit {

	public KitGank(KitManager kitManager) {
		super(kitManager, "Gank", KitType.GANK, 35000, "Gank your enemies");
		setIcon(Material.STONE_SWORD);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.STONE_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.KNOCKBACK, level == 1 ? 1 : 0).addEnchantment(Enchantment.DAMAGE_ALL, level - 1).build());
		p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, level == 1 ? 0 : 1));
		if (level == 3)	
			p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
	}
	
	@Override
	public void onMove(Player p) {
		if (getLevel(p) >= 3)
			particleTracerEffect(p, Color.GRAY, 20);
	}

}
