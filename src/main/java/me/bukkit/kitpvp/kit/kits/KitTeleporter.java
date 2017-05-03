package me.bukkit.kitpvp.kit.kits;

import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class KitTeleporter extends Kit {

	public KitTeleporter(KitManager kitManager) {
		super(kitManager, "Teleporter", KitType.TELEPORTER, 32000, "Where did he go?");
		setIcon(Material.ENDER_PEARL);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.DIAMOND_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level == 3 ? 1 : 0).build());
		p.getInventory().addItem(new ItemBuilder(Material.ENDER_PEARL, 5).build());
		p.getInventory().setHelmet(new ItemBuilder(Material.IRON_HELMET).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level > 1 ? 1 : 0).build());
		p.getInventory().setChestplate(new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).addEnchantment(Enchantment.DURABILITY, level).build());
		p.getInventory().setLeggings(new ItemBuilder(Material.CHAINMAIL_LEGGINGS).addEnchantment(Enchantment.DURABILITY, level).build());
		p.getInventory().setBoots(new ItemBuilder(Material.IRON_BOOTS).build());
		startItemRunnable(p, 50 - (level * 7), new ItemBuilder(Material.ENDER_PEARL).build(), 8, KitType.TELEPORTER);
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Collections.singletonList("§7You will regain epearls overtime");
	}

}
