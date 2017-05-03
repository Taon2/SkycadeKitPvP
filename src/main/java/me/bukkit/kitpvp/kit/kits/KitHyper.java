package me.bukkit.kitpvp.kit.kits;

import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class KitHyper extends Kit {

	public KitHyper(KitManager kitManager) {
		super(kitManager, "Hyper", KitType.HYPER, 28000, "Sugar makes him go crazy");
		setIcon(Material.SUGAR);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.KNOCKBACK, level == 3 ? 0 : 1).addEnchantment(Enchantment.DAMAGE_ALL, level == 3 ? 1 : 0).build());
		p.getInventory().addItem(new ItemBuilder(Material.SUGAR).build());
		
		p.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).addEnchantment(Enchantment.DURABILITY, level + 2).setColour(Color.BLACK).build());
		p.getInventory().setChestplate(new ItemBuilder(Material.IRON_CHESTPLATE).build());
		p.getInventory().setLeggings(new ItemBuilder(Material.IRON_LEGGINGS).build());
		p.getInventory().setBoots(new ItemBuilder(Material.IRON_BOOTS).build());
	}
	
	@Override
	public void onItemUse(Player p, ItemStack item) {
		if (item.getType() != Material.SUGAR)
			return;
		if (!addCooldown(p, getName(), (6 - getLevel(p)) * 10, true))
			return;
		p.addPotionEffects(Arrays.asList(new PotionEffect(PotionEffectType.SPEED, 50 + getLevel(p) * 100, 2), new PotionEffect(PotionEffectType.REGENERATION, 50 + getLevel(p) * 100, 1)));
		p.getWorld().playEffect(p.getLocation(), Effect.ENDER_SIGNAL, 0);
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Use sugar to gain speed and regeneration", "§7the effects are longer if you have a", "§7higher level");
	}

}
