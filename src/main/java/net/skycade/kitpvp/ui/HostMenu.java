package net.skycade.kitpvp.ui;

import net.md_5.bungee.api.ChatColor;
import net.skycade.SkycadeCore.Localization;
import net.skycade.SkycadeCore.guis.dynamicnew.DynamicGui;
import net.skycade.SkycadeCore.utility.CoreUtil;
import net.skycade.SkycadeCore.utility.ItemBuilder;
import net.skycade.crates.CrateUser;
import net.skycade.crates.CratesPlugin;
import net.skycade.crates.crates.Crate;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.Messages;
import net.skycade.kitpvp.playerevents.EventManager;
import net.skycade.kitpvp.playerevents.EventType;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class HostMenu extends DynamicGui {
    private static final ItemStack SUMO = new ItemBuilder(Material.POTATO_ITEM)
            .setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Sumo")
            .build();

    private static final ItemStack BRACKETS = new ItemBuilder(Material.DIAMOND_SWORD)
            .setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Brackets")
            .build();

    private static final ItemStack LMS = new ItemBuilder(Material.GRASS)
            .setDisplayName(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Last Man Standing")
            .build();

    public HostMenu() {
        super("Choose an Event", 3);
        setItemInteraction(11, new ItemBuilder(SUMO).build(),
                (p, ev) -> {
                    EventManager manager = KitPvP.getInstance().getEventManager();
                    if (manager.isCooldownOn()) {
                        long currentTime = System.currentTimeMillis();
                        long newTime = manager.getGlobalCooldown() - currentTime;

                        ev.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                        open(p);

                        int toseconds = (int) (newTime / 1000);
                        String message = Messages.EVENT_ON_COOLDOWN.getMessage();
                        message = message.replaceAll("%time%", CoreUtil.niceFormat(toseconds, false));

                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        return;
                    }
                    if (manager.getCurrentEvent() != EventType.IDLE) {
                        p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                        ev.setCancelled(true);
                        return;
                    }
                    Crate crate = CratesPlugin.getInstance().getEditorModule().getCrate("hostcredit");

                    if (crate == null) {
                        p.sendMessage(org.bukkit.ChatColor.RED + "That crate doesn't exist.");
                        return;
                    }
                    if (CrateUser.get(p.getUniqueId()).hasKey(crate)) {
                        manager.announceEvent(p, EventType.SUMO);
                        manager.setHoster(p.getUniqueId());
                        p.closeInventory();
                    } else {
                        Messages.NO_HOST_CREDITS.msg(p);
                    }
                });

        setItemInteraction(13, new ItemBuilder(LMS).build(),
                (p, ev) -> {
                    EventManager manager = KitPvP.getInstance().getEventManager();
                    if (manager.isCooldownOn()) {
                        long currentTime = System.currentTimeMillis();
                        long newTime = manager.getGlobalCooldown() - currentTime;

                        ev.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                        open(p);

                        int toseconds = (int) (newTime / 1000);
                        String message = Messages.EVENT_ON_COOLDOWN.getMessage();
                        message = message.replaceAll("%time%", CoreUtil.niceFormat(toseconds, false));

                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        return;
                    }
                    if (manager.getCurrentEvent() != EventType.IDLE) {
                        p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                        ev.setCancelled(true);
                        return;
                    }
                    Crate crate = CratesPlugin.getInstance().getEditorModule().getCrate("hostcredit");

                    if (crate == null) {
                        p.sendMessage(org.bukkit.ChatColor.RED + "That crate doesn't exist.");
                        return;
                    }
                    if (CrateUser.get(p.getUniqueId()).hasKey(crate)) {
                        new PlayerEventsKitRosterMenu(EventType.LMS).open(p);
                    } else {
                        Messages.NO_HOST_CREDITS.msg(p);
                    }
                });

        setItemInteraction(15, new ItemBuilder(BRACKETS).build(),
                (p, ev) -> {
                    EventManager manager = KitPvP.getInstance().getEventManager();
                    if (manager.isCooldownOn()) {
                        long currentTime = System.currentTimeMillis();
                        long newTime = manager.getGlobalCooldown() - currentTime;

                        ev.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                        open(p);

                        int toseconds = (int) (newTime / 1000);
                        String message = Messages.EVENT_ON_COOLDOWN.getMessage();
                        message = message.replaceAll("%time%", CoreUtil.niceFormat(toseconds, false));

                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        return;
                    }
                    if (manager.getCurrentEvent() != EventType.IDLE) {
                        p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                        ev.setCancelled(true);
                        return;
                    }
                    Crate crate = CratesPlugin.getInstance().getEditorModule().getCrate("hostcredit");

                    if (crate == null) {
                        p.sendMessage(org.bukkit.ChatColor.RED + "That crate doesn't exist.");
                        return;
                    }
                    if (CrateUser.get(p.getUniqueId()).hasKey(crate)) {
                        new PlayerEventsKitRosterMenu(EventType.BRACKETS).open(p);
                    } else {
                        Messages.NO_HOST_CREDITS.msg(p);
                    }
                });
    }
}
