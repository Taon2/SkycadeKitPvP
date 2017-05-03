package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class KitSupport extends Kit {

	public KitSupport(KitManager kitManager) {
		super(kitManager, "Support", KitType.SUPPORT, 17000, "A real team player");
		setIcon(Material.BEACON);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.STONE_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level - 1).build());
		p.getInventory().setArmorContents(getArmour(Material.LEATHER_HELMET, 12, level == 1 ? 4 : 5, Color.fromBGR(153, 255, 153)));
	}
	
	@Override
	public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		damagee.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 0));
	}
	
	@Override
	public void onItemUse(Player p, ItemStack item) {
		if (item.getType() != Material.STONE_SWORD)
			return;
		int level = getLevel(p);
		if (level == 1)
			return;
		if (!addCooldown(p, getName(), 45 - (level * 5), true))
			return;
		
		Set<Player> targetPlayers = UtilPlayer.getNearbyPlayers(p.getLocation(), 5);
		if (targetPlayers.size() <= 1) {
			removeCooldowns(p);
			return;
		}
		
		targetPlayers.forEach(target -> {
				target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, level == 3 ? 200 : 100 + ((level - 1) * 50), 1));
				target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, level == 3 ? 200 : 100 + ((level - 1) * 50), 1));
		});
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WOLF_HOWL, 1.0F, 1.0F);
		shootParticlesFromLoc(p, ParticleEffect.PORTAL, 500, 1);
	}

	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Use your sword ability to give players", "§7around you a speed and resistance effect");
	}
}
