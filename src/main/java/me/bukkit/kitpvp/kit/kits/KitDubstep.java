package me.bukkit.kitpvp.kit.kits;

import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.coreclasses.utils.UtilMath;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class KitDubstep extends Kit {

	public KitDubstep(KitManager kitManager) {
		super(kitManager, "Dubstep", KitType.DUBSTEP, 7000, "Woop Woop Woop Womp!");
		setIcon(new ItemStack(Material.GLOWSTONE));
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.DIAMOND_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level == 1 ? 1 : 2).build());
		p.getInventory().setArmorContents(getArmour(Material.IRON_HELMET, 0, 0));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, level == 3 ? 2 : 3));
	}
	
	public void onMove(Player p) {
		int level = getLevel(p);
		if (level == 3 && UtilMath.getRandom(0, 100) <= 5)
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, level == 3 ? 2 : level));
	}

}
