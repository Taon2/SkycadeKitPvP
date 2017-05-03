package me.bukkit.kitpvp.kit.kits;

import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.*;

public class KitFisherman extends Kit {
	
	private final List<UUID> rodCd = new ArrayList<>();

	public KitFisherman(KitManager kitManager) {
		super(kitManager, "Fisherman", KitType.FISHERMAN, 48000, "This man is nothing without his fishing rod");
		setIcon(Material.FISHING_ROD);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level == 3 ? 1 : 0).build());
		p.getInventory().addItem(new ItemBuilder(Material.FISHING_ROD).addEnchantment(Enchantment.DURABILITY, 10).build());
		p.getInventory().setArmorContents(getArmour(Material.LEATHER_HELMET, level == 1 ? 4 : 6, 4, Color.fromBGR(255, 255, 200)));
	}
	
	public void onRodUse(Player p, ProjectileLaunchEvent e) {
		if (rodCd.contains(p.getUniqueId()))
			return;
		int level = getLevel(p);
		Location target = getTarget(p, 15 + (level * 5));
		if (target == null) 
			return;
		
		rodCd.add(p.getUniqueId());
		Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> rodCd.remove(p.getUniqueId()), 80);
		
		p.teleport(p.getLocation().add(0, 0.5, 0));
		Vector v = getVectorForPoints(p.getLocation(), target);
		e.getEntity().setVelocity(v);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(getKitManager().getPlugin(), () -> p.setVelocity(v), 5);
	}
	
	private Location getTarget(Player hookshooter, Integer amount) {
		for (Block block : hookshooter.getLineOfSight((Set<Material>) null, amount)) 
			if (!block.getType().equals(Material.AIR)) 
				return block.getLocation();
		return null;
	}
	
	private Vector getVectorForPoints(Location l1, Location l2) {
		double g = -0.08;
		double t = l2.distance(l1);
		double vX = (1.0 + 0.07 * t) * (l2.getX() - l1.getX()) / t;
		double vY = (1.0 + 0.03 * t) * (l2.getY() - l1.getY()) / t - 0.5 * g * t;
		double vZ = (1.0 + 0.07 * t) * (l2.getZ() - l1.getZ()) / t;
		return new Vector(vX, vY, vZ);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		rodCd.remove(e.getPlayer().getUniqueId());
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7You can use your rod as a", "§7grappling hook", "§7you can reach further if you", "§7have a higher level");
	}

}
