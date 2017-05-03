package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class KitDefault extends Kit {

	public KitDefault(KitManager kitManager) {
		super(kitManager, "Default", KitType.DEFAULT, 5000, "Default kit");
		setIcon(Material.DIAMOND_SWORD);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.DIAMOND_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level == 3 ? 1 : 0).build());
		
		p.getInventory().setHelmet(new ItemBuilder(Material.IRON_HELMET).addEnchantment(Enchantment.PROTECTION_PROJECTILE, level == 3 ? 1 : 0).build());
		p.getInventory().setChestplate(new ItemBuilder(Material.IRON_CHESTPLATE).build());
		p.getInventory().setLeggings(new ItemBuilder(level == 1 ? Material.LEATHER_LEGGINGS : Material.IRON_LEGGINGS).build());
		p.getInventory().setBoots(new ItemBuilder(Material.IRON_BOOTS).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level == 3 ? 1 : 0).build());
	}

}
