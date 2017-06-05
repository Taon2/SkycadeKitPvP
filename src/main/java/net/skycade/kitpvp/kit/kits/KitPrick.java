package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Color;
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

public class KitPrick extends Kit {

	public KitPrick(KitManager kitManager) {
		super(kitManager, "Prick", KitType.PRICK, 22000, "A little spiky");

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("kit.icon.material", "CACTUS");
		defaultsMap.put("kit.icon.color", "BLACK");
		defaultsMap.put("kit.price", 22000);

		defaultsMap.put("inventory.sword.material", "IRON_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.enchantments.knockback", 1);
		defaultsMap.put("inventory.sword.enchantments.damage-all", 0);

		defaultsMap.put("armor.material", "LEATHER");
		defaultsMap.put("armor.enchantments.durability", 14);
		defaultsMap.put("armor.enchantments.protection", 2);
		defaultsMap.put("armor.enchantments.thorns", 1);

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

		p.getInventory().setArmorContents(getArmour(
				Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
				getConfig().getInt("armor.enchantments.durability"),
				getConfig().getInt("armor.enchantments.protection"),
				Color.GREEN));

		p.getInventory().addItem(new ItemBuilder(
				Material.CACTUS).build());

		for (ItemStack piece : p.getInventory().getArmorContents())
			piece.addEnchantment(Enchantment.THORNS, getConfig().getInt("armor.enchantments.thorns"));
	}
	
	@Override
	public void onInteract(Player p, Player target, ItemStack item) {
		if (item.getType() != Material.CACTUS)
			return;
		int level = getLevel(p);
		if (!addCooldown(p, getName(), 11, true))
			return;
		
		ItemStack[] armor = target.getEquipment().getArmorContents();
		target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 130, 1));

		for (ItemStack anArmor : armor)
			if (anArmor != null)
				anArmor.setDurability((short) (anArmor.getDurability() + 20));
		target.getInventory().setArmorContents(armor);
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Use the cactus to poison someone", "§7this has a big effect on the armor", "§7durability of your target");
	}

}
