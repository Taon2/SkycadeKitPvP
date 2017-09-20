package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitBarbarian extends Kit {

	public KitBarbarian(KitManager kitManager) {
		super(kitManager, "Barbarian", KitType.BARBARIAN, 7500, "RAWR!");

		Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "IRON_AXE");
		defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 7500);

		defaultsMap.put("inventory.axe.material", "IRON_AXE");
		defaultsMap.put("inventory.axe.enchantments.durability", 5);
		defaultsMap.put("inventory.armour.type", "IRON");
		defaultsMap.put("inventory.armour.durability", 0);
		defaultsMap.put("inventory.armour.protection", 0);

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
				Material.getMaterial(getConfig().getString("inventory.axe.material").toUpperCase()))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.axe.enchantments.durability")).build());

		ItemStack[] armor = getArmour(Material.getMaterial(
						getConfig().getString("inventory.armour.type") + "_HELMET"),
						getConfig().getInt("inventory.armour.durability"),
						getConfig().getInt("inventory.armour.protection"));

		p.getInventory().setArmorContents(armor);
	}

	@Override
	public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		if (getLevel(damagee) == 1)
			return;
		int chance = getLevel(damagee) * 2;
		if (UtilMath.getRandom(0, 100) <= chance) {
			if (damagee.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
				damagee.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
			damagee.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0));
			damagee.getWorld().playEffect(damagee.getLocation(), Effect.FLAME, 1);
			damagee.getWorld().playSound(damagee.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 1, 1);
			shootParticlesFromLoc(damagee, ParticleEffect.FLAME, 500, 0.3F);
		}
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7You got a chance to gain", "§7strength when you're getting hit", "§7higher level means a higher chance");
	}

}