package me.bukkit.kitpvp.kit.kits;

import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class KitElite extends Kit {

	public KitElite(KitManager kitManager) {
		super(kitManager, "Elite", KitType.ELITE, 16000, "Speed is everything");
		setIcon(new ItemStack(Material.LEATHER_HELMET));
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.DIAMOND_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level == 3 ? 1 : 0).build());
		
		ItemStack[] armor = getArmour(Material.LEATHER_HELMET, level == 1 ? 10 : 12, level == 3 ? 4 : 3, Color.BLUE);
		armor[3] = new ItemBuilder(Material.LEATHER_HELMET).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level * 2).addEnchantment(Enchantment.DURABILITY, level + 12).setColour(Color.WHITE).build();
		p.getInventory().setArmorContents(armor);
		
		p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, level - 1));
	}

}
