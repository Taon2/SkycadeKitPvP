package net.skycade.kitpvp.ui.eventshopitems;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class EventShopItem {

    private final EventShopManager eventShopManager;
    private final String name;
    private ItemStack icon;
    private int price;
    private int duration;

    public EventShopItem(EventShopManager eventShopManager, String name, ItemStack icon, int price, int duration) {
        this.eventShopManager = eventShopManager;
        this.name = name;
        this.icon = icon;
        this.price = price;
        this.duration = duration;
    }

    public EventShopManager getEventShopManager() {
        return eventShopManager;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int value) {
        this.price = value;
    }

    public int getDuration() {
        return duration;
    }

    public ItemStack getIcon() {
        if (icon == null) {
            icon = new ItemStack(Material.WOOD_SWORD);
        }

        return icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public void setIcon(Material icon) {
        this.icon = new ItemStack(icon);
    }

    public abstract void giveReward(Player p);

    public abstract void reapplyReward(Player p);

    public abstract List<String> getDescription();
}
