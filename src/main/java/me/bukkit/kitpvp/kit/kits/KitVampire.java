package me.bukkit.kitpvp.kit.kits;

import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.coreclasses.utils.ParticleEffect;
import me.bukkit.kitpvp.coreclasses.utils.UtilMath;
import me.bukkit.kitpvp.coreclasses.utils.UtilPlayer;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class KitVampire extends Kit {

	public KitVampire(KitManager kitManager) {
		super(kitManager, "Vampire", KitType.VAMPIRE, 25000, "Loves the taste of blood");
		setIcon(Material.REDSTONE);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level == 3 ? 1 : 0).build());
		p.getInventory().setArmorContents(getArmour(Material.LEATHER_HELMET, 10, level == 1 ? 3 : 4, Color.RED));
		p.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).addEnchantment(Enchantment.DURABILITY, 12).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level).setColour(Color.BLACK).build());
	}
	
	@Override
	public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		int random = UtilMath.getRandom(0, 100);
		int level = getLevel(damager);
		int chance = 4 + level;
		
		if (random < chance) {
			double healAmount = level == 3 ? 2.5 : 2;
			if (damager.getHealth() + healAmount < damager.getMaxHealth()) 
				damager.setHealth(damager.getHealth() + healAmount);				
			else if (damager.getHealth() == damager.getMaxHealth()) 
				damager.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 60, 0));
			else 
				damager.setHealth(damager.getMaxHealth());			
			damager.sendMessage("§cHealed!");
			
		} else if (random < chance * 2) {
			damagee.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, level == 1 ? 80 : 80 + 20 * level - 1, 0));
			damagee.sendMessage("§cYou are bit by §f" + damager.getName() + "§c.");
		}
	}
	
	@Override
	public void onItemUse(Player p, ItemStack item) {
		if (item.getType() != Material.IRON_SWORD)
			return;
		int level = getLevel(p);
		if (!addCooldown(p, getName(), 45 - (level * 5), true))
			return;
		Set<Player> targetPlayers = UtilPlayer.getNearbyPlayers(p.getLocation(), 4);
		if (targetPlayers.size() <= 1) {
			removeCooldowns(p);
			return;
		}
		
		double healAmount = 0.0;
		for (Player target : targetPlayers) {
			if (target != p) {
				startBleed(p, target, level + 4);
				healAmount += 4;
			}
		}
		
		if (healAmount > 0) {
			if (p.getHealth() + healAmount > p.getMaxHealth()) {
				healAmount -= (p.getMaxHealth() - p.getHealth());
				if (healAmount > 0) 
					p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, (int) (20 * Math.floor(healAmount)), 0));
			} else 
				p.setHealth(p.getHealth() + healAmount);
			p.sendMessage("§7You got §7Healed!");
		}
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Use your sword ability to give", "§7players around you a bleed effect", "§7you will also get healed if there are many", "§7players around you");
	}
	
	@SuppressWarnings("deprecation")
	private void startBleed(Player vampire, Player p, int seconds) {
		ParticleEffect.REDSTONE.display(0.3F, 0.3F, 0.3F, 0, 5, p.getEyeLocation(), 40);
		
		if (seconds > 0) 
			Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> {
				if (getKitManager().getKitPvP().getSpawnRegion().contains(p))
					return;
				p.setLastDamageCause(new EntityDamageByEntityEvent(vampire, p, DamageCause.ENTITY_ATTACK,  3 + getLevel(vampire)));
				p.damage(4);
				startBleed(vampire, p, seconds - 1);
			}, 15);
	}

}
