package net.skycade.kitpvp.ui;

import net.skycade.SkycadeCore.utility.CoreUtil;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPStats;
import net.skycade.kitpvp.ui.eventshopitems.EventShopItem;
import net.skycade.kitpvp.ui.eventshopitems.EventShopManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class EventShopMenu implements Listener {

    private Inventory menu;
    private Inventory confirmMenu;
    private final EventShopManager eventShopManager;

    private Map<String, Long> purchasedUpgrades = new HashMap<>();
    private final Map<UUID, EventShopItem> chosenUpgradeMap = new HashMap<>();

    public EventShopMenu(EventShopManager eventShopManager) {
        this.eventShopManager = eventShopManager;
        if (!KitPvP.getInstance().getConfig().getString("event-shop-enabled").equals("false")){
            menu = Bukkit.createInventory(null, MenuSize.TWO_LINE.getSize(), "§aEvent Shop");
        }
        confirmMenu = Bukkit.createInventory(null, MenuSize.THREE_LINE.getSize(), "§aEvent Shop Confirm");
    }

    private void updateValues(Member member) {
        menu.clear();

        if (eventShopManager.getYaml().contains(member.getUUID().toString())) {
            ConfigurationSection main = eventShopManager.getYaml().getConfigurationSection(member.getUUID().toString());
            for (String handle : main.getKeys(false)) {
                addPurchasedUpgrades(handle, eventShopManager.getYaml().getLong(member.getUUID().toString() + "." + handle));
            }
        }

        Collection<EventShopItem> items = (eventShopManager.getEventShopItems().values());
        for (EventShopItem item : items) {
            if (purchasedUpgrades.containsKey(item.getName()) && (System.currentTimeMillis() - purchasedUpgrades.get(item.getName())) / 1000L < item.getDuration()) {
                menu.addItem(new ItemBuilder(Material.BEDROCK).setName(item.getName()).build());
            } else {
                //building lore
                ArrayList <String> lore = new ArrayList<>(item.getDescription());

                //adding lore and name to item
                ItemStack displayItem = new ItemStack(item.getIcon());
                ItemMeta meta = displayItem.getItemMeta();
                meta.setLore(lore);
                meta.setDisplayName(item.getName());
                displayItem.setItemMeta(meta);

                //adding item to inventory
                menu.addItem(displayItem);
            }
        }
    }

    private void openConfirmMenu(Member member) {
        if (!chosenUpgradeMap.containsKey(member.getUUID())) {
            member.message("You didn't choose an upgrade.");
            return;
        }
        EventShopItem chosenItem = chosenUpgradeMap.get(member.getUUID());
        confirmMenu.setItem(11, new ItemBuilder(new ItemStack(Material.STAINED_GLASS, 1, (short) 5)).setName("§aBuy " + chosenItem.getName()).build());
        confirmMenu.setItem(15, new ItemBuilder(new ItemStack(Material.STAINED_GLASS, 1, (short) 14)).setName("§cDecline").build());
        member.getPlayer().openInventory(confirmMenu);
    }

    public void open(Member member) {
        updateValues(member);
        member.getPlayer().openInventory(menu);
    }

    @EventHandler
    public void onShopClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getName() != null && e.getClickedInventory().getName().equalsIgnoreCase("§aEvent Shop"))
            e.setCancelled(true);
        else
            return;
        if (!(e.getWhoClicked() instanceof Player))
            return;

        EventShopItem clickedItem = eventShopManager.getTypeFromString(e.getCurrentItem().getItemMeta().getDisplayName());
        if (clickedItem == null)
            return;

        Member member = MemberManager.getInstance().getMember((Player) e.getWhoClicked());
        KitPvPStats stats = eventShopManager.getKitPvP().getStats(member);
        long now = System.currentTimeMillis();
        if (purchasedUpgrades.containsKey(clickedItem.getName())) {
            long diff = (now - purchasedUpgrades.get(clickedItem.getName())) / 1000L;

            if (diff < clickedItem.getDuration()) {
                member.message(ChatColor.RED + "You need to wait another " + CoreUtil.niceFormat(clickedItem.getDuration() - ((Long) diff).intValue()) + " before purchasing this upgrade again!");
                return;
            }
        }

        if (stats.getEventCoins() - clickedItem.getPrice() < 0) {
            member.message("§7You don't have enough §aevent tokens §7to buy this upgrade.");
            return;
        }

        chosenUpgradeMap.put(member.getUUID(), clickedItem);
        Bukkit.getScheduler().runTaskLater(eventShopManager.getKitPvP(), () -> openConfirmMenu(member), 1);
    }

    @EventHandler
    public void onConfirmClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getName() != null && e.getClickedInventory().getName().equalsIgnoreCase("§aEvent Shop Confirm"))
            e.setCancelled(true);
        else
            return;
        if (!(e.getWhoClicked() instanceof Player))
            return;

        ItemStack item = e.getCurrentItem();
        if (item == null || item.getItemMeta().getDisplayName() == null)
            return;
        String name = item.getItemMeta().getDisplayName();
        Member member = MemberManager.getInstance().getMember((Player) e.getWhoClicked());

        if (name.contains("Buy")) {
            KitPvPStats stats = eventShopManager.getKitPvP().getStats(member);
            EventShopItem chosenItem = chosenUpgradeMap.get(member.getUUID());
            if (chosenItem == null)
                return;
            if (stats.getEventCoins() - chosenItem.getPrice() < 0)
                return;

            member.message("§7You bought §a" + chosenItem.getName() + "§7 for §a" + chosenItem.getPrice() + "§7 event tokens.");
            chosenItem.giveReward(((Player) e.getWhoClicked()).getPlayer());
            stats.setEventCoins(stats.getEventCoins() - chosenItem.getPrice());

            ScoreboardInfo.getInstance().updatePlayer(member.getPlayer());
            MemberManager.getInstance().update(member);
            Bukkit.getScheduler().runTaskLater(eventShopManager.getPlugin(), () -> member.getPlayer().closeInventory(), 1);
            return;
        }

        Bukkit.getScheduler().runTaskLater(eventShopManager.getKitPvP(), () -> open(member), 1);
    }

    public Map<String, Long> getPurchasedUpgrades() {
        return purchasedUpgrades;
    }

    private void addPurchasedUpgrades(String key, Long time) {
        if (purchasedUpgrades != null){
            purchasedUpgrades.forEach((k, l) -> purchasedUpgrades.put(k, l));
        }
        purchasedUpgrades.put(key, time);
    }
}
