package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class KitUnicorn extends Kit {

	private final Color[] rainbowColors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.AQUA, Color.BLUE, Color.PURPLE, Color.FUCHSIA};
	private final List<UUID> rodUse = new ArrayList<>();
	
	public KitUnicorn(KitManager kitManager) {
		super(kitManager, "Unicorn", KitType.UNICORN, 40000, "Be a mythical creature");
		setIcon(new ItemStack(Material.HAY_BLOCK));
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level + 2).addEnchantment(Enchantment.DURABILITY, 14).setColour(Color.PURPLE).build());
		p.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level + 2).addEnchantment(Enchantment.DURABILITY, 12).setColour(Color.WHITE).build());
		p.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level + 1).addEnchantment(Enchantment.DURABILITY, 12).setColour(Color.WHITE).build());
		p.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).addEnchantment(Enchantment.DURABILITY, 12).setColour(Color.WHITE).build());
		p.getInventory().addItem(new ItemBuilder(Material.STICK).addEnchantment(Enchantment.DAMAGE_ALL, level == 3 ? 6 : 5).build());
	}
	
	@Override
	public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		if (UtilMath.getRandom(0, 100) <= getLevel(damager)) {
			damager.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100 + (20 * getLevel(damager)), 3));
			damager.sendMessage("§fSwing speed up!");
		}
	}
	
	@Override
	public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		if (UtilMath.getRandom(0, 100) <= getLevel(damager)) {
			damagee.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100 + (20 * getLevel(damager)), 2));
			damagee.sendMessage("§fDefence up!");
			shootParticlesFromLoc(damagee, ParticleEffect.WATER_WAKE, 500, 0.3F);
		}
	}
	
	@Override
	public void onItemUse(Player p, ItemStack item) {
		int level = getLevel(p);
		if (item.getType() == Material.STICK) {
			if (rodUse.contains(p.getUniqueId()))
				return;
			rodUse.add(p.getUniqueId());
			Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> rodUse.remove(p.getUniqueId()), level == 3 ? 70 : 100);
			
			new BukkitRunnable() {
				Location loc = p.getEyeLocation().subtract(0, 0.2, 0);
				Vector dir = p.getLocation().getDirection().normalize();
				double t = 0.0;

				public void run() {
					t += 0.07F;
					double x = dir.getX() * t;
					double y = dir.getY() * t;
					double z = dir.getZ() * t;
					loc.add(x, y, z);

					for (int i = 0; i < 3; i ++)
						for (Color col : rainbowColors)
							ParticleEffect.SPELL_MOB.display(new ParticleEffect.OrdinaryColor(col), loc, 30);
					if (UtilMath.getRandom(0, 3) == 2)
						ParticleEffect.LAVA.display(0, 0, 0, 0, 3, loc, 30);

					for (Player target : UtilPlayer.getNearbyPlayers(loc, 1).stream().filter(player -> !player.equals(p) && player.getGameMode() == GameMode.SURVIVAL).collect(Collectors.toList())) {
						target.damage(8 + (level * 2), p);
						target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
					}
					if (t > 1.7)
						this.cancel();
				}
			}.runTaskTimer(getKitManager().getPlugin(), 0, 1);
		}
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7You got a chance to gain", "§7damage resistance when getting hit", "§7and a chance to get haste when hitting someone", "§7use your wand to shoot a rainbow");
	}

}
