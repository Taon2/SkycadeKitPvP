package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class KitKing extends Kit {

	public KitKing(KitManager kitManager) {
		super(kitManager, "King", KitType.KING, 50000, "Not afraid to show his shiny crown");
		setIcon(Material.GOLD_HELMET);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level == 1 ? 0 : 1).build());
		p.getInventory().setArmorContents(getArmour(Material.IRON_HELMET, 0, 0));
		p.getInventory().setHelmet(new ItemBuilder(Material.GOLD_HELMET).addEnchantment(Enchantment.DURABILITY, level == 3 ? 5 : level).build());
	}

	@Override
	public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
		List<Entity> entities = damagee.getNearbyEntities(4, 4, 4).stream()
				.filter(en -> en instanceof Golem && en.getCustomName().contains(damagee.getName()))
				.collect(Collectors.toList());
		entities.forEach(golem -> ((Golem) golem).setTarget(damager));
	}
	
	@Override
	public void onItemUse(Player p, ItemStack item) {
		if (item.getType() != Material.IRON_SWORD)
			return;
		if (!addCooldown(p, getName(), 4 * 10, true))
			return;
		int level = getLevel(p);
		
		IronGolem golem = (IronGolem) p.getWorld().spawnEntity(p.getLocation(), EntityType.IRON_GOLEM);
		golem.setCustomName(p.getName() + " golem");

		/* if (level < 3)
			golem.setHealth(level == 1 ? golem.getMaxHealth() * 0.5 : golem.getMaxHealth() * 0.7); */
		
		Set<Player> nearbyPlayers = UtilPlayer.getNearbyPlayers(p.getLocation(), 5);
		if (nearbyPlayers.contains(p))
			nearbyPlayers.remove(p);
		
		if (!(nearbyPlayers.isEmpty()))
			golem.setTarget(getClosestTarget(nearbyPlayers, p));
		
		Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> {
			if (!(golem.isDead())) 
				golem.remove();
		}, 20 * 30);
	}
	
	private Player getClosestTarget(Set<Player> players, Player p) {
		double distance = 100;
		Player closestPlayer = null;
		
		for (Player target : players) 
			if (p.getLocation().distance(target.getLocation()) < distance) 
				closestPlayer = target;
		return closestPlayer;
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Use your sword to spawn a golem", "§7to fight for you");
	}
}
