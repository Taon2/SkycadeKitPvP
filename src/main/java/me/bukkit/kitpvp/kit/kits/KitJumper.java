package me.bukkit.kitpvp.kit.kits;

import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.coreclasses.utils.ParticleEffect;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class KitJumper extends Kit {

	public KitJumper(KitManager kitManager) {
		super(kitManager, "Jumper", KitType.JUMPER, 26000, "There is no place he can't jump on");
		setIcon(Material.LEATHER_BOOTS);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DAMAGE_ALL, level == 1 ? 1 : 2).addEnchantment(Enchantment.DURABILITY, 5).build());
		
		p.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3).addEnchantment(Enchantment.DURABILITY, 10).setColour(Color.WHITE).build());
		p.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3).addEnchantment(Enchantment.DURABILITY, 10).setColour(Color.RED).build());
		p.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3).addEnchantment(Enchantment.DURABILITY, 10).setColour(Color.RED).build());
		p.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3).addEnchantment(Enchantment.DURABILITY, 10).setColour(Color.WHITE).build());
		
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, level + 1));
	}
	
	@Override
	public void onMove(Player p) {
		if (p.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR)
			return;
		for (int y = 0; y < 10 ; y++) {
			Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> {
				for (int i = 0 ; i < 5 ; i++)
					ParticleEffect.SPELL_MOB.display(new ParticleEffect.OrdinaryColor(Color.RED), p.getLocation().add(0, 0.1F, 0), 1F);
			}, y);
		}
	}

}
