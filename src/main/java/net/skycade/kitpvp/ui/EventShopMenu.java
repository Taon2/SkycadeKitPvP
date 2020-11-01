package net.skycade.kitpvp.ui;

import net.md_5.bungee.api.ChatColor;
import net.skycade.SkycadeCore.guis.dynamicnew.DynamicGui;
import net.skycade.SkycadeCore.utility.CoreUtil;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.stat.KitPvPStats;
import net.skycade.kitpvp.ui.eventshopitems.EventShopItem;
import net.skycade.kitpvp.ui.eventshopitems.EventShopManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;

import static net.skycade.kitpvp.Messages.NOT_ENOUGH_CURRENCY;

public class EventShopMenu extends DynamicGui {

    public EventShopMenu(EventShopManager eventShopManager, Member member) {
        super(ChatColor.GOLD + "" + ChatColor.BOLD + "Event Shop", 2);

        Collection<EventShopItem> items = (eventShopManager.getEventShopItems().values());
        items.forEach(eventShopItem -> addItemInteraction(p -> {
                    ItemStack displayItem = new ItemStack(eventShopItem.getIcon());

                    if (eventShopManager.isActive(p, eventShopItem))
                        displayItem.setType(Material.BEDROCK);

                    // Building lore
                    ArrayList<String> lore = new ArrayList<>(eventShopItem.getDescription());

                    lore.add("");
                    if (eventShopItem.getPrice() > 0)
                        lore.add(ChatColor.GOLD + "Cost: " + ChatColor.WHITE + eventShopItem.getPrice() + " Event Tokens");
                    if (eventShopItem.getDuration() > 0)
                        lore.add(ChatColor.GOLD + "Duration: " + ChatColor.WHITE + CoreUtil.niceFormat(eventShopItem.getDuration()));
                    if (eventShopManager.isActive(p, eventShopItem))
                        lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "You already have this upgrade purchased!");
                    else
                        lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Click to purchase!");

                    // Adding lore and name to item
                    ItemMeta meta = displayItem.getItemMeta();
                    meta.setDisplayName(ChatColor.GREEN + eventShopItem.getName());
                    meta.setLore(lore);
                    displayItem.setItemMeta(meta);

                    return displayItem;
                },
                (p, ev) -> {
                    KitPvPStats stats = eventShopManager.getKitPvP().getStats(member);

                    EventShopItem clickedItem = eventShopManager.getTypeFromString(ChatColor.stripColor(ev.getCurrentItem().getItemMeta().getDisplayName()));
                    if (clickedItem != null) {
                        if (eventShopManager.isActive(p, clickedItem)) {
                            p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
                            return;
                        }

                        if (stats.getEventTokens() - clickedItem.getPrice() < 0) {
                            NOT_ENOUGH_CURRENCY.msg(member.getPlayer(), "%currency%", "event tokens", "%thing%", ChatColor.stripColor(clickedItem.getName()));
                            p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
                            return;
                        }

                        new ConfirmMenu(eventShopManager, member, clickedItem).open(p);
                    }
                }));
    }
}
