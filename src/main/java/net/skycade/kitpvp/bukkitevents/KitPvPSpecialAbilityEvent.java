package net.skycade.kitpvp.bukkitevents;

import net.skycade.kitpvp.kit.KitType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KitPvPSpecialAbilityEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private KitType kitType;

    public KitPvPSpecialAbilityEvent(Player p, KitType kit) {
        player = p;
        kitType = kit;
    }

    public Player getPlayer() {
        return player;
    }

    public KitType getKitType() {
        return kitType;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
