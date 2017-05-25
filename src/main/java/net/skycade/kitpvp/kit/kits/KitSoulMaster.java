package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitSoulMaster extends Kit {

	public KitSoulMaster(KitManager kitManager) {
		super(kitManager, "SoulMaster", KitType.SOULMASTER, 33000, "Your soul is mine!");
		setIcon(Material.SOUL_SAND);

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("inventory.sword.material", "IRON_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.enchantments.damage-all", 1);

		defaultsMap.put("armor.material", "LEATHER");
		defaultsMap.put("armor.enchantments.durability", 11);
		defaultsMap.put("armor.enchantments.protection", 4);

		setConfigDefaults(defaultsMap);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(
				Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
				.addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

		p.getInventory().setArmorContents(getArmour(
				Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
				getConfig().getInt("armor.enchantments.durability"),
				getConfig().getInt("armor.enchantments.protection"),
				Color.fromBGR(0, 51, 51)));
	}

	@Override
	public void onItemUse(Player p, ItemStack item) {
		if (item.getType() != Material.IRON_SWORD)
			return;
		int level = getLevel(p);
		if (!addCooldown(p, getName(), 30, true))
			return;

		Set<Player> targetPlayers = UtilPlayer.getNearbyPlayers(p.getLocation(), 6);
		if (targetPlayers.size() <= 1)
			removeCooldowns(p);
		
		targetPlayers.forEach(target -> {
			if (target != p)
				target.addPotionEffect(
						new PotionEffect(PotionEffectType.SLOW, 200, 1));
		});
		p.getWorld().playEffect(p.getLocation(), Effect.ENDER_SIGNAL, 1);
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 1.0F, 1.0F);
		shootParticlesFromLoc(p, ParticleEffect.SMOKE_LARGE, 500, 0.5F);
	}

	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Use your sword ability to slow", "§7all players around you");
	}

}
