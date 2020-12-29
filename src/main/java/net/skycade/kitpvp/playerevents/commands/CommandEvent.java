package net.skycade.kitpvp.playerevents.commands;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.SkycadeCore.utility.command.addons.Permissible;
import net.skycade.SkycadeCore.utility.command.addons.SubCommand;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.Messages;
import net.skycade.kitpvp.playerevents.EventManager;
import net.skycade.kitpvp.playerevents.EventType;
import net.skycade.kitpvp.ui.HostMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class CommandEvent extends SkycadeCommand {
    public CommandEvent() {
        super("event", Arrays.asList("e", "events"));

        addSubCommands(
            new Host(),
            new Join(),
            new Quit(),
            new Spectate(),
            new ForceEnd(),
            new ResetCooldown()
        );
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] args) {
        Messages.EVENT_COMMAND_USAGE.msg(commandSender);
    }

    @SubCommand
    private class Host extends SkycadeCommand{

        public Host() {
            super("host");
        }

        @Override
        public void onCommand(CommandSender commandSender, String[] strings) {
            if (commandSender instanceof Player) {
                Player p = (Player) commandSender;
                new HostMenu().open(p);
            }
        }
    }
    @SubCommand
    private class Join extends SkycadeCommand{

        public Join() {
            super("join");
        }

        public ArrayList<UUID> queue = new ArrayList<>();

        @Override
        public void onCommand(CommandSender commandSender, String[] strings) {
            if (commandSender instanceof Player) {
                Player p = (Player) commandSender;
                if (!KitPvP.getInstance().isInSpawnArea(p)) {
                    Messages.MUST_BE_AT_SPAWN.msg(p);
                    return;
                }
                EventManager manager = KitPvP.getInstance().getEventManager();
                if (manager.getCurrentEvent() == EventType.IDLE) {
                    Messages.NO_EVENT_RUNNING.msg(p);
                    return;
                }
                if (!manager.isJoinable()) {
                    Messages.CANNOT_JOIN_EVNET.msg(p);
                    return;
                }
                if (queue.contains(p.getUniqueId())){
                    Messages.ALREADY_JOINING.msg(p);
                    return;
                }
                queue.add(p.getUniqueId());
                Messages.JOINING_EVENT.msg(p);

                new BukkitRunnable(){

                    @Override
                    public void run() {
                        queue.remove(p.getUniqueId());
                        manager.join(p);
                    }
                }.runTaskLater(KitPvP.getInstance(), 10);
            }
        }
    }
    @SubCommand
    private class Quit extends SkycadeCommand{

        public Quit() {
            super("quit", Collections.singletonList("leave"));
        }

        private ArrayList<UUID> queue = new ArrayList<>();

        @Override
        public void onCommand(CommandSender commandSender, String[] strings) {
            if (commandSender instanceof Player) {
                Player p = (Player) commandSender;
                EventManager manager = KitPvP.getInstance().getEventManager();
                if (manager.getCurrentEvent() == EventType.IDLE) {
                    Messages.NO_EVENT_RUNNING.msg(p);
                    return;
                }
                if (queue.contains(p.getUniqueId())){
                    Messages.ALREADY_LEAVING.msg(p);
                    return;
                }
                queue.add(p.getUniqueId());
                Messages.LEAVING_EVENT.msg(p);

                new BukkitRunnable(){

                    @Override
                    public void run() {
                        queue.remove(p.getUniqueId());
                        manager.quit(p);
                    }
                }.runTaskLater(KitPvP.getInstance(), 10);
            }
        }
    }

    @SubCommand
    private class Spectate extends SkycadeCommand{
        public ArrayList<UUID> queue = new ArrayList<>();
        public Spectate() {
            super("spectate", Collections.singletonList("spec"));
        }

        @Override
        public void onCommand(CommandSender commandSender, String[] strings) {
            if (commandSender instanceof Player){
                Player p = (Player) commandSender;
                EventManager manager = KitPvP.getInstance().getEventManager();
                if (manager.getCurrentEvent() == EventType.IDLE) {
                    Messages.NO_EVENT_RUNNING.msg(p);
                    return;
                }
                if (manager.getSumo().isPlaying(p)){
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou are already in this Event!"));
                    return;
                }
                if (queue.contains(p.getUniqueId())){
                    Messages.ALREADY_JOINING.msg(p);
                    return;
                }
                queue.add(p.getUniqueId());
                Messages.JOINING_EVENT.msg(p);

                new BukkitRunnable(){

                    @Override
                    public void run() {
                        queue.remove(p.getUniqueId());

                        manager.spectate(p);
                    }
                }.runTaskLater(KitPvP.getInstance(), 10);
            }
        }
    }

    @SubCommand
    @Permissible("kitpvp.admin")
    private class ForceEnd extends SkycadeCommand{

        public ForceEnd() {
            super("forceend", Collections.singletonList("end"));
        }

        @Override
        public void onCommand(CommandSender commandSender, String[] strings) {
            if (commandSender instanceof Player) {
                Player p = (Player) commandSender;
                if (getEventManager().getCurrentEvent() == EventType.IDLE) {
                    p.sendMessage("no event running");
                    return;
                }
                EventType type = getEventManager().getCurrentEvent();
                if (type == EventType.SUMO) {
                    p.sendMessage(ChatColor.GREEN + "You have ended the Sumo event.");
                    getEventManager().getSumo().forceEnd();
                    getEventManager().setStopped(true);
                }
                if (type == EventType.LMS) {
                    p.sendMessage(ChatColor.GREEN + "You have ended the LMS event.");
                    getEventManager().getLMS().forceEnd();
                    getEventManager().setStopped(true);
                }
                if (type == EventType.BRACKETS) {
                    p.sendMessage(ChatColor.GREEN + "You have ended the Brackets event.");
                    getEventManager().getBrackets().forceEnd();
                    getEventManager().setStopped(true);
                }
            }
        }

        private EventManager getEventManager() {
            return KitPvP.getInstance().getEventManager();
        }
    }
    @SubCommand
    @Permissible("kitpvp.admin")
    private class ResetCooldown extends SkycadeCommand{

        public ResetCooldown() {
            super("resetcooldown");
        }


        @Override
        public void onCommand(CommandSender commandSender, String[] strings) {
            if (!getEventManager().isCooldownOn()){
                commandSender.sendMessage("An event has not been hosted recently.");
                return;
            }
            getEventManager().setCooldownOn(false);
            getEventManager().setGlobalCooldown(0);
            commandSender.sendMessage("The Event cooldown has been wiped!");
        }

        private EventManager getEventManager(){
            return KitPvP.getInstance().getEventManager();
        }
    }
}
