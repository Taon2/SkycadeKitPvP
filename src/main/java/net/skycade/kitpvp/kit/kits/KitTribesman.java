package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class KitTribesman extends Kit {
	private final List<UUID> tribesCd = new ArrayList<>();

	public KitTribesman(KitManager kitManager) {
		super(kitManager, "Tribesman", KitType.TRIBESMAN, 37000, "Tribesman is good with herbs");
		setIcon(Material.GOLD_CHESTPLATE);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.KNOCKBACK, level == 3 ? 0 : 1).addEnchantment(Enchantment.DAMAGE_ALL, level == 1 ? 0 : 1).build());
		p.getInventory().setHelmet(new ItemBuilder(Material.IRON_HELMET).build());
		p.getInventory().setChestplate(new ItemBuilder(Material.GOLD_CHESTPLATE).addEnchantment(Enchantment.DURABILITY, 5).build());
		p.getInventory().setLeggings(new ItemBuilder(Material.GOLD_LEGGINGS).addEnchantment(Enchantment.DURABILITY, level == 1 ? 1 : 5).build());
		p.getInventory().setBoots(new ItemBuilder(Material.IRON_BOOTS).build());
		
		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0));
		if (level > 1)
			p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
	}

	@Override
	public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		if (tribesCd.contains(damagee.getUniqueId()))
			return;
		int level = getLevel(damagee);
		
		if (e.getFinalDamage() >= 4) {
			tribesEffect(damagee, level == 1 ? 6 : 6 + 2 * level);
			tribesCd.add(damagee.getUniqueId());
			Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> tribesCd.remove(damagee.getUniqueId()), 220 - (getLevel(damagee) * 20));
		}
	}
	
	private void tribesEffect(Player p, int seconds) {
		for (PotionEffect effect : p.getActivePotionEffects())
			p.removePotionEffect(effect.getType());
		p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, seconds * 20, 1));
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, seconds * 20, 0));
		p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, seconds * 20, 0));
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7You can get potion effects", "§7when someone deals a lot", "§7of damage to you");
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		tribesCd.remove(e.getPlayer().getUniqueId());
	}

}
