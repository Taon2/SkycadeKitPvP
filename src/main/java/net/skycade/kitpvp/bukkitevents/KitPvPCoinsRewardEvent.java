package net.skycade.kitpvp.bukkitevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KitPvPCoinsRewardEvent extends Event implements Cancellable {
    private static HandlerList handlerList = new HandlerList();
    private final Player player;
    private final int coins;

    private int newCoins;

    private boolean cancel;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public KitPvPCoinsRewardEvent(Player player, int coins) {
        this.player = player;
        this.coins = coins;
        this.newCoins = coins;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public int getCoins() {
        return coins;
    }

    public int getNewCoins() {
        return newCoins;
    }

    public void setNewCoins(int newCoins) {
        this.newCoins = newCoins;
    }

    public Player getPlayer() {
        return player;
    }
}
