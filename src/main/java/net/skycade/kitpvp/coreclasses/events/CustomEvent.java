package net.skycade.kitpvp.coreclasses.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class CustomEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private String cancelReason;

    public CustomEvent() {
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public String getCancelReason() {
        return this.cancelReason;
    }

    public void setCancelled(boolean cancelled) {
        this.setCancelled(cancelled, (String) null);
    }

    public void setCancelled(boolean cancelled, String cancelReason) {
        this.cancelled = cancelled;
        this.cancelReason = cancelReason;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}