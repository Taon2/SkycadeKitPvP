package net.skycade.kitpvp.ui;

import net.skycade.SkycadeCore.guis.dynamicnew.DynamicGui;
import net.skycade.SkycadeCore.utility.ItemBuilder;
import net.skycade.crates.CrateUser;
import net.skycade.crates.CratesPlugin;
import net.skycade.crates.crates.Crate;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.Messages;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.playerevents.EventManager;
import net.skycade.kitpvp.playerevents.EventType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class PlayerEventsKitRosterMenu extends DynamicGui {
    private static final ItemStack KNIGHT = new ItemBuilder(Material.CHAINMAIL_CHESTPLATE)
            .setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Knight")
            .build();

    private static final ItemStack ZEUS = new ItemBuilder(Material.BLAZE_ROD)
            .setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Zeus")
            .build();

    private static final ItemStack GANK = new ItemBuilder(Material.STONE_SWORD)
            .setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "Gank")
            .build();

    private static final ItemStack PAINTBALL = new ItemBuilder(Material.DIAMOND_HOE)
            .setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Paintball")
            .build();

    private static final ItemStack BUILDUHC = new ItemBuilder(Material.WOOD)
            .setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "BuildUHC")
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
                        ev.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                        new PlayerEventsKitRosterMenu(type).open(p);
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
                        if (type == EventType.LMS) {
                            manager.getLMS().setChosenKit(KitType.KNIGHT);
                        }
                        if (type == EventType.BRACKETS) {
                            manager.getBrackets().setChosenKit(KitType.KNIGHT);
                        }
                        manager.setHoster(p.getUniqueId());
                        manager.announceEvent(p, type);
                        p.closeInventory();
                    } else {
                        Messages.NO_HOST_CREDITS.msg(p);
                    }
                });

        setItemInteraction(11, new ItemBuilder(ZEUS).build(),
                (p, ev) -> {
                    EventManager manager = KitPvP.getInstance().getEventManager();
                    if (manager.isCooldownOn()) {
                        ev.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                        new PlayerEventsKitRosterMenu(type).open(p);
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
                        if (type == EventType.LMS) {
                            manager.getLMS().setChosenKit(KitType.ZEUS);
                        }
                        if (type == EventType.BRACKETS) {
                            manager.getBrackets().setChosenKit(KitType.ZEUS);
                        }
                        manager.setHoster(p.getUniqueId());
                        manager.announceEvent(p, type);
                        p.closeInventory();
                    } else {
                        Messages.NO_HOST_CREDITS.msg(p);
                    }
                });

        setItemInteraction(12, new ItemBuilder(PAINTBALL).build(),
                (p, ev) -> {
                    EventManager manager = KitPvP.getInstance().getEventManager();
                    if (manager.isCooldownOn()) {
                        ev.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                        new PlayerEventsKitRosterMenu(type).open(p);
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
                        if (type == EventType.LMS) {
                            manager.getLMS().setChosenKit(KitType.PAINTBALL);
                        }
                        if (type == EventType.BRACKETS) {
                            manager.getBrackets().setChosenKit(KitType.PAINTBALL);
                        }
                        manager.setHoster(p.getUniqueId());
                        manager.announceEvent(p, type);
                        p.closeInventory();
                    } else {
                        Messages.NO_HOST_CREDITS.msg(p);
                    }
                });

        setItemInteraction(14, new ItemBuilder(BUILDUHC).build(),
                (p, ev) -> {
                    EventManager manager = KitPvP.getInstance().getEventManager();
                    if (manager.isCooldownOn()) {
                        ev.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                        new PlayerEventsKitRosterMenu(type).open(p);
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
                        if (type == EventType.LMS) {
                            manager.getLMS().setChosenKit(KitType.BUILDUHC);
                        }
                        if (type == EventType.BRACKETS) {
                            manager.getBrackets().setChosenKit(KitType.BUILDUHC);
                        }
                        manager.setHoster(p.getUniqueId());
                        manager.announceEvent(p, type);
                        p.closeInventory();
                    } else {
                        Messages.NO_HOST_CREDITS.msg(p);
                    }
                });

        setItemInteraction(15, new ItemBuilder(GANK).build(),
                (p, ev) -> {
                    EventManager manager = KitPvP.getInstance().getEventManager();
                    if (manager.isCooldownOn()) {
                        ev.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                        new PlayerEventsKitRosterMenu(type).open(p);
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
                        if (type == EventType.LMS) {
                            manager.getLMS().setChosenKit(KitType.GANK);
                        }
                        if (type == EventType.BRACKETS) {
                            manager.getBrackets().setChosenKit(KitType.GANK);
                        }
                        manager.setHoster(p.getUniqueId());
                        manager.announceEvent(p, type);
                        p.closeInventory();
                    } else {
                        Messages.NO_HOST_CREDITS.msg(p);
                    }
                });

        setItemInteraction(16, new ItemBuilder(TELEPORTER).build(),
                (p, ev) -> {
                    EventManager manager = KitPvP.getInstance().getEventManager();
                    if (manager.isCooldownOn()) {
                        ev.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                        new PlayerEventsKitRosterMenu(type).open(p);
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
                        if (type == EventType.LMS) {
                            manager.getLMS().setChosenKit(KitType.TELEPORTER);
                        }
                        if (type == EventType.BRACKETS) {
                            manager.getBrackets().setChosenKit(KitType.TELEPORTER);
                        }
                        manager.setHoster(p.getUniqueId());
                        manager.announceEvent(p, type);
                        p.closeInventory();
                    } else {
                        Messages.NO_HOST_CREDITS.msg(p);
                    }
                });
    }
}
