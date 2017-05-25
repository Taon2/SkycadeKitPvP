package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class KitPlush extends Kit {

	public KitPlush(KitManager kitManager) {
		super(kitManager, "Plush", KitType.PLUSH, 24000, "Launch players into oblivion");
		setIcon(Material.RAW_FISH);

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("inventory.sword.material", "IRON_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.enchantments.damage-all", 1);
		defaultsMap.put("inventory.sword.enchantments.knockback", 1);

		defaultsMap.put("armor.helmet.material", "LEATHER");
		defaultsMap.put("armor.helmet.enchantments.durability", 10);
		defaultsMap.put("armor.helmet.enchantments.protection", 0);

		defaultsMap.put("armor.chestplate.material", "IRON");

		defaultsMap.put("armor.leggings.material", "IRON");

		defaultsMap.put("armor.boots.material", "LEATHER");
		defaultsMap.put("armor.boots.enchantments.durability", 5);

		setConfigDefaults(defaultsMap);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(
		        Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
                .addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all"))
                .addEnchantment(Enchantment.KNOCKBACK, getConfig().getInt("inventory.sword.enchantments.knockback")).build());

		p.getInventory().setHelmet(new ItemBuilder(
		        Material.getMaterial(getConfig().getString("armor.helmet.material").toUpperCase() + "_HELMET"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.helmet.enchantments.durability"))
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.helmet.enchantments.protection")).build());

		p.getInventory().setChestplate(new ItemBuilder(
		        Material.getMaterial(getConfig().getString("armor.chestplate.material").toUpperCase() + "_CHESTPLATE")).build());

		p.getInventory().setLeggings(new ItemBuilder(
		        Material.getMaterial(getConfig().getString("armor.leggings.material").toUpperCase() + "_LEGGINGS")).build());

		p.getInventory().setBoots(new ItemBuilder(
		        Material.getMaterial(getConfig().getString("armor.boots.material").toUpperCase() + "_BOOTS"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.boots.enchantments.durability")).build());
	}
	
	@Override
	public void onItemUse(Player p, ItemStack item) {
		if (item.getType() != Material.IRON_SWORD)
			return;
		if (!addCooldown(p, getName(), 5, true))
			return;
		int level = getLevel(p);
		
		Location loc = p.getEyeLocation();
		LivingEntity cat = (LivingEntity) p.getWorld().spawnEntity(loc.add(loc.getDirection()), EntityType.OCELOT);
		cat.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 99));

		for (Entity ent : loc.getChunk().getEntities()) 
		if (ent.getType() == EntityType.OCELOT) 
			if (!((Ocelot) ent).isAdult())
				ent.remove();

		cat.setVelocity(loc.getDirection().multiply(0.5D));
		cat.setCustomName("Plush cat");
		p.playSound(loc, Sound.ENTITY_CAT_AMBIENT, 0, 0);
		
		Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> {
			Set<Player> targetPlayers = UtilPlayer.getNearbyPlayers(cat.getLocation(), 3.5);
			targetPlayers.forEach(target -> {
				/* if (level == 1) {
					target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2));
					target.setVelocity(new Vector(0, 2, 0));
				} else if (level == 2) {
					target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 3));
					target.setVelocity(new Vector(0, 2.3, 0));
				} else { */
					target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 3));
					target.setVelocity(new Vector(0, 2.5, 0));
				//}
			});
			
			cat.getLocation().getWorld().createExplosion(cat.getLocation(), 0);
			cat.remove();
		}, 10);
	}
	
	@EventHandler
	public void onCatHit(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player))
			return;
		if (e.getEntity() instanceof Player)
			return;
		if (e.getEntity().getCustomName() != null) {
			if (e.getEntity().getCustomName().contains("Plush cat")) {
				e.setCancelled(true);
			}
		}
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Use your sword to throw a cat", "§7the cat can launch players");
	}

}
