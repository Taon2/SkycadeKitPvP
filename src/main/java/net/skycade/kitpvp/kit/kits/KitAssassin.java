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

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("inventory.sword.material", "DIAMOND_SWORD");
		defaultsMap.put("inventory.sword.enchantments.damage-all", 0);
		defaultsMap.put("inventory.armour.type", "LEATHER");
		defaultsMap.put("inventory.armour.durability", 12);
		defaultsMap.put("inventory.armour.protection", 2);
		defaultsMap.put("boots.enchantments.durability", 20);
		defaultsMap.put("boots.enchantments.protection", 3);
		defaultsMap.put("potions.digging.amplifier", 3);
		defaultsMap.put("potions.speed.amplifier", 0);

		setConfigDefaults(defaultsMap);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(
				Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
				.addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

		ItemStack[] armor = getArmour(Material.getMaterial(
				getConfig().getString("inventory.armour.type") + "_HELMET"),
				getConfig().getInt("inventory.armour.durability"),
				getConfig().getInt("inventory.armour.protection"),
				Color.BLACK);

		armor[0] = new ItemBuilder(
				Material.getMaterial(getConfig().getString("inventory.armour.type") + "_BOOTS"))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("boots.enchantments.durability"))
				.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("boots.enchantments.protection"))
				.setColour(Color.RED).build();

		p.getInventory().setArmorContents(armor);

		p.addPotionEffect(new PotionEffect(
				PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, getConfig().getInt("potions.digging.amplifier")));
		p.addPotionEffect(new PotionEffect(
				PotionEffectType.SPEED, Integer.MAX_VALUE , getConfig().getInt("potions.speed.amplifier")));
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
