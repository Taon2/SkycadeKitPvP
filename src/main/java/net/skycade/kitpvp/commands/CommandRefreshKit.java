package net.skycade.kitpvp.commands;

import net.skycade.SkycadeCore.utility.CoreUtil;
import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.events.KillTheKingEvent;
import net.skycade.kitpvp.events.TagEvent;
import net.skycade.kitpvp.events.CaptureTheFlagEvent;
import net.skycade.kitpvp.nms.ActionBarUtil;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import static net.skycade.kitpvp.Messages.*;

public class CommandRefreshKit extends SkycadeCommand {
    private File file;
    private YamlConfiguration yaml;
    private final static int COST = (KitPvP.getInstance().getConfig().getInt("refreshkit-price"));
    private final static int COOLDOWN = (KitPvP.getInstance().getConfig().getInt("refreshkit-cooldown"));

    private Map<UUID, Long> lastRefresh = new HashMap<>();

    public CommandRefreshKit() {
        super("refreshkit");
        configManager();
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        Member member = MemberManager.getInstance().getMember((Player) commandSender);

        if (KillTheKingEvent.getInstance() != null && KillTheKingEvent.getInstance().getCurrentKing() != null) {
            if (member.getUUID().equals(KillTheKingEvent.getInstance().getCurrentKing())) {
                CANNOT_USE.msg(member.getPlayer(), "%thing%", "/refreshkit", "%reason%", ChatColor.RED + "as " + ChatColor.AQUA + "the King");
                return;
            }
        }

        if (CaptureTheFlagEvent.getInstance().getBegin() != null) {
            CANNOT_USE.msg(member.getPlayer(), "%thing%", "/refreshkit", "%reason%", ChatColor.RED + "during " + ChatColor.AQUA + "Capture The Flag");
            return;
        }

        if (TagEvent.getInstance().getBegin() != null) {
            CANNOT_USE.msg(member.getPlayer(), "%thing%", "/refreshkit", "%reason%", ChatColor.RED + "during " + ChatColor.AQUA + "Infection");
            return;
        }

        long now = System.currentTimeMillis();

        // Applies cooldown reduction upgrade
        int cooldown = COOLDOWN;
        if (member.hasRefreshKitCooldownReduction())
            cooldown = 5400;

        try {
            lastRefresh.put(member.getUUID(), yaml.getLong("current-refreshkit-cooldowns." + member.getUUID()));
        } catch (Exception ignored) {}

        if (lastRefresh.containsKey(member.getUUID())) {
            long diff = (now - lastRefresh.get(member.getUUID())) / 1000L;

            if (diff < cooldown) {
                ActionBarUtil.sendActionBarMessage(member.getPlayer(), ON_COOLDOWN.getMessage()
                                .replace("%time%", CoreUtil.niceFormat(cooldown - ((Long) diff).intValue()))
                                .replace("%thing%", "/refreshkit"),
                        4, KitPvP.getInstance());
                return;
            }
        }

        KitPvPStats stats = KitPvP.getInstance().getStats(member);
        long coins = stats.getCoins();
        if (coins < COST) {
            NOT_ENOUGH.msg(member.getPlayer(), "%thing%", "coins");
            return;
        }

        UtilPlayer.reset(member.getPlayer());
        stats.getActiveKit().getKit().beginApplyKit(member.getPlayer());
        stats.getActiveKit().getKit().giveSoup(member.getPlayer(), 35);
        stats.takeCoins(COST);

        ScoreboardInfo.getInstance().updatePlayer(member.getPlayer());

        lastRefresh.put(member.getUUID(), now);
        YOU_PURCHASED.msg(member.getPlayer(), "%thing%", "kit refresh", "%amount%", Integer.toString(COST), "%currency%", "coins");
        yaml.set(("current-refreshkit-cooldowns." + member.getUUID()), now);
        save();
    }

    private void configManager() {
        file = new File(KitPvP.getInstance().getDataFolder(), "commandcooldowns.yml");

        if (!file.exists()) {
            yaml = new YamlConfiguration();
            save();
        } else {
            yaml = YamlConfiguration.loadConfiguration(file);
            save();
        }
    }

    private void save() {
        try {
            yaml.save(file);
        } catch (IOException e) {
            KitPvP.getInstance().getLogger().log(Level.SEVERE, "Couldn't save commandcooldowns yaml file.", e);
        }
    }
}
