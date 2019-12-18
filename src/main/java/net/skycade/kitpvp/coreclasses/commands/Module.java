package net.skycade.kitpvp.coreclasses.commands;

import net.skycade.kitpvp.KitPvP;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Module implements Listener {

    private static final Set<Module> modules = new HashSet<>();

    protected final KitPvP plugin;
    private final List<Listener> listeners = new ArrayList<>();

    public Module() {
        plugin = KitPvP.getInstance();
        registerListener(this);
        modules.add(this);
    }

    public PluginManager getPluginManager() {
        return Bukkit.getPluginManager();
    }

    public BukkitScheduler getScheduler() {
        return Bukkit.getScheduler();
    }

    public void callEvent(Event event) {
        getPluginManager().callEvent(event);
    }

    public void registerListener(Listener listener) {
        Bukkit.getServer().getPluginManager().registerEvents(listener, getPlugin());
    }

    public void registerCommand(Command<? extends Module> command) {
        CommandManager.getInstance().registerCommand(command);
    }

    public void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    public void log() {
        log("");
    }

    public void log(String message) {
        // TODO: name modules
        System.out.println("[Module] " + message);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    protected void disable() {
        for (Listener listener : listeners)
            unregisterListener(listener);
        unregisterListener(this);
    }

    public void onDisable() {
        //TODO: do something?
    }

    public static void onDisable(JavaPlugin plugin) {
        for (Module module : modules)
            module.onDisable();
    }

}