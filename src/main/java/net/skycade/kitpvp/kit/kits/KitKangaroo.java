package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitKangaroo extends Kit {

	public KitKangaroo(KitManager kitManager) {
		super(kitManager, "Kangaroo", KitType.KANGAROO, 35000, "Jump everywhere!");

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("kit.icon.material", "POTION");
		defaultsMap.put("kit.icon.color", "BLACK");
		defaultsMap.put("kit.price", 35000);

		defaultsMap.put("inventory.sword.material", "GOLD_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 10);
		defaultsMap.put("inventory.sword.enchantments.damage-all", 2);

		defaultsMap.put("armor.material", "LEATHER");
		defaultsMap.put("armor.enchantments.durability", 12);
		defaultsMap.put("armor.enchantments.protection", 3);

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
				.addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

		p.getInventory().setArmorContents(getArmour(
				Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
				getConfig().getInt("armor.enchantments.durability"),
				getConfig().getInt("armor.enchantments.protection"),
				Color.SILVER));
	}
	
	@Override
	public void onItemUse(Player p, ItemStack item) {
		if (item.getType() != Material.GOLD_SWORD)
			return;
		if (!addCooldown(p, getName(), 6, true))
			return;
		/* if (getLevel(p) == 1)
			p.setVelocity(new Vector(p.getLocation().getDirection().getX(), 0.15, p.getLocation().getDirection().getZ()).multiply(4));
		else { */
			p.setVelocity(new Vector(p.getLocation().getDirection().getX(), 0.15, p.getLocation().getDirection().getZ()).multiply(4));
			Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> p.setVelocity(new Vector(p.getLocation().getDirection().getX(), 0.15, p.getLocation().getDirection().getZ()).multiply(3)), 3);
		// }
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Use your sword to mega jump", "§7you will be able to jumper further with", "§7a higher level");
	}

}
