package me.bukkit.kitpvp.kit.kits;

import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.coreclasses.utils.ParticleEffect;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class KitHuntsman extends Kit implements Listener {
	
	private final List<UUID> huntsmanActiveBleed = new ArrayList<>();
	private final List<UUID> bleeding = new ArrayList<>();

	public KitHuntsman(KitManager kitManager) {
		super(kitManager, "Huntsman", KitType.HUNTSMAN, 40000, "Hunt them down!");
		setIcon(Material.SKULL_ITEM);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.STONE_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.KNOCKBACK, level == 3 ? 0  : 1).addEnchantment(Enchantment.DAMAGE_ALL, level + 1).build());
		p.getInventory().setArmorContents(getArmour(Material.IRON_HELMET, 0, 0));
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0));
	}
	
	@Override
	public void onItemUse(Player p, ItemStack item) {
		if (item.getType() != Material.IRON_SWORD && item.getType() != Material.STONE_SWORD)
			return;
		if (getLevel(p) < 2)
			return;
		if (!addCooldown(p, getName(), getLevel(p) == 2 ? 30 : 20, true))
			return;
		p.sendMessage("§aBleed §7activated.");
		huntsmanActiveBleed.add(p.getUniqueId());		
		Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> {
			huntsmanActiveBleed.remove(p.getUniqueId());
			p.sendMessage("§7Bleed deactivated.");
		}, 7 * 20);
	}
	
	@Override
	public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		if (!huntsmanActiveBleed.contains(damager.getUniqueId()))
			return;
		if (bleeding.contains(damagee.getUniqueId()))
			return;
		startBleed(damager, (Player) e.getEntity(), getLevel(damager) == 3 ? 4 : 3);
		bleeding.add(damagee.getUniqueId());
	}
	
	@SuppressWarnings("deprecation")
	private void startBleed(Player huntsman, Player p, int seconds) {
		ParticleEffect.REDSTONE.display(0.3F, 0.3F, 0.3F, 0, 5, p.getEyeLocation(), 40);
		if (seconds > 0) 
			Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> {
				if (getKitManager().getKitPvP().getSpawnRegion().contains(p))
					return;
				p.setLastDamageCause(new EntityDamageByEntityEvent(huntsman, p, DamageCause.ENTITY_ATTACK,  4));
				p.damage(4);
				startBleed(huntsman, p, seconds - 1);
			}, 20);
		else
			bleeding.remove(p.getUniqueId());
	}

	@EventHandler
	public void on(PlayerQuitEvent e) {
		huntsmanActiveBleed.remove(e.getPlayer().getUniqueId());
		bleeding.remove(e.getPlayer().getUniqueId());
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7You can use your sword to give", "§7players a bleed effect");
	}

}
