package net.skycade.kitpvp.coreclasses.commands;

import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.Recharge;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.skycade.kitpvp.Messages.NOT_LOADED;

public class CommandManager extends Module implements CommandExecutor {

    private static CommandManager instance;

    public static double COMMAND_COOLDOWN = 0.5D;

    private final List<Command<? extends Module>> commands = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        for (String arg : args) label += " " + arg;
        Bukkit.getPluginManager().callEvent(new ServerCommandEvent(sender, label));
        if (!(sender instanceof ConsoleCommandSender)) return true;
        for (Command<? extends Module> command : commands) {
            List<String> aliases = new ArrayList<>();
            aliases.addAll(Arrays.asList(command.getAliases()));
            if (aliases.contains(cmd.getName())) {
                command.execute(null, cmd.getName(), args);
            }
        }
        return true;
    }

	/*
     * All permanent commands should have their own class that extends Command
	 * CommandEvent is to be used only for debugging
	 */

    @EventHandler
    public void on(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (!Recharge.recharge(p, "Command", COMMAND_COOLDOWN, false)) {
            e.setCancelled(true);
            return;
        }
        Member member = MemberManager.getInstance().getMember(p);
        if (member == null) {
            NOT_LOADED.msg(p);
            e.setCancelled(true);
            return;
        }

        List<String> args = new ArrayList<>(Arrays.asList(e.getMessage().split(" ")));
        String aliasUsed = args.remove(0).substring(1).trim();
        if (aliasUsed.isEmpty())
            return;
        for (Command<? extends Module> command : commands)
            for (String alias : command.getAliases())
                if (alias.equalsIgnoreCase(aliasUsed)) {
                    e.setCancelled(true);
                    for (Permission permission : command.getPermissions())
                        if (member.getPlayer().hasPermission(permission)) {
                            command.execute(member, aliasUsed, args.toArray(new String[args.size()]));
                            return;
                        }
                    member.message(getPermissionMessage(command.getPermission()));
                    return;
                }
        CommandEvent event = new CommandEvent(e.getPlayer(), member, aliasUsed, args.toArray(new String[args.size()]));
        callEvent(event);
        if (event.isCancelled())
            e.setCancelled(true);
    }

    public String getPermissionMessage(Permission permission) {
        return "§7This requires permission " + permission.getName() + "§7.";
    }

    public List<Command<? extends Module>> getCommands() {
        return commands;
    }

    @Override
    public void registerCommand(Command<? extends Module> command) {
        commands.add(command);
    }

    public void unregisterCommand(Class<? extends Command<?>> command) {
        Command<?> commandInstance = getCommand(command);
        while (commandInstance != null) {
            commands.remove(commandInstance);
            commandInstance = getCommand(command);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Command<?>> T getCommand(Class<T> command) {
        for (Command<?> command2 : commands)
            if (command2.getClass().equals(command))
                return (T) command2;
        return null;
    }

    public static CommandManager getInstance() {
        if (instance == null)
            instance = new CommandManager();
        return instance;
    }

}
