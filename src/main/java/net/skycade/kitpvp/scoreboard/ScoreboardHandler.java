package net.skycade.kitpvp.scoreboard;

import net.skycade.SkycadeCore.displays.SidebarType;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.coreclasses.utils.UtilString;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScoreboardHandler extends SidebarType {
    public ScoreboardHandler() {
        super(2);
    }

    @Override
    public List<String> getText(Player viewer) {
        Member member = MemberManager.getInstance().getMember(viewer);
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

        List<String> text = new ArrayList<>();

        text.add(UtilString.getWhitespace(3));
        text.add("Kills: \u00A7a" + kills);
        text.add("Deaths: \u00A7c" + deaths);
        text.add("K/D: " + kdColor + kdr);
        text.add(UtilString.getWhitespace(2));
        text.add("Killstreak: §a" + ks);

        UUID highestKsUUID = plugin.getKsUpdater().getHighestKsPlayer();
        if (highestKsUUID != null && plugin.getStats().containsKey(highestKsUUID)) {
            text.add("Highest ks: §a");

            String name = Bukkit.getOfflinePlayer(highestKsUUID).getName();
            text.add("§7" + name + ": " + plugin.getKsUpdater().getScore());
        }

        if (plugin.getKitManager().getSignMap().containsKey(member.getUUID())) {
            text.add("Refresh cd: §a" + plugin.getKitManager().getSignMap().get(member.getUUID()) + "s");
        }

        text.add(UtilString.getWhitespace(1));
        text.add("Kit: §a" + kit);
        text.add("Coins: §6" + coins);
        text.add("Kits unlocked: " + unlockColor + kitsUnlocked);

        text.add(UtilString.getWhitespace(0));

        text.add(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("scoreboard.bottom-link")));

        return text;
    }

    @Override
    public boolean isVisible(Player player) {
        return true;
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