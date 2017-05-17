package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class KitAssassin extends Kit {
	
	private final Map<UUID, Integer> comboMap = new HashMap<>(); 
	
	public KitAssassin(KitManager kitManager) {
		super(kitManager, "Assassin", KitType.ASSASSIN, 50000, "Be a sneaky assassin");
		setIcon(Material.COAL);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.DIAMOND_SWORD).addEnchantment(Enchantment.DAMAGE_ALL, level == 3 ? 1 : 0).build());
		ItemStack[] armor = getArmour(Material.LEATHER_HELMET, 12, level == 3 ? 3 : 2, Color.BLACK);
		armor[0] = new ItemBuilder(Material.LEATHER_BOOTS).addEnchantment(Enchantment.DURABILITY, 20).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level + 2).setColour(Color.RED).build();
		p.getInventory().setArmorContents(armor);
		p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 3));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE , 0));
	}
	
	@Override
	public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		if (isBackStab(damager, damagee))
			e.setDamage(e.getDamage() * (1.0 + (0.1 * getLevel(damager))));
		if (!comboMap.containsKey(damager.getUniqueId())) {
			comboMap.put(damager.getUniqueId(), 1);
			damager.setLevel(1);
			return;
		}
		int combo = comboMap.get(damager.getUniqueId()) + 1;
		comboMap.put(damager.getUniqueId(), combo);
		damager.setLevel(combo);
		if (combo >= 7) {
			damager.setLevel(0);
			comboMap.remove(damager.getUniqueId());
			teleportBehindPlayer(damager, damagee.getLocation());
		}
		if (damager.getItemInHand().getType() == Material.DIAMOND_SWORD)
			hitParticles(damager);
	}

	private void hitParticles(Player p) {
		double t = 0.0;
		Location loc = p.getLocation().add(0, 0.2, 0);
		Vector dir = p.getLocation().getDirection().normalize();
		while (t < 0.7) {
			t += 0.05F;
			double x = dir.getX() * t;
			double y = dir.getY() * t;
			double z = dir.getZ() * t;
			loc.add(x, y, z);
			
			for (int i = 0; i < 3; i ++) 
				ParticleEffect.SPELL_MOB.display(new ParticleEffect.OrdinaryColor(Color.RED), loc, 30);
			for (Entity entity : loc.getChunk().getEntities()) {
				if (entity.getLocation().distance(loc) < 0.5 && entity != p) {
					t = 1;
				}
			}
		}
	}
	
	private boolean isBackStab(Player damager, Player damagee) {
		final double diffX = damager.getLocation().getDirection().getX() - damagee.getLocation().getDirection().getX();
		final double diffZ = damager.getLocation().getDirection().getZ() - damagee.getLocation().getDirection().getZ();
		if (diffX > 0 && diffX < 1 || diffX < 0 && diffX > -1) {
			if (diffZ > 0 && diffZ < 1 || diffZ < 0 && diffZ > -1) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Collections.singletonList("§7Hits from behind deal more damage depending on your level");
	}
	
	@EventHandler
	public void on(PlayerQuitEvent e) {
		comboMap.remove(e.getPlayer());
	}
	
}
