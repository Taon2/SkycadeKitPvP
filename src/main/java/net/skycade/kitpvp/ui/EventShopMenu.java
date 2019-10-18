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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static net.skycade.kitpvp.Messages.NOT_ENOUGH_CURRENCY;
import static net.skycade.kitpvp.Messages.ON_COOLDOWN;

public class EventShopMenu extends DynamicGui {

    private Map<String, Long> purchasedUpgrades = new HashMap<>();

    public EventShopMenu(EventShopManager eventShopManager, Member member) {
        super(ChatColor.GOLD + "" + ChatColor.BOLD + "Event Shop", 2);

        if (eventShopManager.getYaml().contains(member.getUUID().toString())) {
            ConfigurationSection main = eventShopManager.getYaml().getConfigurationSection(member.getUUID().toString());
            for (String handle : main.getKeys(false)) {
                addPurchasedUpgrades(handle, eventShopManager.getYaml().getLong(member.getUUID().toString() + "." + handle));
            }
        }

        Collection<EventShopItem> items = (eventShopManager.getEventShopItems().values());
        items.forEach(eventItem -> addItemInteraction(p -> {
                    ItemStack displayItem = new ItemStack(eventItem.getIcon());


                    if (purchasedUpgrades.containsKey(eventItem.getName()) && (System.currentTimeMillis() - purchasedUpgrades.get(eventItem.getName())) / 1000L < eventItem.getDuration()) {
                        displayItem.setType(Material.BEDROCK);

                        //setting the name of the item
                        ItemMeta meta = displayItem.getItemMeta();
                        meta.setDisplayName(eventItem.getName());
                        displayItem.setItemMeta(meta);
                    } else {
                        //building lore
                        ArrayList<String> lore = new ArrayList<>(eventItem.getDescription());

                        //adding lore and name to item
                        ItemMeta meta = displayItem.getItemMeta();
                        meta.setLore(lore);
                        meta.setDisplayName(eventItem.getName());
                        displayItem.setItemMeta(meta);
                    }

                    return displayItem;
                },
                (p, ev) -> {
                    KitPvPStats stats = eventShopManager.getKitPvP().getStats(member);

                    long now = System.currentTimeMillis();
                    EventShopItem clickedItem = eventShopManager.getTypeFromString(ev.getCurrentItem().getItemMeta().getDisplayName());
                    if (clickedItem != null && purchasedUpgrades.containsKey(clickedItem.getName())) {
                        long diff = (now - purchasedUpgrades.get(clickedItem.getName())) / 1000L;

                        if (diff < clickedItem.getDuration()) {
                            ON_COOLDOWN.msg(member.getPlayer(), "%time%", CoreUtil.niceFormat(clickedItem.getDuration() - ((Long) diff).intValue()), "%thing%", clickedItem.getName());
                            p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
                            return;
                        }

                        if (stats.getEventTokens() - clickedItem.getPrice() < 0) {
                            NOT_ENOUGH_CURRENCY.msg(member.getPlayer(), "%currency%", "event tokens", "%thing%", ChatColor.stripColor(clickedItem.getName()));
                            p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
                            return;
                        }
                    }

                    new ConfirmMenu(eventShopManager, member, clickedItem).open(p);
            }));
    }

    private void addPurchasedUpgrades(String key, Long time) {
        if (purchasedUpgrades != null)
            purchasedUpgrades.forEach((k, l) -> purchasedUpgrades.put(k, l));

        purchasedUpgrades.put(key, time);
    }
}
