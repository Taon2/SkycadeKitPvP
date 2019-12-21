package net.skycade.kitpvp.commands;

import net.skycade.SkycadeCore.utility.CoreUtil;
import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.events.KillTheKingEvent;
import net.skycade.kitpvp.events.capturetheflag.CaptureTheFlagFlagListener;
import net.skycade.kitpvp.nms.ActionBarUtil;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.skycade.kitpvp.Messages.*;

public class CommandSoup extends SkycadeCommand {
    private final static int COST = (KitPvP.getInstance().getConfig().getInt("soup-price"));
    private final static int COOLDOWN = (KitPvP.getInstance().getConfig().getInt("soup-cooldown"));

    private Map<UUID, Long> lastSoup = new HashMap<>();

    public CommandSoup() {
        super("soup");
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        Member member = MemberManager.getInstance().getMember((Player) commandSender);

        if (KillTheKingEvent.getInstance() != null && KillTheKingEvent.getInstance().getCurrentKing() != null) {
            if (member.getUUID().equals(KillTheKingEvent.getInstance().getCurrentKing())) {
                CANNOT_USE.msg(member.getPlayer(), "%thing%", "/soup", "%reason%", "as the King");
                return;
            }
        }

        if (CaptureTheFlagFlagListener.getInstance() != null && CaptureTheFlagFlagListener.getInstance().getCurrentCarrier() != null) {
            if (member.getUUID().equals(CaptureTheFlagFlagListener.getInstance().getCurrentCarrier().getUniqueId())) {
                CANNOT_USE.msg(member.getPlayer(), "%thing%", "/soup", "%reason%", "as the Flag Carrier");
                return;
            }
        }

        long now = System.currentTimeMillis();
        if (lastSoup.containsKey(member.getUUID())) {
            long diff = (now - lastSoup.get(member.getUUID())) / 1000L;
            if (diff < COOLDOWN) {
                ActionBarUtil.sendActionBarMessage(member.getPlayer(), ON_COOLDOWN.getMessage()
                                .replace("%time%", CoreUtil.niceFormat(COOLDOWN - ((Long) diff).intValue()))
                                .replace("%thing%", "/soup"),
                        4, KitPvP.getInstance());
                return;
            }
        }

        KitPvPStats stats = KitPvP.getInstance().getStats(member);
        int coins = stats.getCoins();

        // Applies soup cost reduction upgrade
        int cost = COST;
        if (member.hasSoupDiscount())
            cost = 50;

        if (coins - cost < 0) {
            NOT_ENOUGH.msg(member.getPlayer(), "%thing%", "coins");
            return;
        }

        stats.getActiveKit().getKit().giveSoup(member.getPlayer(), 30);
        stats.takeCoins(cost);

        ScoreboardInfo.getInstance().updatePlayer(member.getPlayer());

        lastSoup.put(member.getUUID(), now);
        YOU_PURCHASED.msg(member.getPlayer(), "%thing%", "soup", "%amount%", Integer.toString(cost), "%currency%", "coins");
    }
}
