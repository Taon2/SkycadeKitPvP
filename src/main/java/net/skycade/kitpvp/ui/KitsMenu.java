package net.skycade.kitpvp.ui;

import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
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

public class KitsMenu implements Listener {

    private final KitManager kitManager;
    private final Map<UUID, List<Inventory>> menuMap = new HashMap<>();
    private final Map<UUID, Integer> pageMap = new HashMap<>();

    public KitsMenu(KitManager kitManager) {
        this.kitManager = kitManager;
    }

    private void updateValues(Member member) {
        Inventory menu = Bukkit.createInventory(null, MenuSize.SIX_LINE.getSize(), "§aKits");
        KitPvPStats stats = kitManager.getKitPvP().getStats(member);

        // Reset maps
        menuMap.remove(member.getUUID());
        pageMap.remove(member.getUUID());

        // First menu
        pageMap.put(member.getUUID(), 0);

        List<Inventory> menuList = new LinkedList<>();

        int i = 0;
        for (Map.Entry<KitType, Kit> entry : kitManager.getKits().entrySet()) {
            KitType k = entry.getKey();
            Kit kit = entry.getValue();
            if (!kit.isEnabled()) continue;

            if (i >= menu.getSize() - 1) {
                menu.addItem(new ItemBuilder(Material.ARROW).setName("§aNext page").addLore("Click to go to the next page.").build());
                menuList.add(menu);
                menu = Bukkit.createInventory(null, MenuSize.SIX_LINE.getSize(), "§aKits");
                menu.addItem(new ItemBuilder(Material.ARROW).setName("§aPrev page").addLore("Click to go to the previous page.").build());
                i = 1;
            }

            if (!stats.hasKit(k)) {
                menu.addItem(new ItemBuilder(Material.BEDROCK).setName("§c" + kit.getName()).build());
            } else {
                /*
                KitData data = stats.getKits().get(k);
                menu.addItem(new ItemBuilder(kit.getIcon()).setName("§a" + kit.getName()).addLore("").addLore(kit.getDescription()).addLore("")
                        .addLore("§7Level: §f" + data.getLevel(), data.getLevel() < 3 ? "§7Experience: §f" + data.getXp() + "/" + kit.getLevelUpXp(member.getPlayer()) : "").setGlow(stats.getActiveKit() == k).build()); */

                menu.addItem(new ItemBuilder(kit.getIcon()).setName("§a" + kit.getName()).addLore("")
                        .addLore(kit.getDescription())
                        .setGlow(stats.getActiveKit() == k)
                        .build());
            }
            i++;
        }
        menuList.add(menu);
        menuMap.put(member.getUUID(), menuList);
    }

    public void open(Member member) {
        updateValues(member);
        member.getPlayer().openInventory(menuMap.get(member.getUUID()).get(0));
    }

    @EventHandler
    public void on(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getName() != null && e.getClickedInventory().getName().contains("§aKits"))
            e.setCancelled(true);
        else
            return;
        if (!(e.getWhoClicked() instanceof Player))
            return;

        Member member = MemberManager.getInstance().getMember((Player) e.getWhoClicked());
        ItemStack item = e.getCurrentItem();
        if (item.getType() == Material.ARROW) {
            if (item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null)
                return;
            String itemName = item.getItemMeta().getDisplayName();


            // Go to the next kits menu
            if (itemName.equalsIgnoreCase("§aNext page")) {
                UUID uuid = member.getUUID();
                int nextPage = pageMap.containsKey(uuid) ? pageMap.get(uuid) + 1 : 1;
                pageMap.put(uuid, nextPage);

                //Shouldn't be possible
                if (!menuMap.containsKey(uuid) || nextPage > menuMap.get(uuid).size() - 1)
                    return;

                Bukkit.getScheduler().runTaskLater(kitManager.getKitPvP(), () -> member.getPlayer().openInventory(menuMap.get(uuid).get(nextPage)), 1);
            }
            // Go to the prev kits menu
            else if (itemName.equalsIgnoreCase("§aPrev page")) {
                UUID uuid = member.getUUID();
                int prevPage = pageMap.containsKey(uuid) ? pageMap.get(uuid) - 1 < 0 ? 0 : pageMap.get(uuid) - 1 : 0;
                pageMap.put(uuid, prevPage);

                //Shouldn't be possible
                if (!menuMap.containsKey(uuid))
                    return;
                Bukkit.getScheduler().runTaskLater(kitManager.getKitPvP(), () -> member.getPlayer().openInventory(menuMap.get(uuid).get(prevPage)), 1);
            }
        }

        KitType kitType = KitType.getClickedKit(item);
        if (kitType == null)
            return;

        KitPvPStats stats = kitManager.getKitPvP().getStats(member);

        if (!stats.hasKit(kitType))
            return;

        Kit kit = kitType.getKit();
        if (stats.getActiveKit() == kitType) {
            member.message("§7You are already using kit §a" + kit.getName() + "§7.");
            return;
        }

        if (kitManager.getKitPvP().getSpawnRegion().contains(member.getPlayer()) && !Arrays.asList(KitType.TELEPORTER, KitType.FISHERMAN).contains(kitType)) {
            if (kitManager.getSignMap().containsKey(member.getUUID())) {
                member.message("§7Kit refreshing is on cooldown.");
                return;
            }
            UtilPlayer.reset(member.getPlayer());
            kitManager.getKitPvP().getStats(member).setActiveKit(kitType);
            kitManager.getKitPvP().getStats(member).setKitPreference(kitType);
            kit.applyKit(member.getPlayer());
            kit.giveSoup(member.getPlayer(), 32);
            member.message("Equipped kit §a" + kit.getName() + "§7.");

        } else {
            kitManager.getKitPvP().getStats(member).setKitPreference(kitType);
            member.message("Equipped kit §a" + kit.getName() + "§7. It will be active after you respawn.");
        }

        Bukkit.getScheduler().runTaskLater(kitManager.getKitPvP(), () -> member.getPlayer().closeInventory(), 1);
    }

}
