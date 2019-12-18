package net.skycade.kitpvp.scoreboard;

import net.skycade.SkycadeCore.utility.scoreboard.Display;
import net.skycade.SkycadeCore.utility.scoreboard.ScoreboardManager;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ScoreboardHandler {
    private ScoreboardHandler() {}

    public static void init() {
        ScoreboardManager.getInstance().setCurrentInitializer((p, d) -> {
            Member member = MemberManager.getInstance().getMember(p);
            KitPvP plugin = KitPvP.getInstance();
            KitPvPStats stats = plugin.getStats(member);
            int kills = stats.getKills();
            int deaths = stats.getDeaths();
            double kd = UtilMath.getKDR(kills, deaths);
            double kdr = (Math.round(kd * 10.0) / 10.0);
            String kdColor = kdr >= 1 ? "\u00A7a" : "\u00A7c";
            int ks = stats.getStreak();
            String kit = stats.getActiveKit().getKit().getName();
            int coins = stats.getCoins();
            String unlockColor = plugin.getStats().get(member.getUUID()).getKits().size() == plugin.getKitManager().getKits().size() ? "§6" : "§e";
            String kitsUnlocked = unlockColor + plugin.getStats().get(member.getUUID()).getKits().size() + "/§6" + KitPvP.getInstance().getAvailableKits();

            int i = 21;

            d.setTitle(ChatColor.translateAlternateColorCodes('&', "&b&lSkycade &f&lKitPvP"));

            d.setScore("w1", "    ", --i);
            d.setScore("kills", "Kills: \u00A7a" + kills, --i);
            d.setScore("deaths", "Deaths: \u00A7c" + deaths, --i);
            d.setScore("kd", "K/D: " + kdColor + kdr, --i);
            d.setScore("w2", "   ", --i);
            d.setScore("killstreak", "Killstreak: §a" + ks, --i);

            UUID highestKsUUID = plugin.getKsUpdater().getHighestKsPlayer();
            if (highestKsUUID != null && plugin.getStats().containsKey(highestKsUUID)) {
                d.setScore("highks1","Highest ks: " + ChatColor.GREEN, --i);

                String name = Bukkit.getOfflinePlayer(highestKsUUID).getName();
                d.setScore("highks2","§7" + name + ": " + plugin.getKsUpdater().getScore(), --i);
            } else {
                d.setScore("highks1","Highest ks: " + ChatColor.GREEN, --i);
                d.setScore("highks2","§7N/A", --i);
            }

            d.setScore("w3", "  ", --i);

            d.setScore("kit","Kit: §a" + kit, --i);
            d.setScore("coins","Coins: §6" + coins, --i);
            d.setScore("kits","Kits unlocked: " + unlockColor + kitsUnlocked, --i);

            d.setScore("w4", " ", --i);

            d.setScore("footer", ChatColor.translateAlternateColorCodes('&', "&b&lplay.skycade.net"), --i);
        });
    }

    public static void updatePlayer(Player p) {
        Display d = ScoreboardManager.getInstance().getDisplay(p);
        if (d == null) return;

        Member member = MemberManager.getInstance().getMember(p);
        KitPvP plugin = KitPvP.getInstance();
        KitPvPStats stats = plugin.getStats(member);
        int kills = stats.getKills();
        int deaths = stats.getDeaths();
        double kd = UtilMath.getKDR(kills, deaths);
        double kdr = (Math.round(kd * 10.0) / 10.0);
        String kdColor = kdr >= 1 ? "\u00A7a" : "\u00A7c";
        int ks = stats.getStreak();
        String kit = stats.getActiveKit().getKit().getName();
        int coins = stats.getCoins();
        String unlockColor = plugin.getStats().get(member.getUUID()).getKits().size() == plugin.getKitManager().getKits().size() ? "§6" : "§e";
        String kitsUnlocked = unlockColor + plugin.getStats().get(member.getUUID()).getKits().size() + "/§6" + KitPvP.getInstance().getAvailableKits();

        d.updateScore("kills", "Kills: \u00A7a" + kills);
        d.updateScore("deaths", "Deaths: \u00A7c" + deaths);
        d.updateScore("kd", "K/D: " + kdColor + kdr);
        d.updateScore("killstreak", "Killstreak: §a" + ks);

        UUID highestKsUUID = plugin.getKsUpdater().getHighestKsPlayer();
        if (highestKsUUID != null && plugin.getStats().containsKey(highestKsUUID)) {
            d.updateScore("highks1","Highest ks: " + ChatColor.GREEN);

            String name = Bukkit.getOfflinePlayer(highestKsUUID).getName();
            d.updateScore("highks2","§7" + name + ": " + plugin.getKsUpdater().getScore());
        } else {
            d.updateScore("highks1","Highest ks: " + ChatColor.GREEN);
            d.updateScore("highks2","§7N/A");
        }

        d.updateScore("kit","Kit: §a" + kit);
        d.updateScore("coins","Coins: §6" + coins);
        d.updateScore("kits","Kits unlocked: " + unlockColor + kitsUnlocked);
    }
}


/*
KitPvPStats stats = plugin.getStats(member);
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();

        int kills = stats.getKills();
        int deaths = stats.getDeaths();
        double kd = UtilMath.getKDR(kills, deaths);
        double kdr = (Math.round(kd * 10.0) / 10.0);

        String kdColor = kdr >= 1 ? "§a" : "§c";

        int ks = stats.getStreak();

        String kit = stats.getActiveKit().getKit().getName();
        int coins = stats.getCoins();

        String unlockColor = plugin.getStats().get(member.getUUID()).getKits().size() == plugin.getKitManager().getKits().size() ? "§6" : "§e";
        String kitsUnlocked = unlockColor + plugin.getStats().get(member.getUUID()).getKits().size() + "/§6" + KitPvP.getInstance().getAvailableKits();

        Objective o = board.registerNewObjective("test", "dummy");
        o.setDisplayName(ChatColor.translateAlternateColorCodes('&', KitPvP.getInstance().getConfig().getString("scoreboard.name")));
        o.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score s15 = o.getScore(UtilString.getWhitespace(3));
        s15.setScore(15);

        Score s14 = o.getScore("Kills: §a" + kills);
        s14.setScore(14);

        Score s13 = o.getScore("Deaths: §c" + deaths);
        s13.setScore(13);

        Score s12 = o.getScore("K/D: " + kdColor + kdr);
        s12.setScore(12);

        Score s11 = o.getScore(UtilString.getWhitespace(2));
        s11.setScore(11);

        Score s10 = o.getScore("Killstreak: §a" + ks);
        s10.setScore(10);

        UUID highestKsUUID = plugin.getKsUpdater().getHighestKsPlayer();
        if (highestKsUUID != null && plugin.getStats().containsKey(highestKsUUID)) {
            Score s9 = o.getScore("Highest ks: §a");
            s9.setScore(9);

            String name = Bukkit.getOfflinePlayer(highestKsUUID).getName();
            Score s8 = o.getScore("§7" + name + ": " + plugin.getKsUpdater().getScore());
            s8.setScore(8);
        }

        if (plugin.getKitManager().getSignMap().containsKey(member.getUUID())) {
            Score s7 = o.getScore("Refresh cd: §a" + plugin.getKitManager().getSignMap().get(member.getUUID()) + "s");
            s7.setScore(7);
        }

        Score s6 = o.getScore(UtilString.getWhitespace(1));
        s6.setScore(6);

        Score s5 = o.getScore("Kit: §a" + kit);
        s5.setScore(5);

        Score s4 = o.getScore("Coins: §6" + coins);
        s4.setScore(4);

        Score s3 = o.getScore("Kits unlocked: " + unlockColor + kitsUnlocked);
        s3.setScore(3);

        Score s2 = o.getScore(UtilString.getWhitespace(0));
        s2.setScore(2);

        Score s1 = o.getScore(ChatColor.translateAlternateColorCodes('&', KitPvP.getInstance().getConfig().getString("scoreboard.bottom-link")));
        s1.setScore(1);

        member.getPlayer().setScoreboard(board);
 */