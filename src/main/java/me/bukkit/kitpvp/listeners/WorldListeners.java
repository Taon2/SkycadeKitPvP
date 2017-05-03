package me.bukkit.kitpvp.listeners;

import me.bukkit.kitpvp.KitPvP;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.Arrays;

public class WorldListeners implements Listener {

	private final KitPvP plugin;

	public WorldListeners(KitPvP plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void on(WeatherChangeEvent e) {
		World w = e.getWorld();
		if (!w.hasStorm())
			e.setCancelled(true);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			if (w.hasStorm())
				w.setStorm(false);
		}, 5);
	}

	@EventHandler
	public void on(EntityExplodeEvent e) {
		if (!(e.getEntity() instanceof TNTPrimed))
			return;
		for (Entity en : e.getEntity().getNearbyEntities(4, 4, 4)) 
			if (en instanceof FallingBlock) 
				e.getEntity().remove();
		e.setCancelled(true);
	}

	@EventHandler
	public void on(EntitySpawnEvent e) {
        if (!Arrays.asList(EntityType.ARMOR_STAND, EntityType.WOLF, EntityType.PRIMED_TNT, EntityType.IRON_GOLEM).contains(e.getEntityType()))
            e.setCancelled(true);
	}

}