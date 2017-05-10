package net.skycade.kitpvp.scoreboard;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.coreclasses.utils.UtilString;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.*;

import java.util.UUID;


public class KitPvPScoreboard implements Listener {

    private final KitPvP plugin;

    public KitPvPScoreboard(KitPvP plugin) {
        this.plugin = plugin;
        startRefresh();
    }

    private void startRefresh() {
        Bukkit.getScheduler().runTaskTimer(plugin, () ->
            Bukkit.getOnlinePlayers().forEach(this::updateScoreBoard)
        , 5 * 20, 5 * 20);
    }

    public void updateScoreBoard(Player p) {
        updateScoreBoard(MemberManager.getInstance().getMember(p));
    }

    public void updateScoreBoard(Member member) {
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
        String kitsUnlocked = unlockColor + plugin.getStats().get(member.getUUID()).getKits().size() + "/§6" + KitType.values().length;

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

            String name = Bukkit.getPlayer(highestKsUUID) != null ? Bukkit.getPlayer(highestKsUUID).getName()
                    :  Bukkit.getOfflinePlayer(highestKsUUID).getName();
            Score s8 = o.getScore("§7" + name + ": " + plugin.getStats().get(highestKsUUID).getStreak());
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
    }

}
