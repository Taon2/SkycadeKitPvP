package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitHyper extends Kit {

	public KitHyper(KitManager kitManager) {
		super(kitManager, "Hyper", KitType.HYPER, 28000, "Sugar makes him go crazy");

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("kit.icon.material", "SUGAR");
		defaultsMap.put("kit.icon.color", "BLACK");
		defaultsMap.put("kit.price", 28000);

		defaultsMap.put("inventory.sword.material", "IRON_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.enchantments.knockback", 1);
		defaultsMap.put("inventory.sword.enchantments.damage-all", 0);

		defaultsMap.put("armor.helmet.material", "LEATHER");
		defaultsMap.put("armor.helmet.enchantments.durability", 3);

		defaultsMap.put("armor.chestplate.material", "IRON");

		defaultsMap.put("armor.leggings.material", "IRON");

		defaultsMap.put("armor.boots.material", "IRON");

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
				.addEnchantment(Enchantment.KNOCKBACK, getConfig().getInt("inventory.sword.enchantments.knockback"))
				.addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

		p.getInventory().addItem(new ItemBuilder(
				Material.SUGAR).build());
		
		p.getInventory().setHelmet(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.helmet.material").toUpperCase() + "_HELMET"))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.helmet.enchantments.durability"))
				.setColour(Color.BLACK).build());

		p.getInventory().setChestplate(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.chestplate.material").toUpperCase() + "_CHESTPLATE")).build());

		p.getInventory().setLeggings(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.leggings.material").toUpperCase() + "_LEGGINGS")).build());

		p.getInventory().setBoots(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.boots.material").toUpperCase() + "_BOOTS")).build());
	}
	
	@Override
	public void onItemUse(Player p, ItemStack item) {
		if (item.getType() != Material.SUGAR)
			return;
		if (!addCooldown(p, getName(), 3 * 10, true))
			return;
		p.addPotionEffects(Arrays.asList(new PotionEffect(PotionEffectType.SPEED, 50 + 3 * 100, 2), new PotionEffect(PotionEffectType.REGENERATION, 50 + 3 * 100, 1)));
		p.getWorld().playEffect(p.getLocation(), Effect.ENDER_SIGNAL, 0);
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Use sugar to gain speed and regeneration", "§7the effects are longer if you have a", "§7higher level");
	}

}
