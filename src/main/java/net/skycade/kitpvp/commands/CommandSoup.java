package net.skycade.kitpvp.commands;

import net.skycade.SkycadeCore.utility.CoreUtil;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.events.KillTheKingEvent;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.skycade.kitpvp.Messages.*;

public class CommandSoup extends Command<KitManager> {

    private final static int COST = (KitPvP.getInstance().getConfig().getInt("soup-price"));
    private final static int COOLDOWN = (KitPvP.getInstance().getConfig().getInt("soup-cooldown"));

    private Map<UUID, Long> lastSoup = new HashMap<>();

    public CommandSoup(KitManager module) {
        super(module, "Buy soup for " + COST + " coins.", new Permission("kitpvp.default", PermissionDefault.TRUE), "soup");
    }

    @Override
    public void execute(Member member, String aliasUsed, String... args) {
        if (KillTheKingEvent.getInstance() != null && KillTheKingEvent.getInstance().getCurrentKing() != null) {
            if (member.getUUID().equals(KillTheKingEvent.getInstance().getCurrentKing())) {
                CANNOT_USE.msg(member.getPlayer(), "%thing%", "/soup", "%reason%", "as the King");
                return;
            }
        }

        long now = System.currentTimeMillis();
        if (lastSoup.containsKey(member.getUUID())) {
            long diff = (now - lastSoup.get(member.getUUID())) / 1000L;
            if (diff < COOLDOWN) {
                ON_COOLDOWN.msg(member.getPlayer(), "%time%", CoreUtil.niceFormat(COOLDOWN - ((Long) diff).intValue()), "%thing%", "/soup");
                return;
            }
        }

        KitPvPStats stats = getModule().getKitPvP().getStats(member);
        int coins = stats.getCoins();
        if (coins - COST < 0) {
            NOT_ENOUGH.msg(member.getPlayer(), "%thing%", "coins");
            return;
        }

        stats.getActiveKit().getKit().giveSoup(member.getPlayer(), 30);
        stats.setCoins(coins - COST);
        lastSoup.put(member.getUUID(), now);
        YOU_PURCHASED.msg(member.getPlayer(), "%thing%", "soup", "%amount%", Integer.toString(COST), "%currency%", "coins");
    }

    private boolean hasSpace(Player p) {
        for (ItemStack item : p.getInventory())
            if (item == null || item.getType() == Material.AIR)
                return true;
        return false;
    }
}
