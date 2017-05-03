package me.bukkit.kitpvp.kit.kits;

import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.coreclasses.utils.UtilMath;
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

public class KitCobra extends Kit {

	public KitCobra(KitManager kitManager) {
		super(kitManager, "Cobra", KitType.COBRA, 12000, "Poison your enemies");
		setIcon(Material.IRON_SWORD);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level == 3 ? 2 : 1).build());
		p.getInventory().setArmorContents(getArmour(Material.LEATHER_HELMET, level == 1 ? 10 : 12, 4, Color.GREEN));
	}
	
	@Override
	public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		int level = getLevel(damager);
		if (UtilMath.getRandom(0, 100) < 5 + level * 3)
			damagee.addPotionEffect(new PotionEffect(PotionEffectType.POISON, level == 1 ? 60 : 60 + 20 * level - 1, 1));
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Your sword can poison players", "§7you got a higher chance when", "§7you have a higher level");
	}

}
