package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitSniper extends Kit {
	
	private final List<UUID> bowCooldown = new ArrayList<>();
	private final Map<UUID, UUID> sniperPlayerHit = new HashMap<>();
	private final Map<UUID, Integer> sniperCombo = new HashMap<>();

	public KitSniper(KitManager kitManager) {
		super(kitManager, "Sniper", KitType.SNIPER, 41000, "Take time for your shots");
		setIcon(Material.GHAST_TEAR);

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("inventory.bow.material", "BOW");
		defaultsMap.put("inventory.bow.enchantments.durability", 5);
		defaultsMap.put("inventory.bow.enchantments.arrow-infinite", 1);
		defaultsMap.put("inventory.bow.enchantments.knockback", 1);
		defaultsMap.put("inventory.bow.enchantments.arrow-damage", 3);

		defaultsMap.put("armor.material", "LEATHER");
		defaultsMap.put("armor.enchantments.durability", 5);
		defaultsMap.put("armor.enchantments.protection", 2);

		setConfigDefaults(defaultsMap);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(
				Material.getMaterial(getConfig().getString("inventory.bow.material").toUpperCase()))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.bow.enchantments.durability"))
				.addEnchantment(Enchantment.ARROW_INFINITE, getConfig().getInt("inventory.bow.enchantments.arrow-infinite"))
				.addEnchantment(Enchantment.KNOCKBACK, getConfig().getInt("inventory.bow.enchantments.knockback"))
				.addEnchantment(Enchantment.ARROW_DAMAGE, getConfig().getInt("inventory.bow.enchantments.arrow-damage")).build());

		p.getInventory().addItem(new ItemBuilder(
				Material.ARROW, 1).build());

		p.getInventory().setArmorContents(getArmour(
				Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
				getConfig().getInt("armor.enchantments.durability"),
				getConfig().getInt("armor.enchantments.protection"),
				Color.fromBGR(0, 60, 0)));
	}

	public void onArrowLaunch(Player shooter, ProjectileLaunchEvent e) {
		if (bowCooldown.contains(shooter.getUniqueId())) {
			e.setCancelled(true);
			return;
		}
		bowCooldown.add(shooter.getUniqueId());
		Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> bowCooldown.remove(shooter.getUniqueId()), 20);
	}

	public void onArrowHit(Player shooter, Player damagee, EntityDamageByEntityEvent e) {
		int level = getLevel(shooter);
		if (UtilMath.getRandom(0, 100) < level * 3)
			damagee.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, level == 3 ? 120 : 90, 0));
		
		if (!sniperPlayerHit.containsKey(shooter.getUniqueId()))
			sniperPlayerHit.put(shooter.getUniqueId(), damagee.getUniqueId());

		if (damagee.getUniqueId().equals(sniperPlayerHit.get(shooter.getUniqueId()))) {
			if (sniperCombo.containsKey(shooter.getUniqueId()))
				sniperCombo.put(shooter.getUniqueId(), sniperCombo.get(shooter.getUniqueId()) + 1);
			else 
				sniperCombo.put(shooter.getUniqueId(), 1);
			shooter.setLevel(sniperCombo.get(shooter.getUniqueId()));
			if (!hasArmor(damagee))
				return;
			int combo = sniperCombo.get(shooter.getUniqueId());
			double increasedDamage = 1 + (combo < 10 ? combo * 0.05 : 0.5);
			e.setDamage(e.getDamage() * increasedDamage);
		} else {
			sniperCombo.put(shooter.getUniqueId(), 1);
			sniperPlayerHit.put(shooter.getUniqueId(), damagee.getUniqueId());
		}
	}

	@EventHandler
	public void on(PlayerDeathEvent e) {
		sniperCombo.remove(e.getEntity().getUniqueId());
		sniperPlayerHit.remove(e.getEntity().getUniqueId());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		sniperCombo.remove(e.getPlayer().getUniqueId());
		sniperPlayerHit.remove(e.getPlayer().getUniqueId());
		bowCooldown.remove(e.getPlayer().getUniqueId());
	}
	
	private boolean hasArmor(Player p) {
		for (ItemStack item : p.getInventory().getArmorContents())
			if (item != null)
				if (item.getType() != Material.AIR)
					return true;
		return false;
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Your arrows will deal more", "§7damage if you combo someone");
	}

}
