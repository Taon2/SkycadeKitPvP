package me.bukkit.kitpvp.kit.kits;

import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
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

import java.util.Collections;
import java.util.List;

public class KitJesus extends Kit {

	public KitJesus(KitManager kitManager) {
		super(kitManager, "Jesus", KitType.JESUS, 30000, "Fast in water");
		setIcon(Material.BOOK_AND_QUILL);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(level == 1 ? Material.IRON_SWORD : Material.DIAMOND_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level == 3 ? 1 : 0).build());
		p.getInventory().setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE).build());
		p.getInventory().setLeggings(new ItemBuilder(Material.DIAMOND_LEGGINGS).build());
		p.getInventory().setBoots(new ItemBuilder(Material.DIAMOND_BOOTS).addEnchantment(Enchantment.DEPTH_STRIDER, level).addLore("§FReceive 100% more damage.").build());
	}
	
	@Override
	public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		e.setDamage(e.getDamage() * 2);
	}
	
	@Override
	public void onMove(Player p) {
		if (p.getLocation().getBlock().getType() == Material.WATER || p.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
			if (!p.hasPotionEffect(PotionEffectType.SPEED)) 
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * (3 * getLevel(p)), 1));
		}
		particleTracerEffect(p, Color.BLUE, 20);
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Collections.singletonList("§7Get speed in water");
	}

}
