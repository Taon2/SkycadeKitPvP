package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class KitGhost extends Kit {

	public KitGhost(KitManager kitManager) {
		super(kitManager, "Ghost", KitType.GHOST, 22000, false, "Very spooky");
		setIcon(new ItemBuilder(new ItemStack(Material.POTION, 1, (short) 8270)).build());
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.DIAMOND_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.KNOCKBACK, level == 1  ? 1 : 0).addEnchantment(Enchantment.DAMAGE_ALL, level + 1).build());
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 10, false, false));
		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
	}
	
	@Override
	public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		/* if (getLevel(damagee) <= 2) {
			damagee.removePotionEffect(PotionEffectType.INVISIBILITY);
			
			Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> {
				if (getKitManager().getKitPvP().getStats(damagee).getActiveKit() == KitType.GHOST) 
					damagee.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
			}, 40);
		} */
	}
	
	@Override
	public void onItemUse(Player p, ItemStack item) {
		if (item.getType() != Material.DIAMOND_SWORD)
			return;
		int level = getLevel(p);
		if (!addCooldown(p, getName(), 10, true))
			return;
		
		int range = 4;
		Set<Player> targetPlayers = UtilPlayer.getNearbyPlayers(p.getLocation(), range);
		if (targetPlayers.size() <= 1)
			removeCooldowns(p);
		
		targetPlayers.forEach(target -> {
			if (!target.equals(p)) {
				target.sendMessage("Get spooked!");
				levitateInAir(target, 40);
			}
		});
	}
	
	private void levitateInAir(Player target, Integer ticks) {
		if (ticks > 0) {
			int remainingTicks = ticks - 1;
			Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> {
				particleEffect(target);
				target.setVelocity(new Vector(0, 0.1, 0));
				levitateInAir(target, remainingTicks);
			}, 1);
		}
	}
	
	private void particleEffect(Player target) {
		for (int i = 0 ; i < 5 ; i++)
			ParticleEffect.SPELL_MOB.display(new ParticleEffect.OrdinaryColor(Color.WHITE), target.getLocation().add(0, 0.1F, 0), 1F);
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Right click with your sword", "§7to spook players around you");
	}

}
