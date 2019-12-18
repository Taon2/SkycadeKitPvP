package net.skycade.kitpvp.bukkitevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KitPvPKillstreakChange extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private int killstreak;

    public KitPvPKillstreakChange(Player p, int ks) {
        player = p;
        killstreak = ks;
    }

    public Player getPlayer() {
        return player;
    }

    public int getKillstreak() {
        return killstreak;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
