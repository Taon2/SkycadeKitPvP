package net.skycade.kitpvp.ui;

import net.md_5.bungee.api.ChatColor;
import net.skycade.SkycadeCore.guis.dynamicnew.DynamicGui;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static net.skycade.kitpvp.Messages.*;
import static net.skycade.kitpvp.Messages.KIT_EQUIPPED_RESPAWN;

public class KitMenu extends DynamicGui {

    public KitMenu(KitManager kitManager, Member member) {
        super(ChatColor.GOLD + "" + ChatColor.BOLD + member.getName() + "'s Kits", 6);

        KitPvPStats stats = kitManager.getKitPvP().getStats(member);
        Map<KitType, Kit> kits = KitPvP.getInstance().getKitManager().getKits();

        List<KitType> toRemove = new ArrayList<>();
        kits.forEach((kitType, kit) -> {
            if (!kit.isEnabled()) {
                toRemove.add(kitType);
            }
        });

        toRemove.forEach(kits::remove);

        kits.values().forEach(kit -> addItemInteraction(p -> {
                    if (!kit.isEnabled()) return null;

                    ItemStack item = new ItemStack(kit.getIcon());

                    if (!stats.hasKit(kit.getKitType()))
                        item.setType(Material.BEDROCK);

                    ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());

                    if (stats.getActiveKit().equals(kit.getKitType())) {
                        meta.addEnchant(Enchantment.DURABILITY, 10, true);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    }

                    meta.setDisplayName(ChatColor.GREEN + kit.getName());

                    List<String> lore = new ArrayList<>(Arrays.asList(kit.getDescription()));
                    lore.add("");
                    if (stats.hasKit(kit.getKitType()))
                        lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Click to use!");
                    else
                        lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase in /shop!");
                    meta.setLore(lore);

                    item.setItemMeta(meta);

                    return item;
                },
                (p, ev) -> {
                    if (!stats.hasKit(kit.getKitType())) {
                        DONT_OWN.msg(member.getPlayer(), "%kit%", "kit " + kit.getName());
                        p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
                        return;
                    }

                    if (stats.getActiveKit() == kit.getKitType()) {
                        ALREADY_USING.msg(member.getPlayer(), "%kit%", kit.getKitType().getKit().getName());
                        p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
                        return;
                    }

                    if (kitManager.getKitPvP().getSpawnRegion().contains(member.getPlayer())) {
                        if (kitManager.getSignMap().containsKey(member.getUUID())) {
                            ON_COOLDOWN_NO_TIME.msg(member.getPlayer(), "%thing%", "Kit refreshing");
                            p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
                            return;
                        }
                        UtilPlayer.reset(member.getPlayer());
                        kitManager.getKitPvP().getStats(member).setActiveKit(kit.getKitType());
                        kitManager.getKitPvP().getStats(member).setKitPreference(kit.getKitType());
                        kit.beginApplyKit(member.getPlayer());
                        kit.giveSoup(member.getPlayer(), 32);
                        KIT_EQUIPPED.msg(member.getPlayer(), "%kit%", kit.getName());
                        p.playSound(p.getLocation(), Sound.LEVEL_UP, 1f, 2f);
                    } else {
                        kitManager.getKitPvP().getStats(member).setKitPreference(kit.getKitType());
                        KIT_EQUIPPED_RESPAWN.msg(member.getPlayer(), "%kit%", kit.getName());
                        p.playSound(p.getLocation(), Sound.LEVEL_UP, 1f, 2f);
                    }

                    p.getOpenInventory().close();
                }));
    }
}
