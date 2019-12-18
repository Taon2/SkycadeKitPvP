package net.skycade.kitpvp.bukkitevents;

import net.skycade.kitpvp.kit.KitType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KitPvPKillPlayerEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private KitType usedKitType;

    public KitPvPKillPlayerEvent(Player p, KitType kit) {
        player = p;
        usedKitType = kit;
    }

    public Player getPlayer() {
        return player;
    }

    public KitType getUsedKitType() {
        return usedKitType;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
