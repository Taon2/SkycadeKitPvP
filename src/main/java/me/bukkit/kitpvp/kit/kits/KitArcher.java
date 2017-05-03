package me.bukkit.kitpvp.kit.kits;

import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.coreclasses.utils.ParticleEffect;
import me.bukkit.kitpvp.coreclasses.utils.UtilMath;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class KitArcher extends Kit {
	
	private final List<UUID> bowCooldown = new ArrayList<>();

	public KitArcher(KitManager kitManager) {
		super(kitManager, "Archer", KitType.ARCHER, 8000, "Chance-based archer kit");
		setIcon(new ItemStack(Material.BOW));
	}
	
	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.STONE_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level >= 3 ? 1 : 0).build());
		p.getInventory().addItem(new ItemBuilder(Material.BOW).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.ARROW_INFINITE, 1).addEnchantment(Enchantment.ARROW_DAMAGE, level).build());
		p.getInventory().addItem(new ItemBuilder(Material.ARROW, 1).build());

		p.getInventory().setArmorContents(getArmour(Material.LEATHER_HELMET, 5, level == 1 ? 2 : 3));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, level == 3 ? 1 : 0));
	}

	public void onArrowLaunch(Player shooter, ProjectileLaunchEvent e) {
		if (bowCooldown.contains(shooter.getUniqueId())) {
			e.setCancelled(true);
			return;
		}
		bowCooldown.add(shooter.getUniqueId());
		Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> bowCooldown.remove(shooter.getUniqueId()), 20);
	}

	public void onArrowHit(Player shooter, Player damagee, EntityDamageByEntityEvent e) {
		int level = getLevel(shooter);
		
		if (level == 1) 
			archerChanceEffects(shooter, damagee, e, 30, 30, 20, 20, 10, 5);
		else if (level == 2) 
			archerChanceEffects(shooter, damagee, e, 40, 40, 30, 20, 10, 7);
		else 
			archerChanceEffects(shooter, damagee, e, 60, 50, 30, 30, 20, 10);
	}

	private void archerChanceEffects(Player archer, Player target, EntityDamageByEntityEvent e, int regainHealth, int doubleDamage, int slowEffect, int miningEffect, int blindEffect, int instaChance) {
		int randomNumber = UtilMath.getRandom(0, 1000);
		if (randomNumber <= regainHealth) {
			archer.setHealth(archer.getMaxHealth());
			archer.sendMessage("§cHealth boost!");
		} else if (randomNumber <= regainHealth + doubleDamage) {
			archer.sendMessage("§eDouble damage!");
			e.setDamage(e.getDamage() * 2);
			target.sendMessage("§e" + archer.getName() + " got double damage on you");
		} else if (randomNumber <= regainHealth + doubleDamage + slowEffect) {
			archer.sendMessage("§0Target is slowed");
			target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 0));
			target.sendMessage("§0" + archer.getName() + " slowed you");
		} else if (randomNumber <= regainHealth + doubleDamage + slowEffect + miningEffect) {
			archer.sendMessage("§7Target attack speed lowered");
			target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * 10, 1));
			target.sendMessage("§7"+ archer.getName() + " lowered your attack speed");
		} else if (randomNumber <= regainHealth + doubleDamage + slowEffect + miningEffect + blindEffect) {
			archer.sendMessage("§5Target blinded");
			target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 6 * 20, 0));
			target.sendMessage("§5You got blinded by " + archer.getName());
		} else if (randomNumber <= regainHealth + doubleDamage + slowEffect + miningEffect + blindEffect + instaChance) {
			archer.sendMessage("§4Triple damage!");
			target.sendMessage("§7" + archer.getName() + " got triple damage on you");
			e.setDamage(e.getDamage() * 3);
			ParticleEffect.EXPLOSION_NORMAL.display(0, 0, 0, 0, 1, target.getLocation(), 20);
		}
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Arrows have a chance to", "§7give effects to your target", "§7higher level is a higher", "§7chance for good effects");
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		bowCooldown.remove(e.getPlayer().getUniqueId());
	}

}