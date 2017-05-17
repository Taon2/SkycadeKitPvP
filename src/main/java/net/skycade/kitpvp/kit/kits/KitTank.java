package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class KitTank extends Kit {

	public KitTank(KitManager kitManager) {
		super(kitManager, "Tank", KitType.TANK, 46000, "Slow but powerful");
		setIcon(Material.DIAMOND_HELMET);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(level == 1 ? Material.IRON_SWORD : Material.DIAMOND_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, 2).build());
		p.getInventory().setArmorContents(getArmour(Material.DIAMOND_HELMET, 0, 0));
		p.getInventory().setHelmet(new ItemBuilder(p.getInventory().getArmorContents()[3]).addLore("§FReceive 50% more damage.").build());
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0));
	}

	@Override
	public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		e.setDamage(e.getDamage() * 1.5);
	}
	
}
