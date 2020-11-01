package net.skycade.kitpvp.scoreboard;

import net.skycade.SkycadeCore.information.GlobalDisplay;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class ScoreboardInfo {

    private static ScoreboardInfo instance;

    public final GlobalDisplay.Info killsDeathsInfo;
    public final GlobalDisplay.Info kitCoins;

    private final DecimalFormat df = new DecimalFormat("###,###,###,###.##");

    private ScoreboardInfo() {

        killsDeathsInfo = new GlobalDisplay.Info("kitpvp-kdkdks") {
            @Override
            public List<String> apply(Player p) {
                Member member = MemberManager.getInstance().getMember(p);
                double kdr = UtilMath.getKDR(member.getKills(), member.getDeaths());
                return Arrays.asList(
                        ChatColor.GRAY + "Kills: " + ChatColor.GREEN + df.format(member.getKills()),
                        ChatColor.GRAY + "Deaths: " + ChatColor.RED + df.format(member.getDeaths()),
                        ChatColor.GRAY + "K/D: " + (kdr >= 1 ? ChatColor.GREEN : ChatColor.RED) + df.format(kdr),
                        ChatColor.GRAY + "Killstreak: " + ChatColor.GREEN + df.format(KitPvP.getInstance().getStats(p).getStreak()) +
                                ChatColor.GRAY + " (" + ChatColor.YELLOW + "" + df.format(KitPvP.getInstance().getStats(p).getHighestStreak())
                                + ChatColor.GRAY + ")"
                );
            }

            @Override
            public int getPosition() {
                return 2;
            }

            @Override
            public int getSize() {
                return 4;
            }
        };

        kitCoins = new GlobalDisplay.Info("kitpvp-kitcoins") {
            @Override
            public List<String> apply(Player p) {
                KitPvPStats stats = KitPvP.getInstance().getStats(p);
                return Arrays.asList(
                        "    ",
                        ChatColor.GRAY + "Coins: " + ChatColor.GOLD + df.format(stats.getCoins()),
                        ChatColor.GRAY + "Event Tokens: " + ChatColor.GOLD + df.format(stats.getEventTokens()),
                        "  "
                );
            }

            @Override
            public int getPosition() {
                return 5;
            }

            @Override
            public int getSize() {
                return 4;
            }
        };

        GlobalDisplay.registerInformation(killsDeathsInfo);
        GlobalDisplay.registerInformation(kitCoins);
    }

    public static ScoreboardInfo getInstance() {
        if (instance == null) instance = new ScoreboardInfo();
        return instance;
    }

    public void updatePlayer(Player player) {
        killsDeathsInfo.update(player);
        kitCoins.update(player);
    }
}
