package net.skycade.kitpvp.runnable;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemRunnable {

    private final KitPvP plugin;
    private final int seconds;
    private final Player p;
    private final ItemStack item;
    private final int maxAmount;
    private final KitType kit;
    private boolean stop = false;

    public ItemRunnable(KitPvP instance, int sec, Player player, ItemStack it, int max, KitType kit) {
        this.plugin = instance;
        this.seconds = sec;
        this.p = player;
        this.item = it;
        this.maxAmount = max;
        this.kit = kit;
        startRunnable();
    }

    public void startRunnable() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!canRun()) return;
            Inventory inv = p.getInventory();
            int amount = 0;

            Integer finalSlot = null;
            for (Integer i = 0; i < inv.getSize(); i++)
                if (inv.getItem(i) != null)
                    if (inv.getItem(i).getType() == item.getType()) {
                        amount += inv.getItem(i).getAmount();
                        if (amount <= inv.getMaxStackSize())
                            finalSlot = i;
                    }
            if (finalSlot != null && amount > 0 && plugin.getStats(p).getActiveKit() == kit) {
                ItemStack invItem = inv.getItem(finalSlot);
                if (amount < maxAmount)
                    invItem.setAmount(invItem.getAmount() + 1);
                    //inv.setItem(finalSlot, new ItemStack(invItem.getType(), invItem.getAmount() + 1));
            } else
                p.getInventory().addItem(item);
            startRunnable();
        }, seconds * 20);
    }

    private boolean canRun() {
        return !(p == null || Bukkit.getPlayer(p.getUniqueId()) == null) && plugin.getStats(p).getActiveKit() == kit && !stop;
    }

    public void stopRunnable() {
        stop = true;
    }
}
