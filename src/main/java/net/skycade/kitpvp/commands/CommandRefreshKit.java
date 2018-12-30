package net.skycade.kitpvp.commands;

import net.skycade.SkycadeCore.utility.CoreUtil;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.events.KillTheKingEvent;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class CommandRefreshKit extends Command<KitManager> {
    private File file;
    private YamlConfiguration yaml;
    private final static int COST = (KitPvP.getInstance().getConfig().getInt("refreshkit-price"));
    private final static int COOLDOWN = (KitPvP.getInstance().getConfig().getInt("refreshkit-cooldown"));

    private Map<UUID, Long> lastRefresh = new HashMap<>();

    public CommandRefreshKit(KitManager module) {
        super(module, "Refresh your kit for " + COST + " coins", new Permission("kitpvp.default", PermissionDefault.TRUE), "refreshkit");
        configManager();
    }

    @Override
    public void execute(Member member, String aliasUsed, String... args) {
        if (KillTheKingEvent.getInstance() != null && KillTheKingEvent.getInstance().getCurrentKing() != null) {
            if (member.getUUID().equals(KillTheKingEvent.getInstance().getCurrentKing())) {
                member.getPlayer().sendMessage(ChatColor.RED + ("You cannot use /refreshkit as the King!"));
                return;
            }
        }

        long now = System.currentTimeMillis();

        try {
            lastRefresh.put(member.getUUID(), yaml.getLong("current-refreshkit-cooldowns." + member.getUUID()));
        } catch (Exception ignored) {}

        if (lastRefresh.containsKey(member.getUUID())) {
            long diff = (now - lastRefresh.get(member.getUUID())) / 1000L;

            if (diff < COOLDOWN) {
                member.message(ChatColor.RED + "You need to wait another " + CoreUtil.niceFormat(((Long) diff).intValue()) + " before using /refreshkit again!");
                return;
            }
        }

        KitPvPStats stats = getModule().getKitPvP().getStats(member);
        int coins = stats.getCoins();
        if (coins < COST) {
            member.message("§7You don't have enough §acoins§7.");
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
