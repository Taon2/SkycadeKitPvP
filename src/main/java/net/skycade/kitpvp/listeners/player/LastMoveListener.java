package net.skycade.kitpvp.listeners.player;

import net.skycade.kitpvp.KitPvP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LastMoveListener implements Listener {

    private static LastMoveListener instance;
    private final Map<UUID, Long> lastMoved = new HashMap<>();

    public LastMoveListener() {
        instance = this;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom(), to = event.getTo();
        if (Math.abs(from.getX() - to.getX()) > 0 || Math.abs(from.getY() - to.getY()) > 0 || Math.abs(from.getZ() - to.getZ()) > 0)
            lastMoved.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        lastMoved.remove(event.getPlayer().getUniqueId());
    }

    public static LastMoveListener getInstance() {
        if (instance == null) {
            instance = new LastMoveListener();
            Bukkit.getPluginManager().registerEvents(instance, KitPvP.getInstance());
        }
        return instance;
    }

    public Map<UUID, Long> getLastMoved() {
        return lastMoved;
    }

}
