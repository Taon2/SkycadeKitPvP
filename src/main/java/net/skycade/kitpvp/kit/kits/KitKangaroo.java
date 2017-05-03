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
import java.util.List;

public class KitKangaroo extends Kit {

	public KitKangaroo(KitManager kitManager) {
		super(kitManager, "Kangaroo", KitType.KANGAROO, 35000, "Jump everywhere!");
		setIcon(new ItemBuilder(new ItemStack(Material.POTION, 1, (short) 8267)).build());
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.GOLD_SWORD).addEnchantment(Enchantment.DURABILITY, 10).addEnchantment(Enchantment.DAMAGE_ALL, level + 1).build());
		p.getInventory().setArmorContents(getArmour(Material.LEATHER_HELMET, 12, level == 3 ? 4 : 3, Color.SILVER));
	}
	
	@Override
	public void onItemUse(Player p, ItemStack item) {
		if (item.getType() != Material.GOLD_SWORD)
			return;
		if (!addCooldown(p, getName(), 15 - (getLevel(p) * 3), true))
			return;
		if (getLevel(p) == 1)
			p.setVelocity(new Vector(p.getLocation().getDirection().getX(), 0.15, p.getLocation().getDirection().getZ()).multiply(4));
		else {
			p.setVelocity(new Vector(p.getLocation().getDirection().getX(), 0.15, p.getLocation().getDirection().getZ()).multiply(4));
			Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> p.setVelocity(new Vector(p.getLocation().getDirection().getX(), 0.15, p.getLocation().getDirection().getZ()).multiply(3)), 3);
		}
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Use your sword to mega jump", "§7you will be able to jumper further with", "§7a higher level");
	}

}
