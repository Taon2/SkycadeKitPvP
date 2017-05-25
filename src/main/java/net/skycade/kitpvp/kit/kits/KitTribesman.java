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

import java.util.*;

public class KitTribesman extends Kit {
	private final List<UUID> tribesCd = new ArrayList<>();

	public KitTribesman(KitManager kitManager) {
		super(kitManager, "Tribesman", KitType.TRIBESMAN, 37000, "Tribesman is good with herbs");
		setIcon(Material.GOLD_CHESTPLATE);

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("inventory.sword.material", "IRON_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.enchantments.knockback", 1);
		defaultsMap.put("inventory.sword.enchantments.damage-all", 0);

		defaultsMap.put("armor.helmet.material", "IRON");

		defaultsMap.put("armor.chestplate.material", "GOLD");
		defaultsMap.put("armor.chestplate.enchantments.durability", 5);

		defaultsMap.put("armor.leggings.material", "GOLD");
		defaultsMap.put("armor.leggings.enchantments.durability", 1);

		defaultsMap.put("armor.boots.material", "IRON");

		defaultsMap.put("potions.damage.amplifier", 0);
		defaultsMap.put("potions.jump.amplifier", 0);
		defaultsMap.put("potions.regeneration.amplifier", 0);

		setConfigDefaults(defaultsMap);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(
				Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
				.addEnchantment(Enchantment.KNOCKBACK, getConfig().getInt("inventory.sword.enchantments.knockback"))
				.addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

		p.getInventory().setHelmet(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.helmet.material").toUpperCase() + "_HELMET")).build());

		p.getInventory().setChestplate(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.chestplate.material").toUpperCase() + "_CHESTPLATE"))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.chestplate.enchantments.durability")).build());

		p.getInventory().setLeggings(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.leggings.material").toUpperCase() + "_LEGGINGS"))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.leggings.enchantments.durability")).build());

		p.getInventory().setBoots(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.boots.material").toUpperCase() + "_BOOTS")).build());
		
		p.addPotionEffect(new PotionEffect(
				PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, getConfig().getInt("potions.damage.amplifier")));

		p.addPotionEffect(new PotionEffect(
				PotionEffectType.JUMP, Integer.MAX_VALUE, getConfig().getInt("potions.jump.amplifier")));

		p.addPotionEffect(new PotionEffect(
				PotionEffectType.REGENERATION, Integer.MAX_VALUE, getConfig().getInt("potions.regeneration.amplifier")));
	}

	@Override
	public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		if (tribesCd.contains(damagee.getUniqueId()))
			return;
		int level = getLevel(damagee);
		
		if (e.getFinalDamage() >= 4) {
			tribesEffect(damagee, 6 + 2 * 3);
			tribesCd.add(damagee.getUniqueId());
			Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> tribesCd.remove(damagee.getUniqueId()), 220 - (3 * 20));
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
