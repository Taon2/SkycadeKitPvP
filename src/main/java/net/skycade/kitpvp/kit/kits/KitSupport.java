package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitSupport extends Kit {

	public KitSupport(KitManager kitManager) {
		super(kitManager, "Support", KitType.SUPPORT, 17000, "A real team player");

		Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "BEACON");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 17000);

		defaultsMap.put("inventory.sword.material", "STONE_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.enchantments.damage-all", 0);

		defaultsMap.put("armor.material", "LEATHER");
		defaultsMap.put("armor.enchantments.durability", 12);
		defaultsMap.put("armor.enchantments.protection", 4);

		setConfigDefaults(defaultsMap);

		if (getConfig().getString("kit.icon.material") != null) {
			if (getConfig().getString("kit.icon.material").contains("LEATHER")) {
				setIcon(new ItemBuilder(Material.getMaterial(getConfig().getString("kit.icon.material").toUpperCase()))
						.setColour(getColor(getConfig().getString("kit.icon.color"))).build());
			} else {
				setIcon(new ItemStack(Material.getMaterial(getConfig().getString("kit.icon.material").toUpperCase())));
			}
		} else {
			setIcon(new ItemStack(Material.DIRT));
		}
		setPrice(getConfig().getInt("kit.price"));
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(
		        Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
                .addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.protection")).build());

		p.getInventory().setArmorContents(getArmour(
		        Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
                getConfig().getInt("armor.enchantments.durability"),
                getConfig().getInt("armor.enchantments.protection"),
                Color.fromBGR(153, 255, 153)));
	}
	
	@Override
	public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		damagee.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 0));
	}
	
	@Override
	public void onItemUse(Player p, ItemStack item) {
		if (item.getType() != Material.STONE_SWORD)
			return;
		int level = getLevel(p);
		if (!addCooldown(p, getName(), 30, true))
			return;
		
		Set<Player> targetPlayers = UtilPlayer.getNearbyPlayers(p.getLocation(), 5);
		if (targetPlayers.size() <= 1) {
			removeCooldowns(p);
			return;
		}
		
		targetPlayers.forEach(target -> {
				target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1));
				target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 1));
		});
		p.getWorld().playSound(p.getLocation(), Sound.WOLF_HOWL, 1.0F, 1.0F);
		shootParticlesFromLoc(p, ParticleEffect.PORTAL, 500, 1);
	}

	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Use your sword ability to give players", "§7around you a speed and resistance effect");
	}
}
