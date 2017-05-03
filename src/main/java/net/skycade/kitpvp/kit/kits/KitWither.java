package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class KitWither extends Kit {

	public KitWither(KitManager kitManager) {
		super(kitManager, "Wither", KitType.WITHER, 36000, "Wither can be an annoying guy");
		setIcon(Material.NETHER_STALK);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DAMAGE_ALL, level == 3 ? 2 : 1).addEnchantment(Enchantment.DURABILITY, 5).build());
		p.getInventory().setArmorContents(getArmour(Material.LEATHER_HELMET, 8 + (level * 2), 4, Color.BLACK));
	}
	
	@Override
	public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		witherEffect(damagee, getLevel(damager) * 4, getLevel(damager) * 2);
	}

	private void witherEffect(Player p, int chance, int duration) {
		int random = UtilMath.getRandom(0, 100);
		if (random <= chance)
			p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, duration * 20, 1));
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7There is a chance to give players", "§7a wither effect when you hit them");
	}

}
