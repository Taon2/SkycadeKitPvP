package net.skycade.kitpvp.ui;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ShopMenu implements Listener {

    private final Inventory menu;
    private final Inventory confirmMenu;
    private final KitManager kitManager;
    private final Map<UUID, KitType> chosenKitMap = new HashMap<>();

    public ShopMenu(KitManager kitManager) {
        this.kitManager = kitManager;
        menu = Bukkit.createInventory(null, getMenuSize(), "§aShop");
        confirmMenu = Bukkit.createInventory(null, MenuSize.THREE_LINE.getSize(), "§aConfirm");
    }

    private void updateValues(Member member) {
        menu.clear();
        KitPvPStats stats = kitManager.getKitPvP().getStats(member);

        List<KitType> kits = KitPvP.getInstance().getKitPvPDocManager().getCurrentKits();
        for (KitType k : kits) {
            Kit kit = k.getKit();
            if (stats.hasKit(k)) {
                menu.addItem(new ItemBuilder(Material.BEDROCK).setName("§c" + kit.getName()).build());
            } else
                menu.addItem(new ItemBuilder(kit.getIcon()).addLore(Arrays.asList("Price: §6" + kit.getPrice(), "", "§7Click to buy this kit")).setName("§a" + kit.getName()).build());
        }
    }

    private void openConfirmMenu(Member member) {
        if (!chosenKitMap.containsKey(member.getUUID())) {
            member.message("You didn't choose a kit.");
            return;
        }
        KitType chosenKit = chosenKitMap.get(member.getUUID());
        confirmMenu.setItem(11, new ItemBuilder(new ItemStack(Material.STAINED_GLASS, 1, (short) 5)).setName("§aBuy " + chosenKit.getKit().getName()).build());
        confirmMenu.setItem(15, new ItemBuilder(new ItemStack(Material.STAINED_GLASS, 1, (short) 14)).setName("§cDecline").build());
        member.getPlayer().openInventory(confirmMenu);
    }

    public void open(Member member) {
        updateValues(member);
        member.getPlayer().openInventory(menu);
    }

    private int getMenuSize() {
        int slots = KitPvP.getInstance().getConfig().getInt("kits-rotation-amount");
        int size = 9;
        while (slots - 9 > 0) {
            size += 9;
            slots -= 9;
        }
        return size;
    }

    @EventHandler
    public void onShopClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getName() != null && e.getClickedInventory().getName().equalsIgnoreCase("§aShop"))
            e.setCancelled(true);
        else
            return;
        if (!(e.getWhoClicked() instanceof Player))
            return;
        KitType kitType = KitType.getClickedKit(e.getCurrentItem());
        if (kitType == null)
            return;

        Member member = MemberManager.getInstance().getMember((Player) e.getWhoClicked());
        KitPvPStats stats = kitManager.getKitPvP().getStats(member);
        if (stats.hasKit(kitType)) {
            member.message("§7You §aalready §7have this kit §aunlocked§7.");
            return;
        }

        Kit kit = kitType.getKit();
        if (stats.getCoins() - kit.getPrice() < 0) {
            member.message("§7You don't have enough §amoney §7to buy this kit.");
            return;
        }

        chosenKitMap.put(member.getUUID(), kitType);
        Bukkit.getScheduler().runTaskLater(kitManager.getKitPvP(), () -> openConfirmMenu(member), 1);
    }

    @EventHandler
    public void onConfirmClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getName() != null && e.getClickedInventory().getName().equalsIgnoreCase("§aConfirm"))
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
            KitPvPStats stats = kitManager.getKitPvP().getStats(member);
            KitType kitType = chosenKitMap.get(member.getUUID());
            if (kitType == null)
                return;
            Kit kit = kitType.getKit();

            member.message("§7You bought §a" + kit.getName() + "§7 for §a" + kit.getPrice() + "§7 coins.");
            stats.addKit(kitType);
            stats.setCoins(stats.getCoins() - kit.getPrice());
            Bukkit.getScheduler().runTaskLater(kitManager.getPlugin(), () -> member.getPlayer().closeInventory(), 1);
            return;
        }

        Bukkit.getScheduler().runTaskLater(kitManager.getKitPvP(), () -> open(member), 1);
    }

}
