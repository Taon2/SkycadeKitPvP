package net.skycade.kitpvp.commands;

import net.skycade.SkycadeCore.utility.CoreUtil;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.events.KillTheKingEvent;
import net.skycade.kitpvp.events.TagEvent;
import net.skycade.kitpvp.events.TeamFightEvent;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import static net.skycade.kitpvp.Messages.*;

public class CommandRefreshKit extends Command<KitManager> {
    private File file;
    private YamlConfiguration yaml;
    private final static int COST = (KitPvP.getInstance().getConfig().getInt("refreshkit-price"));
    private final static int COOLDOWN = (KitPvP.getInstance().getConfig().getInt("refreshkit-cooldown"));

    private Map<UUID, Long> lastRefresh = new HashMap<>();

    public CommandRefreshKit(KitManager module) {
        super(module, "Refresh your kit for " + COST + " coins.", new Permission("kitpvp.default", PermissionDefault.TRUE), "refreshkit");
        configManager();
    }

    @Override
    public void execute(Member member, String aliasUsed, String... args) {
        if (KillTheKingEvent.getInstance() != null && KillTheKingEvent.getInstance().getCurrentKing() != null) {
            if (member.getUUID().equals(KillTheKingEvent.getInstance().getCurrentKing())) {
                CANNOT_USE.msg(member.getPlayer(), "%thing%", "/refreshkit", "%reason%", "as the King");
                return;
            }
        }

        if (TeamFightEvent.getInstance().getBegin() != null) {
            CANNOT_USE.msg(member.getPlayer(), "%thing%", "/refreshkit", "%reason%", "during Team Fight");
            return;
        }

        if (TagEvent.getInstance().getBegin() != null) {
            CANNOT_USE.msg(member.getPlayer(), "%thing%", "/refreshkit", "%reason%", "during Infection");
            return;
        }

        long now = System.currentTimeMillis();

        try {
            lastRefresh.put(member.getUUID(), yaml.getLong("current-refreshkit-cooldowns." + member.getUUID()));
        } catch (Exception ignored) {}

        if (lastRefresh.containsKey(member.getUUID())) {
            long diff = (now - lastRefresh.get(member.getUUID())) / 1000L;

            if (diff < COOLDOWN) {
                ON_COOLDOWN.msg(member.getPlayer(), "%time%", CoreUtil.niceFormat(COOLDOWN - ((Long) diff).intValue()), "%thing%", "/refreshkit");
                return;
            }
        }

        KitPvPStats stats = getModule().getKitPvP().getStats(member);
        int coins = stats.getCoins();
        if (coins < COST) {
            NOT_ENOUGH.msg(member.getPlayer(), "%thing%", "coins");
            return;
        }

        UtilPlayer.reset(member.getPlayer());
        stats.getActiveKit().getKit().applyKit(member.getPlayer());
        stats.getActiveKit().getKit().giveSoup(member.getPlayer(), 32);
        stats.setCoins(coins - COST);
        lastRefresh.put(member.getUUID(), now);
        member.message("§7You refreshed your kit for §a" + COST + " coins§7.");
        yaml.set(("current-refreshkit-cooldowns." + member.getUUID()), now);
        save();
    }

    private void configManager() {
        file = new File(plugin.getDataFolder(), "commandcooldowns.yml");

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
            plugin.getLogger().log(Level.SEVERE, "Couldn't save commandcooldowns yaml file.", e);
        }
    }

}
