package me.bukkit.kitpvp.kit.kits;

import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitStrafe extends Kit {
	
	private final Map<UUID, Integer> comboMap = new HashMap<>(); 

	public KitStrafe(KitManager kitManager) {
		super(kitManager, "Strafe", KitType.STRAFE, 41000, "Do you like to strafe?");
		setIcon(Material.DIAMOND_BOOTS);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.STONE_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level == 3 ? 0 : level + 1).build());
		p.getInventory().setBoots(new ItemBuilder(Material.DIAMOND_BOOTS).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level).build());
		
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, level == 1 ? 2 : 3));
		if (level > 1)
			p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, level - 1));
		if (level == 3) 
			p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
	}
	
	@Override
	public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		if (!comboMap.containsKey(damager.getUniqueId())) {
			comboMap.put(damager.getUniqueId(), 1);
			return;
		}
		int combo = comboMap.get(damager.getUniqueId()) + 1;
		damager.setLevel(combo);
		if (combo > 0 && (combo % 3 == 0)) 
			damager.sendMessage("§7Current combo is §a" + combo + "§7.");
		
		double dmgInc = 1.0;
		while (combo >= 3) {
			dmgInc += 0.1;
			combo -= 3;
		}
		e.setDamage(e.getDamage() * (dmgInc >= 1.5 ? 1.5 : dmgInc));
	}
	
	@Override
	public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		comboMap.remove(damagee.getUniqueId());
	}
	
	@Override
	public void onMove(Player p) {
		if (getLevel(p) >= 3)
			particleTracerEffect(p, Color.RED, 30);
	}

	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Comboing someone will make your", "§7hits more deadly");
	}
	
	@EventHandler
	public void on(PlayerQuitEvent e) {
		comboMap.remove(e.getPlayer().getUniqueId());
	}

}
