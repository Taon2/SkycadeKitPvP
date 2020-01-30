package net.skycade.kitpvp.ui;

import net.md_5.bungee.api.ChatColor;
import net.skycade.SkycadeCore.guis.dynamicnew.DynamicGui;
import net.skycade.SkycadeCore.utility.ItemBuilder;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.ui.prestige.PrestigeManager;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PrestigeMenu extends DynamicGui {

    private static final ItemStack BACK = new ItemBuilder(Material.ARROW)
            .setDisplayName(ChatColor.GOLD + "Go Back")
            .build();
    private static final ItemStack NEXT = new ItemBuilder(Material.ARROW)
            .setDisplayName(ChatColor.GOLD + "Next")
            .build();

    public PrestigeMenu(Member member, int page) {
        super(ChatColor.GOLD + "" + ChatColor.BOLD + "Prestige - Page " + page, 6);
        KitPvP plugin = KitPvP.getInstance();
        PrestigeManager prestigeManager = plugin.getPrestigeManager();

        KitPvPStats stats = plugin.getStats(member);

        prestigeManager.getPrestigeLevels().values().stream()
                .skip((page - 1) * 45)
                .limit(45)
                .forEach(prestigeLevel -> addItemInteraction(p -> {
                            ItemStack item = new ItemStack(Material.BEDROCK, 1);

                            if (stats.getPrestigeLevel() >= prestigeLevel.getLevel())
                                item.setType(Material.EMERALD_BLOCK);

                            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());

                            meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Prestige " + ChatColor.GRAY + "[" + ChatColor.WHITE + prestigeLevel.getLevel() + "★" + ChatColor.GRAY + "]");

                            List<String> lore = new ArrayList<>();
                            if (stats.getPrestigeLevel() >= prestigeLevel.getLevel())
                                lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "UNLOCKED!");
                            else
                                lore.add(ChatColor.RED + "" + ChatColor.BOLD + "LOCKED!");

                            lore.add("");
                            lore.add(ChatColor.GOLD + "Cost: " + ChatColor.WHITE + prestigeLevel.getCost() + " Coins");
                            lore.add(ChatColor.GOLD + "Rewards: ");
                            prestigeLevel.getRewardDesc().forEach(line -> {
                                lore.add(ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', line));
                            });
                            meta.setLore(lore);

                            item.setItemMeta(meta);

                            return item;
                        },
                        (p, ev) -> {
                            ItemStack clicked = ev.getCurrentItem();
                            String name = clicked.getItemMeta().getDisplayName();
                            name = ChatColor.stripColor(name);

                            if (name == null)
                                return;

                            int index1 = name.indexOf('[') + 1;
                            int index2 = name.indexOf('★');
                            int level = Integer.parseInt(name.substring(index1, index2));

                            if (prestigeManager.attemptPrestige(member, stats, level)) {
                                clicked.setType(Material.EMERALD_BLOCK);

                                ItemMeta meta = clicked.getItemMeta();

                                List<String> lore = meta.getLore();
                                lore.set(0, ChatColor.GREEN + "UNLOCKED!");

                                meta.setLore(lore);
                                clicked.setItemMeta(meta);
                                p.updateInventory();
                                p.playSound(p.getLocation(), Sound.LEVEL_UP, 1f, 2f);
                            } else {
                                p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
                            }
                        }));

        if (page > 1) {
            setItemInteraction(45, new ItemBuilder(BACK).build(),
                    (p, ev) -> {
                        new PrestigeMenu(member, page -1).open(p);
                    });
        }

        if (prestigeManager.getPrestigeLevels().size() > page * 36) {
            setItemInteraction(53, new ItemBuilder(NEXT).build(),
                    (p, ev) -> {
                        new PrestigeMenu(member, page + 1).open(p);
                    });
        }
    }
}
