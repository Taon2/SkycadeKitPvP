package net.skycade.kitpvp.ui;

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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class HostMenu extends DynamicGui {
    public HostMenu() {
        super("Choose an Event", 3);
        setItemInteraction(11, new ItemBuilder(getSumo()).build(),
                (p, ev) -> {
                    if (getEventManager().isCooldownOn()) {
                        ev.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                        new HostMenu().open(p);
                        return;
                    }
                    if (getEventManager().getCurrentEvent() != EventType.IDLE) {
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
                        getEventManager().announceEvent(p, EventType.SUMO);
                        getEventManager().setHoster(p.getUniqueId());
                        p.closeInventory();
                    } else {
                        Messages.NO_HOST_CREDITS.msg(p);
                    }
                });

        setItemInteraction(13, new ItemBuilder(getLMS()).build(),
                (p, ev) -> {
                    if (getEventManager().isCooldownOn()) {
                        ev.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                        new HostMenu().open(p);
                        return;
                    }
                    if (getEventManager().getCurrentEvent() != EventType.IDLE) {
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

        setItemInteraction(15, new ItemBuilder(getBrackets()).build(),
                (p, ev) -> {
                    if (getEventManager().isCooldownOn()) {
                        ev.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                        new HostMenu().open(p);
                        return;
                    }
                    if (getEventManager().getCurrentEvent() != EventType.IDLE) {
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

    private ItemStack getSumo() {
        if (getEventManager().isCooldownOn()) {
            long currentTime = System.currentTimeMillis();
            long newTime = getEventManager().getGlobalCooldown() - currentTime;
            int toseconds = (int) (newTime / 1000);

            return new ItemBuilder(Material.POTATO_ITEM)
                    .setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lSumo"))
                    .addToLore(
                            ChatColor.translateAlternateColorCodes('&', " "),
                            ChatColor.translateAlternateColorCodes('&', "&fThe main objective is to knock"),
                            ChatColor.translateAlternateColorCodes('&', "&foff your opponents off the platform."),
                            ChatColor.translateAlternateColorCodes('&', " "),
                            ChatColor.translateAlternateColorCodes('&', "&fParticipants are chosen at random"),
                            ChatColor.translateAlternateColorCodes('&', "&fto fight."),
                            ChatColor.translateAlternateColorCodes('&', " "),
                            ChatColor.translateAlternateColorCodes('&', "&4&lCOOLDOWN"),
                            ChatColor.translateAlternateColorCodes('&', "&c" + CoreUtil.niceFormat(toseconds, false)),
                            ChatColor.translateAlternateColorCodes('&', " ")
                    )
                    .build();
        }
        if (getEventManager().getCurrentEvent() != EventType.IDLE){
            return new ItemBuilder(Material.POTATO_ITEM)
                    .setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lSumo"))
                    .addToLore(
                            ChatColor.translateAlternateColorCodes('&', " "),
                            ChatColor.translateAlternateColorCodes('&', "&fThe main objective is to knock"),
                            ChatColor.translateAlternateColorCodes('&', "&foff your opponents off the platform."),
                            ChatColor.translateAlternateColorCodes('&', " "),
                            ChatColor.translateAlternateColorCodes('&', "&fParticipants are chosen at random"),
                            ChatColor.translateAlternateColorCodes('&', "&fto fight."),
                            ChatColor.translateAlternateColorCodes('&', " "),
                            ChatColor.translateAlternateColorCodes('&', "&a&lTHERE IS ALREADY AN EVENT RUNNING"),
                            ChatColor.translateAlternateColorCodes('&', " ")
                    )
                    .build();
        }
        return new ItemBuilder(Material.POTATO_ITEM)
                .setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lSumo"))
                .addToLore(
                        ChatColor.translateAlternateColorCodes('&', " "),
                        ChatColor.translateAlternateColorCodes('&', "&fThe main objective is to knock"),
                        ChatColor.translateAlternateColorCodes('&', "&foff your opponents off the platform."),
                        ChatColor.translateAlternateColorCodes('&', " "),
                        ChatColor.translateAlternateColorCodes('&', "&fParticipants are chosen at random"),
                        ChatColor.translateAlternateColorCodes('&', "&fto fight."),
                        ChatColor.translateAlternateColorCodes('&', " ")
                )
                .build();
    }

    private ItemStack getLMS() {
        if (getEventManager().isCooldownOn()) {
            long currentTime = System.currentTimeMillis();
            long newTime = getEventManager().getGlobalCooldown() - currentTime;
            int toseconds = (int) (newTime / 1000);

            return new ItemBuilder(Material.GRASS)
                    .setDisplayName(ChatColor.translateAlternateColorCodes('&', "&2&lLast Man Standing"))
                    .addToLore(
                            ChatColor.translateAlternateColorCodes('&', " "),
                            ChatColor.translateAlternateColorCodes('&', "&fThe concept is simple,"),
                            ChatColor.translateAlternateColorCodes('&', "&fbe the last man alive!"),
                            ChatColor.translateAlternateColorCodes('&', " "),
                            ChatColor.translateAlternateColorCodes('&', "&fYou receive &b14 &fhealing (pots/soup)"),
                            ChatColor.translateAlternateColorCodes('&', "&fwhen you kill a player."),
                            ChatColor.translateAlternateColorCodes('&', " "),
                            ChatColor.translateAlternateColorCodes('&', "&4&lCOOLDOWN"),
                            ChatColor.translateAlternateColorCodes('&', "&c" + CoreUtil.niceFormat(toseconds, false)),
                            ChatColor.translateAlternateColorCodes('&', " ")
                    )
                    .build();
        }
        if (getEventManager().getCurrentEvent() != EventType.IDLE){
            return new ItemBuilder(Material.GRASS)
                    .setDisplayName(ChatColor.translateAlternateColorCodes('&', "&2&lLast Man Standing"))
                    .addToLore(
                    ChatColor.translateAlternateColorCodes('&', " "),
                    ChatColor.translateAlternateColorCodes('&', "&fThe concept is simple,"),
                    ChatColor.translateAlternateColorCodes('&', "&fbe the last man alive!"),
                    ChatColor.translateAlternateColorCodes('&', " "),
                    ChatColor.translateAlternateColorCodes('&', "&fYou receive &b14 &fhealing (pots/soup)"),
                    ChatColor.translateAlternateColorCodes('&', "&fwhen you kill a player."),
                    ChatColor.translateAlternateColorCodes('&', " "),
                    ChatColor.translateAlternateColorCodes('&', "&a&lTHERE IS ALREADY AN EVENT RUNNING"),
                    ChatColor.translateAlternateColorCodes('&', " ")
                    ).build();
        }

        return new ItemBuilder(Material.GRASS)
                .setDisplayName(ChatColor.translateAlternateColorCodes('&', "&2&lLast Man Standing"))
                .addToLore(
                        ChatColor.translateAlternateColorCodes('&', " "),
                        ChatColor.translateAlternateColorCodes('&', " "),
                        ChatColor.translateAlternateColorCodes('&', "&fThe concept is simple,"),
                        ChatColor.translateAlternateColorCodes('&', "&fbe the last man alive!"),
                        ChatColor.translateAlternateColorCodes('&', " "),
                        ChatColor.translateAlternateColorCodes('&', "&fYou receive &b14 &fhealing (pots/soup)"),
                        ChatColor.translateAlternateColorCodes('&', "&fwhen you kill a player."),
                        ChatColor.translateAlternateColorCodes('&', " ")
                )
                .build();
    }

    private ItemStack getBrackets() {
        if (getEventManager().isCooldownOn()) {
            long currentTime = System.currentTimeMillis();
            long newTime = getEventManager().getGlobalCooldown() - currentTime;
            int toseconds = (int) (newTime / 1000);

            return new ItemBuilder(Material.DIAMOND_SWORD)
                    .setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a&lBrackets"))
                    .addToLore(
                            ChatColor.translateAlternateColorCodes('&', " "),
                            ChatColor.translateAlternateColorCodes('&', "&fYour objective is to beat"),
                            ChatColor.translateAlternateColorCodes('&', "&fyour opponent in a 1v1."),
                            ChatColor.translateAlternateColorCodes('&', " "),
                            ChatColor.translateAlternateColorCodes('&', "&fParticipants are chosen at random"),
                            ChatColor.translateAlternateColorCodes('&', "&fto fight."),
                            ChatColor.translateAlternateColorCodes('&', " "),
                            ChatColor.translateAlternateColorCodes('&', "&4&lCOOLDOWN"),
                            ChatColor.translateAlternateColorCodes('&', "&c" + CoreUtil.niceFormat(toseconds, false))
                    )
                    .build();
        }

        if (getEventManager().getCurrentEvent() != EventType.IDLE){
            return new ItemBuilder(Material.DIAMOND_SWORD)
                    .setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a&lBrackets"))
                    .addToLore(
                            ChatColor.translateAlternateColorCodes('&', " "),
                            ChatColor.translateAlternateColorCodes('&', "&fYour objective is to beat"),
                            ChatColor.translateAlternateColorCodes('&', "&fyour opponent in a 1v1."),
                            ChatColor.translateAlternateColorCodes('&', " "),
                            ChatColor.translateAlternateColorCodes('&', "&fParticipants are chosen at random"),
                            ChatColor.translateAlternateColorCodes('&', "&fto fight."),
                            ChatColor.translateAlternateColorCodes('&', " "),
                            ChatColor.translateAlternateColorCodes('&', "&a&lTHERE IS ALREADY AN EVENT RUNNING"),
                            ChatColor.translateAlternateColorCodes('&', " ")
                    )
                    .build();
        }

        return new ItemBuilder(Material.DIAMOND_SWORD)
                .setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a&lBrackets"))
                .addToLore(
                        ChatColor.translateAlternateColorCodes('&', " "),
                        ChatColor.translateAlternateColorCodes('&', "&fYour objective is to beat"),
                        ChatColor.translateAlternateColorCodes('&', "&fyour opponent in a 1v1."),
                        ChatColor.translateAlternateColorCodes('&', " "),
                        ChatColor.translateAlternateColorCodes('&', "&fParticipants are chosen at random"),
                        ChatColor.translateAlternateColorCodes('&', "&fto fight.")
                )
                .build();
    }

    private EventManager getEventManager() {
        return KitPvP.getInstance().getEventManager();
    }
}
