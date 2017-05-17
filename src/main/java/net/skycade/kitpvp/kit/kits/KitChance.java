package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class KitChance extends Kit {

	public KitChance(KitManager kitManager) {
		super(kitManager, "Chance", KitType.CHANCE, 17000, "How lucky are you?");
		setIcon(new ItemStack(Material.NETHER_BRICK_ITEM));
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, 1).build());
		ItemStack[] armor = getArmour(Material.LEATHER_HELMET, 12, level + 1, Color.ORANGE);
		armor[3] = new ItemBuilder(Material.LEATHER_HELMET).addEnchantment(Enchantment.DURABILITY, 10).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level == 1 ? 1 : 2).setColour(Color.YELLOW).build();
		p.getInventory().setArmorContents(armor);
	}
	
	@Override
	public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		int level = getLevel(damager);
		/* if (level == 1)
			chanceEffect(4, 3, 0, 3, 2, 4, 1, damager, damagee, e);
		else if (level == 2) 
			chanceEffect(5, 4, 0, 2, 3, 3, 2, damager, damagee, e);
		else if (level == 3) */
			chanceEffect(6, 5, 2, 1, 4, 2, 3, damager, damagee, e);
	}
	
	private void chanceEffect(int healthPer, int doublePer, int soupPer, int swingDownPer, int swingUpPer, int backPer, int triplePer, Player damager, Player damagee, EntityDamageByEntityEvent e) {
		int random = UtilMath.getRandom(0, 100);
		
		if (random <= healthPer) {
			damager.setHealth(damager.getMaxHealth());
			damager.sendMessage(ChatColor.RED + "Health boost!");
		} else if (random <= healthPer + doublePer) {
			e.setDamage(e.getDamage() * 2);
			damager.sendMessage(ChatColor.YELLOW + "Double damage!");
		} else if (random <= healthPer + doublePer + soupPer) {
			giveSoup(damager, 5);
			damager.sendMessage(ChatColor.AQUA + "Soup refill!");
		} else if (random <= healthPer + doublePer + soupPer + swingDownPer) {
			damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 200, 1));
			damager.sendMessage(ChatColor.WHITE + "Swing speed down!");
		} else if (random <= healthPer + doublePer + soupPer + swingDownPer + swingUpPer) {
			damager.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 200, 1));
			damager.sendMessage(ChatColor.WHITE + "Swing speed up!");			
		} else if (random <= healthPer + doublePer + soupPer + swingDownPer + swingUpPer + backPer) {
			damager.damage(e.getDamage());						
			damager.sendMessage(ChatColor.DARK_RED + "Backfire!");
			e.setCancelled(true);
		} else if (random <= healthPer + doublePer + soupPer + swingDownPer + swingUpPer + backPer + triplePer) {
			e.setDamage(e.getDamage() * 3);
			damager.sendMessage(ChatColor.YELLOW + "Triple damage!");
			damagee.sendMessage(ChatColor.RED + damager.getName() + " got triple damage!");
			ParticleEffect.EXPLOSION_NORMAL.display(0, 0, 0, 0, 1, damagee.getLocation(), 20);
		}
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Your sword can give your target", "§7different effects, higher level", "§7results in a higher chance");
	}

}
