package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitSonic extends Kit {

	private final HashMap<UUID, Integer> sprinting = new HashMap<>();

	public KitSonic(KitManager kitManager) {
		super(kitManager, "Sonic", KitType.SONIC, 28000, "You gotta go fast!");
		setIcon(Material.BREAD);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level == 1 ? 1 : 2).build());
		p.getInventory().setArmorContents(getArmour(Material.LEATHER_HELMET, 12, level == 3 ? 3 : 2, Color.BLUE));
		p.getInventory().setBoots(new ItemBuilder(p.getInventory().getArmorContents()[0]).setColour(Color.RED).build());
	}
	
	@Override
	public void onMove(Player p) {
		int level = getLevel(p);
		
		if (p.isSprinting()) {
			if (!sprinting.containsKey(p.getUniqueId())) 
				sprinting.put(p.getUniqueId(), 1);
			else {
				int value = sprinting.get(p.getUniqueId());
				value++;
				if (level == 1) 
					soniceSprint(p, value, Arrays.asList(2, 5, 9, 15, 22));
				else if (level == 2) 
					soniceSprint(p, value, Arrays.asList(1, 4, 6, 11, 18));
				else 
					soniceSprint(p, value, Arrays.asList(1, 2, 4, 8, 15));
				sprinting.put(p.getUniqueId(), value);
			}
			
		} else {
			if (p.hasPotionEffect(PotionEffectType.SPEED)) 
				p.removePotionEffect(PotionEffectType.SPEED);				
			sprinting.remove(p.getUniqueId());
		}
	}

	private void soniceSprint(Player p, int value, List<Integer> values) {
		if (values.contains(value))  {
			if (p.hasPotionEffect(PotionEffectType.SPEED)) 
				p.removePotionEffect(PotionEffectType.SPEED);		
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, values.indexOf(value)));
			if (values.indexOf(value) == values.size() - 1) {
				p.getWorld().playEffect(p.getLocation(), Effect.POTION_BREAK, 1);
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WOLF_SHAKE, 1, 1);
			}
		}			
	}

	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Get speed when you're sprinting", "§7the effect increases the longer you sprint");
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
	    UUID uuid = e.getPlayer().getUniqueId();
		sprinting.remove(uuid);
	}

}
