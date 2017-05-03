package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class KitBladeMaster extends Kit {

	public KitBladeMaster(KitManager kitManager) {
		super(kitManager, "BladeMaster", KitType.BLADEMASTER, 29000, "Master of blades");
		setIcon(Material.GOLD_SWORD);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.GOLD_SWORD).addEnchantment(Enchantment.DAMAGE_ALL, level + 1).addEnchantment(Enchantment.DURABILITY, 10).setName("Sword of damage").build());
		p.getInventory().addItem(new ItemBuilder(Material.GOLD_SWORD).addEnchantment(Enchantment.DURABILITY, 10).addEnchantment(Enchantment.KNOCKBACK, level + 1).setName("Sword of knockback").build());
		
		p.getInventory().setBoots(new ItemBuilder(Material.IRON_BOOTS).build());
		p.getInventory().setLeggings(new ItemBuilder(Material.IRON_LEGGINGS).build());
		p.getInventory().setChestplate(new ItemBuilder(Material.IRON_CHESTPLATE).build());
		p.getInventory().setHelmet(new ItemBuilder(Material.GOLD_HELMET).addEnchantment(Enchantment.DURABILITY, level + 1).build());
	}
	
	@Override 
	public void onMove(Player p) {
		if (p.isSneaking()) {
			if (p.hasPotionEffect(PotionEffectType.SPEED))
				p.removePotionEffect(PotionEffectType.SPEED);
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, getLevel(p) * 75, 0));
		}
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Gain a short speed buff", "§7when you're sneaking", "§7the effect is longer", "§7if your level is higher");
	}
	
}
