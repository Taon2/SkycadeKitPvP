package net.skycade.kitpvp.coreclasses.utils;

import net.skycade.kitpvp.listeners.player.LastMoveListener;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UtilPlayer {

	private static final HashMap<UUID, PermissionAttachment> perms = new HashMap<>();

	public static PermissionAttachment getAttachment(Player player, JavaPlugin plugin) {
		if (perms.containsKey(player.getUniqueId())) {
			return perms.get(player.getUniqueId());
		}
		PermissionAttachment perm = player.addAttachment(plugin);
		perms.put(player.getUniqueId(), perm);
		return perm;
	}

	public static void removeAttachment(Player player) {
		perms.remove(player.getUniqueId());
	}

	public static void reset(Player p) {
		for (PotionEffect effect : p.getActivePotionEffects())
			p.removePotionEffect(effect.getType());
		p.setWalkSpeed(0.2F);
		p.setAllowFlight(false);
		p.setFlying(false);
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		p.setMaxHealth(20);
		p.setHealth(20);
		p.setFoodLevel(20);
		p.setSaturation(20);
		p.setExp(0);
		p.setLevel(0);
		p.setFireTicks(0);
		p.setGameMode(GameMode.SURVIVAL);
		if (p.getVehicle() != null)
			p.leaveVehicle();
		if (p.getPassenger() != null)
			p.getPassenger().leaveVehicle();
	}

    public static Set<Player> getNearbyPlayers(Location loc, double r) {
        Set<Player> players = new HashSet<>();

        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (pl.getLocation().distanceSquared(loc) <= r * r) {
                players.add(pl);
            }
        }

        return players;
    }

	public static void sendPacket(Player p, Object packet) {
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket((Packet<?>) packet);
	}

    public static boolean isMoving(Player p) {
		return LastMoveListener.getInstance().getLastMoved().containsKey(p.getUniqueId()) && (System.currentTimeMillis() - LastMoveListener.getInstance().getLastMoved().get(p.getUniqueId())) < 250;
	}

}