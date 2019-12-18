package net.skycade.kitpvp.bukkitevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KitPvPEventStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;

    public KitPvPEventStartEvent(Player p) {
        player = p;
    }

    public Player getPlayer() {
        return player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
