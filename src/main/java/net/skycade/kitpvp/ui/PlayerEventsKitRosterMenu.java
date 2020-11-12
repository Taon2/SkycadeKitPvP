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
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.playerevents.EventManager;
import net.skycade.kitpvp.playerevents.EventType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class PlayerEventsKitRosterMenu extends DynamicGui {
    private static final ItemStack KNIGHT = new ItemBuilder(Material.CHAINMAIL_CHESTPLATE)
            .setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Knight")
            .build();

    private static final ItemStack REAPER = new ItemBuilder(Material.SKULL_ITEM)
            .setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Reaper")
            .build();

    private static final ItemStack PAINTBALL = new ItemBuilder(Material.DIAMOND_HOE)
            .setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Paintball")
            .build();

    private static final ItemStack BUILDUHC = new ItemBuilder(Material.WOOD)
            .setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "BuildUHC")
            .build();

    private static final ItemStack DUBSTEP = new ItemBuilder(Material.RECORD_3)
            .setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Dubstep")
            .build();

    private static final ItemStack TELEPORTER = new ItemBuilder(Material.ENDER_PORTAL_FRAME)
            .setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Teleporter")
            .build();

    public PlayerEventsKitRosterMenu(EventType type) {
        super("Choose a Kit", 3);

        setItemInteraction(10, new ItemBuilder(KNIGHT).build(),
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
                        manager.announceEvent(p, type);
                        if (type == EventType.LMS) {
                            manager.getLMS().setChosenKit(KitType.KNIGHT);
                        }
                        if (type == EventType.BRACKETS){
                            manager.getBrackets().setChosenKit(KitType.KNIGHT);
                        }
                        manager.setHoster(p.getUniqueId());
                        p.closeInventory();
                    } else {
                        p.sendMessage(Localization.getInstance().tl("skycade.crates.open.nokey"));
                    }
                });

        setItemInteraction(11, new ItemBuilder(REAPER).build(),
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
                        manager.announceEvent(p, type);
                        if (type == EventType.LMS) {
                            manager.getLMS().setChosenKit(KitType.REAPER);
                        }
                        if (type == EventType.BRACKETS){
                            manager.getBrackets().setChosenKit(KitType.REAPER);
                        }
                        manager.setHoster(p.getUniqueId());
                        p.closeInventory();
                    } else {
                        p.sendMessage(Localization.getInstance().tl("skycade.crates.open.nokey"));
                    }
                });

        setItemInteraction(12, new ItemBuilder(PAINTBALL).build(),
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
                        manager.announceEvent(p, type);
                        if (type == EventType.LMS) {
                            manager.getLMS().setChosenKit(KitType.PAINTBALL);
                        }
                        if (type == EventType.BRACKETS){
                            manager.getBrackets().setChosenKit(KitType.PAINTBALL);
                        }
                        manager.setHoster(p.getUniqueId());
                        p.closeInventory();
                    } else {
                        p.sendMessage(Localization.getInstance().tl("skycade.crates.open.nokey"));
                    }
                });

        setItemInteraction(14, new ItemBuilder(BUILDUHC).build(),
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
                        manager.announceEvent(p, type);
                        if (type == EventType.LMS) {
                            manager.getLMS().setChosenKit(KitType.BUILDUHC);
                        }
                        if (type == EventType.BRACKETS){
                            manager.getBrackets().setChosenKit(KitType.BUILDUHC);
                        }
                        manager.setHoster(p.getUniqueId());
                        p.closeInventory();
                    } else {
                        p.sendMessage(Localization.getInstance().tl("skycade.crates.open.nokey"));
                    }
                });

        setItemInteraction(15, new ItemBuilder(DUBSTEP).build(),
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
                        manager.announceEvent(p, type);
                        if (type == EventType.LMS) {
                            manager.getLMS().setChosenKit(KitType.DUBSTEP);
                        }
                        if (type == EventType.BRACKETS){
                            manager.getBrackets().setChosenKit(KitType.DUBSTEP);
                        }
                        manager.setHoster(p.getUniqueId());
                        p.closeInventory();
                    } else {
                        p.sendMessage(Localization.getInstance().tl("skycade.crates.open.nokey"));
                    }
                });

        setItemInteraction(16, new ItemBuilder(TELEPORTER).build(),
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
                        manager.announceEvent(p, type);
                        if (type == EventType.LMS) {
                            manager.getLMS().setChosenKit(KitType.TELEPORTER);
                        }
                        if (type == EventType.BRACKETS){
                            manager.getBrackets().setChosenKit(KitType.TELEPORTER);
                        }
                        manager.setHoster(p.getUniqueId());
                        p.closeInventory();
                    } else {
                        p.sendMessage(Localization.getInstance().tl("skycade.crates.open.nokey"));
                    }
                });
    }
}
