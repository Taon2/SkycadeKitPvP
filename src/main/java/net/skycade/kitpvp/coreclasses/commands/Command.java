package net.skycade.kitpvp.coreclasses.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.UtilPacket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.List;

public abstract class Command<M extends Module> {

    private final M module;
    protected final KitPvP plugin;

    private String description;
    private List<Permission> permissions = new ArrayList<>();
    private String[] aliases;
    private String[] usage = new String[0];
    private boolean visible = true, consoleCompatible = true;

    public Command(M module, String description, Permission permission, String... aliases) {
        this.module = module;
        this.plugin = KitPvP.getInstance();
        this.permissions.add(permission);
        this.description = description;
        this.aliases = aliases;
    }

    public abstract void execute(Member member, String aliasUsed, String... args);

    public List<String> onTabComplete(Member member, String aliasUsed, String... args) {
        List<String> l = new ArrayList<>();
        if (args.length == 0)
            return l;
        String arg = args[args.length - 1];
        for (Player pl : Bukkit.getOnlinePlayers())
            if (!l.contains(pl.getName()) && pl.getName().toLowerCase().startsWith(arg.toLowerCase()))
                l.add(pl.getName());
        return l;
    }

    public M getModule() {
        return module;
    }

    public String getDescription() {
        return description;
    }

    public Permission getPermission() {
        return permissions.get(0);
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public String[] getAliases() {
        return aliases;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }

    public String[] getUsage() {
        return usage;
    }

    public String getUsageToString() {
        String usageStr = "";
        for (String usage : usage)
            usageStr += usage + " ";
        return usageStr;
    }

    public void setUsage(String... usage) {
        this.usage = usage;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isConsoleCompatible() {
        return consoleCompatible;
    }

    public void setConsoleCompatible(boolean consoleCompatible) {
        this.consoleCompatible = consoleCompatible;
    }

    public void addPermission(Permission permission) {
        permissions.add(permission);
    }

    public static boolean getPlayer(Member member, String name) {
        boolean online = Bukkit.getPlayer(name) != null;

        if (!online) {
            couldNotFind(member, "Player", name);
        }

        return online;
    }

    public Member getOfflineMember(Member member, String name) {
        Member subject = MemberManager.getInstance().getMember(name);
        if (subject == null)
            couldNotFind(member, "Offline Player", name);
        return subject;
    }

    public boolean parseInt(Member member, String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException exception) {
            if (member != null) couldNotFind(member, "number", s);
            return false;
        }
        return true;
    }

    public boolean parseDouble(Member member, String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException exception) {
            couldNotFind(member, "decimal", s);
            return false;
        }
        return true;
    }

    public boolean checkArgs(Member member, String aliasUsed, String[] args) {
        return checkArgs(member, aliasUsed, args, usage.length);
    }

    public boolean checkArgs(Member member, String aliasUsed, String[] args, int required) {
        boolean check = args.length >= required;
        if (!check)
            if (member != null) usage(member, aliasUsed);
        return check;
    }

    /**
     * Must be added to plugin.yml!
     */
    public void registerAsBukkitCommand() {
        for (String alias : aliases)
            KitPvP.getInstance().getCommand(alias).setExecutor(CommandManager.getInstance());
    }

    public static void couldNotFind(Member member, String thing, String attempt) {
        if (member != null) member.message("Could not find " + thing + "§7 '§e" + attempt + "§7'.");
    }

    public void usage(Member member, String aliasUsed) {
        if (member.isConsole()) {
            String usage = aliasUsed;
            for (String s : this.usage)
                usage += " " + s;
            member.message(usage);
            return;
        }
        member.getPlayer().spigot().sendMessage(getUsageFormatted(aliasUsed));
    }

    public TextComponent getUsageFormatted(String aliasUsed) {
        TextComponent textComponent = UtilPacket.createTextComponent("/", ChatColor.GRAY);
        TextComponent command = UtilPacket.createTextComponent(aliasUsed.toLowerCase(), ChatColor.RED);
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(getDescription() + ".").create());
        command.setHoverEvent(hoverEvent);
        String suggest = "/" + aliasUsed;
        for (String s : usage)
            suggest += " " + s.substring(Math.min(1, s.length()), Math.max(s.length() - 1, 0));
        ClickEvent clickEvent = new ClickEvent(getUsage().length > 0 ? ClickEvent.Action.SUGGEST_COMMAND : ClickEvent.Action.RUN_COMMAND, suggest);
        command.setClickEvent(clickEvent);
        textComponent.addExtra(command);
        for (String s : usage) {
            if (s.length() < 3)
                continue;
            textComponent.addExtra(UtilPacket.createTextComponent(" " + s.substring(0, 1), ChatColor.GRAY));
            TextComponent textComponentUsage = UtilPacket.createTextComponent(s.substring(1, s.length() - 1), ChatColor.RED);
            textComponentUsage.setClickEvent(clickEvent);
            textComponentUsage.setHoverEvent(hoverEvent);
            textComponent.addExtra(textComponentUsage);
            textComponent.addExtra(UtilPacket.createTextComponent(s.substring(s.length() - 1, s.length()), ChatColor.GRAY));
        }
        return textComponent;
    }


}