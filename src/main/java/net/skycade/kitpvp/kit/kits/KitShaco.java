package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitShaco extends Kit {
	
	private final Map<UUID, ItemStack[]> shacoArmor = new HashMap<>();
	private final List<UUID> snowballCooldown = new ArrayList<>();

	public KitShaco(KitManager kitManager) {
		super(kitManager, "Shaco", KitType.SHACO, 42000, "Now you see me, now you don't");
		setIcon(Material.FERMENTED_SPIDER_EYE);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level < 3 ? level + 1 : 3).build());
		p.getInventory().setArmorContents(getArmour(Material.LEATHER_HELMET, 12, level, Color.BLACK));
		p.getInventory().addItem(new ItemBuilder(Material.SNOW_BALL, level == 1 ? 5 : 7).build());
		startItemRunnable(p, 25 - (level * 5), new ItemBuilder(Material.SNOW_BALL).build(), 8, KitType.SHACO);
	}
	
	
	@Override
	public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		if (!(damager.hasPotionEffect(PotionEffectType.INVISIBILITY)))
			return;
		double diffX = damager.getLocation().getDirection().getX() - damagee.getLocation().getDirection().getX();
		double diffZ = damager.getLocation().getDirection().getZ() - damagee.getLocation().getDirection().getZ();
		
		if (diffX > 0 && diffX < 1 || diffX < 0 && diffX > -1) {
			if (diffZ > 0 && diffZ < 1 || diffZ < 0 && diffZ > -1) {
				damagee.sendMessage("§7You got backstabbed");
				e.setDamage(e.getDamage() * 1.3);
			}
		}
	}
	
	@Override
	public void onItemUse(Player p, ItemStack item) {
		if (item.getType() != Material.IRON_SWORD)
			return;
		int level = getLevel(p);
		if (level == 1)
			return;
		if (!addCooldown(p, getName(), level == 2 ? 40 : 30, true))
			return;
		p.sendMessage("§fPoof!");
		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, level == 2 ? 140 : 200, 1));
		
		shacoArmor.put(p.getUniqueId(), p.getInventory().getArmorContents());
		p.getInventory().setArmorContents(null);
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, level == 2 ? 140 : 200, 0));
		
		Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> {
			if (shacoArmor.containsKey(p.getUniqueId())) {
				p.getInventory().setArmorContents(shacoArmor.get(p.getUniqueId()));
				shacoArmor.remove(p.getUniqueId());
			}
		},  level == 2 ? 140 : 200);
	}
	
	public void onSnowballUse(Player p, ProjectileLaunchEvent e) {
		if (snowballCooldown.contains(p.getUniqueId())) {
			e.setCancelled(true);
			return;
		}
		snowballCooldown.add(p.getUniqueId());
		p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() + 1);
		p.updateInventory();
		Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> snowballCooldown.remove(p.getUniqueId()), 10);
		e.getEntity().setVelocity(e.getEntity().getVelocity().multiply(2.5D));
	}
	
	public void onSnowballHit(Player shooter, Player damagee) {
		if (shooter.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
			teleportBehindPlayer(shooter, damagee.getLocation());
		} else {
			Location shooterLoc = shooter.getLocation();
			Location damageeLoc = damagee.getLocation();
			damagee.teleport(shooterLoc);
			shooter.teleport(damageeLoc);
			shooter.sendMessage("§9Positions switched!");
			damagee.sendMessage("§9Positions switched!");
		}
		damagee.damage(8, shooter);
		shacoHit.add(damagee.getUniqueId());
		Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> shacoHit.remove(damagee.getUniqueId()), 5 * 20);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		shacoArmor.remove(e.getPlayer().getUniqueId());
		snowballCooldown.remove(e.getPlayer().getUniqueId());
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Use the snowballs to switch positions", "§7use your sword to become invis, you will get", "§7this ability on level 2", "§7hitting someone with a snowball while invis will", "§7teleport you behind the target");
	}
	
}
