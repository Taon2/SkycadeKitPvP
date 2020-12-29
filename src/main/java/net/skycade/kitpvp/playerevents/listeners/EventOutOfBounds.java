package net.skycade.kitpvp.playerevents.listeners;

import net.skycade.SkycadeCore.vanish.VanishStatus;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.Messages;
import net.skycade.kitpvp.playerevents.EventManager;
import net.skycade.kitpvp.playerevents.EventType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class EventOutOfBounds implements Listener {
    private KitPvP plugin;
    public EventOutOfBounds(){
        plugin = KitPvP.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void move(PlayerMoveEvent event){
        Player player = event.getPlayer();
        EventManager m = getEventManager();
        if (m.isSpectating(player)){
            if (VanishStatus.isVanished(player.getUniqueId()))return;

            Location to = event.getTo();

            Location sumo1 = (Location) plugin.getConfig().get("player-events.sumo.region.pos1");
            Location sumo2 = (Location) plugin.getConfig().get("player-events.sumo.region.pos2");

            Location brackets1 = (Location) plugin.getConfig().get("player-events.brackets.region.pos1");
            Location brackets2 = (Location) plugin.getConfig().get("player-events.brackets.region.pos2");

            Location lms1 = (Location) plugin.getConfig().get("player-events.lms.region.pos1");
            Location lms2 = (Location) plugin.getConfig().get("player-events.lms.region.pos2");

            if (m.getCurrentEvent() == EventType.SUMO) {
                if (!isInside(to, sumo1, sumo2)) {
                    Location lobby = getEventManager().getSumo().getLobbyLocation();

                    player.teleport(new Location(lobby.getWorld(), lobby.getX(), lobby.getY(), lobby.getZ(), lobby.getYaw(), lobby.getPitch()));

                    Messages.OUT_OF_BOUNDS.msg(player);
                    return;
                }
            }
            if (m.getCurrentEvent() == EventType.BRACKETS) {
                if (!isInside(to, brackets1, brackets2)) {
                    Location lobby = getEventManager().getBrackets().getLobbyLocation();

                    player.teleport(new Location(lobby.getWorld(), lobby.getX(), lobby.getY(), lobby.getZ(), lobby.getYaw(), lobby.getPitch()));

                    Messages.OUT_OF_BOUNDS.msg(player);
                    return;
                }
            }
            if (m.getCurrentEvent() == EventType.LMS) {
                if (!isInside(to, lms1, lms2)) {
                    Location lobby = getEventManager().getLMS().getLobbyLocation();

                    player.teleport(new Location(lobby.getWorld(), lobby.getX(), lobby.getY(), lobby.getZ(), lobby.getYaw(), lobby.getPitch()));

                    Messages.OUT_OF_BOUNDS.msg(player);
                }
            }
        }
    }

    public boolean isInside(Location loc, Location l1, Location l2) {
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        int x1 = Math.min(l1.getBlockX(), l2.getBlockX());
        int y1 = Math.min(l1.getBlockY(), l2.getBlockY());
        int z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
        int x2 = Math.max(l1.getBlockX(), l2.getBlockX());
        int y2 = Math.max(l1.getBlockY(), l2.getBlockY());
        int z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());

        return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2;
    }

    public EventManager getEventManager(){
        return KitPvP.getInstance().getEventManager();
    }
}
