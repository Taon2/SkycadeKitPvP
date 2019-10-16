package net.skycade.kitpvp.ui;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
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

import static net.skycade.kitpvp.Messages.*;

public class ShopMenu implements Listener {

    private Inventory menu;
    private Inventory confirmMenu;
    private final KitManager kitManager;
    private final Map<UUID, KitType> chosenKitMap = new HashMap<>();

    public ShopMenu(KitManager kitManager) {
        this.kitManager = kitManager;
        if (KitPvP.getInstance().getConfig().getString("kits-rotation-enabled").equals("false")){
            menu = Bukkit.createInventory(null, MenuSize.SIX_LINE.getSize(), "§aShop");
        }
        else{
            menu = Bukkit.createInventory(null, getMenuSize(), "§aShop");
        }
        confirmMenu = Bukkit.createInventory(null, MenuSize.THREE_LINE.getSize(), "§aShop Confirm");
    }


    private void updateValues(Member member) {
        if (KitPvP.getInstance().getConfig().getString("kits-rotation-enabled").equals("false")){
            menu = Bukkit.createInventory(null, MenuSize.SIX_LINE.getSize(), "§aShop");
        }
        else{
            menu = Bukkit.createInventory(null, getMenuSize(), "§aShop");
        }
        menu.clear();
        KitPvPStats stats = kitManager.getKitPvP().getStats(member);

        List<KitType> kits = KitPvP.getInstance().getKitPvPDocManager().getCurrentKits();
        for (KitType k : kits) {
            Kit kit = k.getKit();
            if (stats.hasKit(k)) {
                menu.addItem(new ItemBuilder(Material.BEDROCK).setName("§c" + kit.getName()).build());
            } else
                menu.addItem(new ItemBuilder(kit.getIcon()).addLore(Arrays.asList("Price: §6" + kit.getPrice(), "", "§7Click to buy this kit.")).setName("§a" + kit.getName()).build());
        }
    }


    private void openConfirmMenu(Member member) {
        if (!chosenKitMap.containsKey(member.getUUID())) {
            DIDNT_CHOOSE.msg(member.getPlayer(), "%thing%", "a kit");
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
            ALREADY_UNLOCKED.msg(member.getPlayer(), "%kit%", kitType.getKit().getName());
            return;
        }

        Kit kit = kitType.getKit();
        if (stats.getCoins() - kit.getPrice() < 0) {
            NOT_ENOUGH_CURRENCY.msg(member.getPlayer(), "%currency%", "coins", "%thing%", "kit");
            return;
        }

        chosenKitMap.put(member.getUUID(), kitType);
        Bukkit.getScheduler().runTaskLater(kitManager.getKitPvP(), () -> openConfirmMenu(member), 1);
    }

    @EventHandler
    public void onConfirmClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getName() != null && e.getClickedInventory().getName().equalsIgnoreCase("§aShop Confirm"))
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
            if (stats.getCoins() - kit.getPrice() < 0)
                return;

            YOU_PURCHASED.msg(member.getPlayer(), "%thing%", kit.getName(), "%amount%", Integer.toString(kit.getPrice()), "%currency%", "coins");
            stats.addKit(kitType);
            stats.setCoins(stats.getCoins() - kit.getPrice());

            ScoreboardInfo.getInstance().updatePlayer(member.getPlayer());
            MemberManager.getInstance().update(member);
            Bukkit.getScheduler().runTaskLater(kitManager.getPlugin(), () -> member.getPlayer().closeInventory(), 1);
            return;
        }

        Bukkit.getScheduler().runTaskLater(kitManager.getKitPvP(), () -> open(member), 1);
    }

}
