package net.skycade.kitpvp.ui.prestige;

import net.skycade.SkycadeCore.CoreSettings;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static net.skycade.kitpvp.Messages.*;

public class PrestigeManager {

    private final KitPvP plugin;
    private final String prestigeRewardsTable;

    private TreeMap<Integer, PrestigeLevel> prestigeLevels = new TreeMap<>();

    public PrestigeManager(KitPvP plugin) {
        this.plugin = plugin;
        prestigeRewardsTable = KitPvP.getInstance().getConfig().getString("database.kitpvp-prestige-levels");

        loadRewards();
    }

    private void loadRewards() {
        try (Connection connection = CoreSettings.getInstance().getConnection()) {
            try (Statement statement = connection.createStatement()) {
                ResultSet results = statement.executeQuery("SELECT * FROM " + prestigeRewardsTable + " ORDER BY level ASC");
                while (results.next()) {
                    int level = results.getInt("level");
                    int cost = results.getInt("cost");
                    String commands = results.getString("commands");

                    List<String> rewardCommands;
                    rewardCommands = new ArrayList<>(Arrays.asList(commands.split("\n")));

                    String desc = results.getString("reward_desc");

                    List<String> rewardDesc;
                    rewardDesc = new ArrayList<>(Arrays.asList(desc.split("\n")));

                    prestigeLevels.put(level, new PrestigeLevel(level, cost, rewardCommands, rewardDesc));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean attemptPrestige(Member member, KitPvPStats stats, int level) {
        int currentLevel = stats.getPrestigeLevel();
        PrestigeLevel nextLevel = getPrestigeLevel(level);

        if (nextLevel == null) {
            MAX_PRESTIGE.msg(member.getPlayer());
            return false;
        }

        if (currentLevel >= nextLevel.getLevel()) {
            ALREADY_UNLOCKED.msg(member.getPlayer(), "%thing%", "prestige rank " + nextLevel.getLevel());
            return false;
        }

        if (level - currentLevel > 1) {
            NOT_THAT_PRESTIGE.msg(member.getPlayer());
            return false;
        }

        if (stats.getCoins() < nextLevel.getCost()) {
            NOT_ENOUGH_CURRENCY.msg(member.getPlayer(), "%currency%", "coins", "%thing%", "prestige rank " + nextLevel.getLevel());
            return false;
        }

        stats.takeCoins(nextLevel.getCost());
        stats.setPrestigeLevel(currentLevel + 1);
        STAT_SET.msg(member.getPlayer(), "%player%", member.getName(), "%stat%", "prestige rank", "%amount%", Integer.toString(stats.getPrestigeLevel()));
        nextLevel.getCommands().forEach(command -> {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", member.getName()));
        });
        return true;
    }

    public TreeMap<Integer, PrestigeLevel> getPrestigeLevels() {
        return prestigeLevels;
    }

    public PrestigeLevel getPrestigeLevel(int toGet) {
        PrestigeLevel level = null;

        for (Map.Entry<Integer, PrestigeLevel> entry : prestigeLevels.entrySet()) {
            PrestigeLevel prestigeLevel = entry.getValue();
            if (prestigeLevel.getLevel() == toGet)
                level = prestigeLevel;
        }

        return level;
    }

    public KitPvP getPlugin() {
        if (plugin == null)
            return KitPvP.getInstance();
        return plugin;
    }
}
