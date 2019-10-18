package net.skycade.kitpvp.ui;

import net.md_5.bungee.api.ChatColor;
import net.skycade.SkycadeCore.guis.dynamicnew.DynamicGui;
import net.skycade.SkycadeCore.utility.ItemBuilder;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPStats;
import net.skycade.kitpvp.ui.eventshopitems.EventShopItem;
import net.skycade.kitpvp.ui.eventshopitems.EventShopManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.skycade.kitpvp.Messages.NOT_ENOUGH_CURRENCY;
import static net.skycade.kitpvp.Messages.YOU_PURCHASED;

class ConfirmMenu extends DynamicGui {

    private static final ItemStack CONFIRM = new ItemBuilder(Material.STAINED_GLASS)
            .setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Confirm Purchase")
            .setData((byte) 5)
            .build();
    private static final ItemStack DECLINE = new ItemBuilder(Material.STAINED_GLASS)
            .setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Decline Purchase")
            .setData((byte) 14)
            .build();

    ConfirmMenu(KitManager kitManager, Member member, KitType kitType) {
        super(ChatColor.GOLD + "" + ChatColor.BOLD + "Confirm Purchase", 3);

        setItemInteraction(11, new ItemBuilder(CONFIRM).build(),
                (p, ev) -> {
                    KitPvPStats stats = kitManager.getKitPvP().getStats(member);
                    Kit kit = kitType.getKit();
                    if (stats.getCoins() - kit.getPrice() < 0) {
                        NOT_ENOUGH_CURRENCY.msg(p, "%currency%", "coins", "%thing%", "kit " + kit.getName());
                        new ShopMenu(kitManager, member).open(p);
                        return;
                    }

                    YOU_PURCHASED.msg(p, "%thing%", kit.getName(), "%amount%", Integer.toString(kit.getPrice()), "%currency%", "coins");
                    stats.addKit(kitType);
                    stats.setCoins(stats.getCoins() - kit.getPrice());

                    ScoreboardInfo.getInstance().updatePlayer(p);
                    MemberManager.getInstance().update(member);
                    new ShopMenu(kitManager, member).open(p);
                });

        setItemInteraction(15, new ItemBuilder(DECLINE).build(),
                (p, ev) -> {
                    new ShopMenu(kitManager, member).open(p);
                });
    }

    ConfirmMenu(EventShopManager eventShopManager, Member member, EventShopItem eventShopItem) {
        super(ChatColor.GOLD + "" + ChatColor.BOLD + "Confirm Purchase", 3);

        setItemInteraction(11, new ItemBuilder(CONFIRM).build(),
                (p, ev) -> {
                    KitPvPStats stats = eventShopManager.getKitPvP().getStats(member);
                    if (eventShopItem == null)
                        return;
                    if (stats.getEventTokens() - eventShopItem.getPrice() < 0)
                        return;

                    YOU_PURCHASED.msg(member.getPlayer(), "%thing%", eventShopItem.getName(), "%amount%", Integer.toString(eventShopItem.getPrice()), "%currency%", "event tokens");
                    eventShopItem.giveReward(((Player) ev.getWhoClicked()).getPlayer());
                    stats.setEventCoins(stats.getEventTokens() - eventShopItem.getPrice());

                    ScoreboardInfo.getInstance().updatePlayer(member.getPlayer());
                    MemberManager.getInstance().update(member);
                    Bukkit.getScheduler().runTaskLater(eventShopManager.getPlugin(), () -> member.getPlayer().closeInventory(), 1);

                    new EventShopMenu(eventShopManager, member).open(p);
                });

        setItemInteraction(15, new ItemBuilder(DECLINE).build(),
                (p, ev) -> {
                    new EventShopMenu(eventShopManager, member).open(p);
                });
    }
}
