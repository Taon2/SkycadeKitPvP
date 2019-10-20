package net.skycade.kitpvp.ui;

import net.md_5.bungee.api.ChatColor;
import net.skycade.SkycadeCore.guis.dynamicnew.DynamicGui;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static net.skycade.kitpvp.Messages.ALREADY_UNLOCKED;
import static net.skycade.kitpvp.Messages.NOT_ENOUGH_CURRENCY;

public class ShopMenu extends DynamicGui {

    public ShopMenu(KitManager kitManager, Member member) {
        super(ChatColor.GOLD + "" + ChatColor.BOLD + "Shop", 6);

        KitPvPStats stats = kitManager.getKitPvP().getStats(member);
        List<KitType> kits = KitPvP.getInstance().getKitPvPDocManager().getCurrentKits();

        kits.forEach(kitType -> addItemInteraction(p -> {
                    Kit kit = kitType.getKit();

                    if (kit.getPrice() == 0) return null;

                    ItemStack item = new ItemStack(kit.getIcon());

                    if (stats.hasKit(kitType))
                        item.setType(Material.BEDROCK);

                    ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());

                    meta.setDisplayName(ChatColor.GREEN + kit.getName());

                    List<String> lore = new ArrayList<>(kit.getDescription());
                    lore.add("");
                    lore.add(ChatColor.GOLD + "Cost: " + ChatColor.WHITE + kit.getPrice() + " Coins");
                    if (stats.hasKit(kitType))
                        lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "You already own this kit!");
                    else
                        lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Click to purchase!");
                    meta.setLore(lore);

                    item.setItemMeta(meta);

                    return item;
                },
                (p, ev) -> {
                    if (stats.hasKit(kitType)) {
                        ALREADY_UNLOCKED.msg(member.getPlayer(), "%thing%", "kit " + kitType.getKit().getName());
                        p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
                        return;
                    }

                    if (stats.getCoins() - kitType.getKit().getPrice() < 0) {
                        NOT_ENOUGH_CURRENCY.msg(p, "%currency%", "coins", "%thing%", "kit " + kitType.getKit().getName());
                        p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
                        return;
                    }

                    new ConfirmMenu(kitManager, member, kitType).open(p);
                }));
    }
}
