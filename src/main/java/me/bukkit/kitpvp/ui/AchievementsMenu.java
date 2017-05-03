package me.bukkit.kitpvp.ui;

import me.bukkit.kitpvp.coreclasses.member.Member;
import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.coreclasses.utils.UtilMath;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.stat.Achievement;
import me.bukkit.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class AchievementsMenu implements Listener {

    private final KitManager kitManager;
	private final Inventory menu;

	public AchievementsMenu(KitManager kitManager) {
	    this.kitManager = kitManager;
	    this.menu = Bukkit.createInventory(null, MenuSize.SIX_LINE.getSize(), "§aAchievements");

		ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) UtilMath.getRandom(0, 15));
		int row = 0;
		for (int i = 0; i < 6; i++) {
			for (int y = 0; y < 9; y++) {
				if (y == 0 || y == 8 || i == 0 || i == 5)
				    menu.setItem(y + row, glass);
				if (y == 8)
					row += 9;
			}
		}
	}

	private void updateValues(Member member) {
        KitPvPStats stats = kitManager.getKitPvP().getStats(member);
        int kills = stats.getKills();
        int killstreak = kitManager.getKitPvP().getStats(member).getHighestStreak();
        int duels = kitManager.getKitPvP().getStats(member).getDuels();
        int assist = kitManager.getKitPvP().getStats(member).getAssists();

        int counter = 10;
        for (int num : Achievement.KILLS.getValues()) {
            menu.setItem(counter++, new ItemBuilder(num > kills ? Material.DIRT : Achievement.KILLS.getMaterial()).addLore(Collections.singletonList("§7Get " + num + " kills")).setName(num > kills ? "§c" + num + " kills" : "§a" + num + " kills").build());
        }

        counter = 19;
        for (int num : Achievement.KILLSTREAK.getValues()) {
            menu.setItem(counter++, new ItemBuilder(num > killstreak ? Material.DIRT : Achievement.KILLSTREAK.getMaterial()).addLore(Collections.singletonList("§7Get a " + num + " killstreak")).setName(num > killstreak ? "§c" + num + " killstreak" : "§a" + num + " killstreak").build());
        }

        counter = 28;
        for (int num : Achievement.DUEL.getValues()) {
            menu.setItem(counter++, new ItemBuilder(num > duels  ? Material.DIRT : Achievement.DUEL.getMaterial()).addLore(Collections.singletonList("§7Win " + num + " duels.")).setName(num > duels ? "§c" + num + " duels" : "§a" + num + " duels").build());
        }

        counter = 37;
        for (int num : Achievement.ASSISTS.getValues()) {
            menu.setItem(counter++, new ItemBuilder(num > assist ? Material.DIRT : Achievement.ASSISTS.getMaterial()).addLore(Collections.singletonList("§7Get " + num + " assists")).setName(num > assist ? "§c" + num + " assists" : "§a" + num + " assists").build());
        }

    }

    public void open(Member member) {
        updateValues(member);
        member.getPlayer().openInventory(menu);
    }

    @EventHandler
    public void on(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getName() != null && e.getClickedInventory().getName().equalsIgnoreCase("§aAchievements"))
            e.setCancelled(true);
    }

}
