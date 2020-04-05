package net.skycade.kitpvp.commands.staff;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.SkycadeCore.utility.command.addons.Permissible;
import net.skycade.SkycadeCore.utility.command.addons.SubCommand;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

import static net.skycade.kitpvp.Messages.*;

@Permissible("kitpvp.admin")
public class CommandEco extends SkycadeCommand {
    public CommandEco() {
        super("eco", Collections.singletonList("economy"));

        addSubCommands(
                new Give(),
                new Take(),
                new Set()
        );
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        ECO_USAGE.msg(commandSender);
    }

    @SubCommand
    private class Give extends SkycadeCommand {
        Give() {
            super("give");
        }

        @Override
        public void onCommand(CommandSender commandSender, String[] strings) {
            if (strings.length < 2) {
                ECO_USAGE.msg(commandSender);
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(strings[1]);
            } catch (NumberFormatException exception) {
                COULDNT_FIND.msg(commandSender, "%type%", "number", "%thing%", strings[1]);
                return;
            }

            if (strings[0].equalsIgnoreCase("all")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    incCoins(KitPvP.getInstance().getStats(p), commandSender, p, amount);
                }
            } else {
                if (Bukkit.getPlayer(strings[0]) == null) {
                    COULDNT_FIND.msg(commandSender, "%type%", "player", "%thing%", strings[0]);
                    return;
                }
                Player target = Bukkit.getPlayer(strings[0]);
                KitPvPStats targetStats = KitPvP.getInstance().getStats(target);

                incCoins(targetStats, commandSender, target, amount);

                ScoreboardInfo.getInstance().updatePlayer(target);
            }
        }
    }

    @SubCommand
    private class Take extends SkycadeCommand {
        Take() {
            super("take");
        }

        @Override
        public void onCommand(CommandSender commandSender, String[] strings) {
            if (strings.length < 2) {
                ECO_USAGE.msg(commandSender);
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(strings[1]);
            } catch (NumberFormatException exception) {
                COULDNT_FIND.msg(commandSender, "%type%", "number", "%thing%", strings[1]);
                return;
            }

            if (strings[0].equalsIgnoreCase("all")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    takeCoins(KitPvP.getInstance().getStats(p), commandSender, p, amount);
                }
            } else {
                if (Bukkit.getPlayer(strings[0]) == null) {
                    COULDNT_FIND.msg(commandSender, "%type%", "player", "%thing%", strings[0]);
                    return;
                }
                Player target = Bukkit.getPlayer(strings[0]);
                KitPvPStats targetStats = KitPvP.getInstance().getStats(target);

                takeCoins(targetStats, commandSender, target, amount);

                ScoreboardInfo.getInstance().updatePlayer(target);
            }
        }
    }

    @SubCommand
    private class Set extends SkycadeCommand {
        Set() {
            super("reset");
        }

        @Override
        public void onCommand(CommandSender commandSender, String[] strings) {
            if (strings.length < 1) {
                ECO_USAGE.msg(commandSender);
                return;
            }

            if (strings[0].equalsIgnoreCase("all")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    resetCoins(KitPvP.getInstance().getStats(p), commandSender, p);
                }
            } else {
                if (Bukkit.getPlayer(strings[0]) == null) {
                    COULDNT_FIND.msg(commandSender, "%type%", "player", "%thing%", strings[0]);
                    return;
                }
                Player target = Bukkit.getPlayer(strings[0]);
                KitPvPStats targetStats = KitPvP.getInstance().getStats(target);

                resetCoins(targetStats, commandSender, target);

                ScoreboardInfo.getInstance().updatePlayer(target);
            }
        }
    }

    private void resetCoins(KitPvPStats targetStats, CommandSender commandSender, Player target) {
        targetStats.setCoins(0);
        YOUR_CURRENCY_RESET.msg(target, "%currency%", "coins");
        CURRENCY_RESET.msg(commandSender, "%player%", target.getName(), "%currency%", "coins");
    }

    private void incCoins(KitPvPStats targetStats, CommandSender commandSender, Player target, int amount) {
        targetStats.giveCoins(amount);
        YOUR_CURRENCY_ADDED.msg(target, "%amount%", Integer.toString(amount), "%currency%", "coins", "%total%", Long.toString(targetStats.getCoins()));
        CURRENCY_ADDED.msg(commandSender, "%amount%", Integer.toString(amount), "%currency%", "coins", "%player%", target.getName());
    }

    private void takeCoins(KitPvPStats targetStats, CommandSender commandSender, Player target, int amount) {
        if (targetStats.getCoins() - amount < 0)
            resetCoins(targetStats, commandSender, target);
        else {
            targetStats.takeCoins(targetStats.getCoins() - amount);
            YOUR_CURRENCY_REMOVED.msg(target, "%amount%", Integer.toString(amount), "%currency%", "coins");
            CURRENCY_REMOVED.msg(commandSender, "%player%", target.getName(), "%amount%", Integer.toString(amount), "%currency%", "coins", "%total%", Long.toString(targetStats.getCoins()));
        }
    }
}
