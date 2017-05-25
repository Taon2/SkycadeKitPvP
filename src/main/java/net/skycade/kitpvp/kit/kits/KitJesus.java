package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitJesus extends Kit {

	public KitJesus(KitManager kitManager) {
		super(kitManager, "Jesus", KitType.JESUS, 30000, "Fast in water");
		setIcon(Material.BOOK_AND_QUILL);

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("inventory.sword.material", "IRON_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.enchantments.damage=all", 0);

		defaultsMap.put("armor.chestplate.material", "DIAMOND");

		defaultsMap.put("armor.leggings.material", "DIAMOND");

		defaultsMap.put("armor.boots.material", "DIAMOND");
		defaultsMap.put("armor.boots.enchantments.depth-strider", 1);
		defaultsMap.put("armor.boots.lore", "§FReceive 100% more damage.");

		setConfigDefaults(defaultsMap);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(
				Material.getMaterial(getConfig().getString("inventory.sword.material")))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
				.addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

		p.getInventory().setChestplate(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.chestplate.material").toUpperCase() + "_CHESTPLATE")).build());

		p.getInventory().setLeggings(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.leggings.material").toUpperCase() + "_LEGGINGS")).build());

		p.getInventory().setBoots(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.boots.material").toUpperCase() + "_BOOTS"))
				.addEnchantment(Enchantment.DEPTH_STRIDER, getConfig().getInt("armor.boots.enchantments.depth-strider"))
				.addLore(getConfig().getString("armor.boots.lore")).build());
	}
	
	@Override
	public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		e.setDamage(e.getDamage() * 2);
	}
	
	@Override
	public void onMove(Player p) {
		if (p.getLocation().getBlock().getType() == Material.WATER || p.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
			if (!p.hasPotionEffect(PotionEffectType.SPEED)) 
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * (3 * 3), 1));
		}
		particleTracerEffect(p, Color.BLUE, 20);
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Collections.singletonList("§7Get speed in water");
	}

}
