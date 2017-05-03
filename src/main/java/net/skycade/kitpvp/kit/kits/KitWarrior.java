package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class KitWarrior extends Kit {

	public KitWarrior(KitManager kitManager) {
		super(kitManager, "Warrior", KitType.WARRIOR, 50000, "This warrior can survive a hit!");
		setIcon(Material.DIAMOND_CHESTPLATE);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(level == 1 ? Material.STONE_SWORD : Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.KNOCKBACK, level == 1 ? 1 : 0).addEnchantment(Enchantment.DAMAGE_ALL, level == 1 ? 1 : 0).build());
		p.getInventory().setArmorContents(getArmour(Material.DIAMOND_HELMET, 0, 0));
		p.getInventory().getArmorContents()[2].addEnchantment(Enchantment.THORNS, level);
		p.getInventory().getArmorContents()[2].addEnchantment(Enchantment.DURABILITY, level);
		p.getInventory().setHelmet(new ItemBuilder(p.getInventory().getArmorContents()[3]).addLore("§FDamage you receive is multiplied by " + getDamageMultiplier(level)).build());
	}
	
	@Override
	public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		e.setDamage(e.getDamage() * getDamageMultiplier(getLevel(damagee)));
	}
	
	private double getDamageMultiplier(int level) {
		if (level == 1)
			return 2;
		else if (level == 2)
			return 1.7;
		else 
			return 1.4;
	}

}
