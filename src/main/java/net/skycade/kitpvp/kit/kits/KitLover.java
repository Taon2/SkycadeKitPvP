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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;

public class KitLover extends Kit {

	public KitLover(KitManager kitManager) {
		super(kitManager, "Lover", KitType.LOVER, 19000, "Love is a weird thing");
		setIcon(Material.RED_ROSE);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.WOOD_SWORD).addEnchantment(Enchantment.DURABILITY, 10).addEnchantment(Enchantment.DAMAGE_ALL, level == 3 ? 4 : level + 1).build());
		p.getInventory().addItem(new ItemBuilder(Material.RED_ROSE).build());
		p.getInventory().setArmorContents(getArmour(Material.LEATHER_HELMET, 12, level == 3 ? 5 : 4, Color.RED));
	}
	
	@Override
	public void onInteract(Player p, Player target, ItemStack item) {
		if (item.getType() != Material.RED_ROSE)
			return;
		int level = getLevel(p);
		if (!addCooldown(p, getName(), 15, true))
			return;
		target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 3));
		target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 200, 1));
		target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 0));
			
		ParticleEffect.HEART.display(0.5F, 0.5F, 0.5F, 1, 10, target.getLocation().add(0, 2, 0), 100);
		target.sendMessage("§cI LOVE YOU <3!");
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Collections.singletonList("§7Use the flower to love someone");
	}

}
