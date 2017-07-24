package net.skycade.kitpvp.commands;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.datastructures.binarytree.BTNode;
import net.skycade.kitpvp.coreclasses.datastructures.binarytree.BinaryTree;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.managers.PageManager;
import net.skycade.kitpvp.stat.KitPvPDB;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandTopStats extends Command<KitManager> {

    private BinaryTree<Integer, UUID> killTree;
    private BinaryTree<Integer, UUID> deathTree;
    private BinaryTree<Integer, UUID> streakTree;
    private BinaryTree<Double, UUID> kdrTree;
    private PageManager killsPageManager = new PageManager("Top 10 kills ", "/topstats kills ", new ArrayList<>(), 9, 6);
    private PageManager deathsPageManager = new PageManager("Top 10 deaths ", "/topstats deaths ", new ArrayList<>(), 9, 6);
    private PageManager streakPageManager = new PageManager("Top 10 killstreak ", "/topstats ks ", new ArrayList<>(), 9, 6);;
    private PageManager kdrPageManager = new PageManager("Top 10 kd ", "/topstats kd ", new ArrayList<>(), 9, 6);
    private boolean updating = true;

    public CommandTopStats(KitManager module) {
        super(module, "View the top 10 kills/killstreak/deaths.", new Permission("kitpvp.default", PermissionDefault.TRUE), "topstats", "statstop");
        setUsage("<kills/deaths/killstreak/kdr>");
        /* startStatRefresh(); */
    }

    @SuppressWarnings("unchecked")
    private void startStatRefresh() {
        updating = true;
        Bukkit.getScheduler().runTaskAsynchronously(getModule().getKitPvP(), () -> {
            List<BTNode<Integer, UUID>> killsStartList = new ArrayList<>();
            List<BTNode<Integer, UUID>> deathsStartList = new ArrayList<>();
            List<BTNode<Integer, UUID>> streakStartList = new ArrayList<>();
            List<BTNode<Double, UUID>> kdStartList = new ArrayList<>();

            ResultSet members = KitPvPDB.getInstance().getAllMembers();
            try {
                while (members.next()) {
                    UUID uuid = UUID.fromString(members.getString("UUID"));
                    Integer kills = members.getInt("Kills");
                    Integer streak = members.getInt("HighestStreak");
                    Integer deaths = members.getInt("Deaths");
                    Double kd = getKd(kills, deaths);

                    killsStartList.add(new BTNode<>(kills, uuid));
                    deathsStartList.add(new BTNode<>(streak, uuid));
                    streakStartList.add(new BTNode<>(deaths, uuid));
                    kdStartList.add(kd == null ? new BTNode<>(0.0, uuid) : new BTNode<>(kd, uuid));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            killTree = new BinaryTree<>(false,  killsStartList.toArray(new BTNode[killsStartList.size()]));
            killsPageManager.setPageElements(getPageElements(killTree.getInOrderArray()));
            deathTree = new BinaryTree<>(false,  deathsStartList.toArray(new BTNode[deathsStartList.size()]));
            deathsPageManager.setPageElements(getPageElements(deathTree.getInOrderArray()));
            streakTree = new BinaryTree<>(false, streakStartList.toArray(new BTNode[streakStartList.size()]));
            streakPageManager.setPageElements(getPageElements(streakTree.getInOrderArray()));
            kdrTree = new BinaryTree<>(false,  kdStartList.toArray(new BTNode[kdStartList.size()]));
            kdrPageManager.setPageElements(getKdrPageElements(kdrTree.getInOrderArray()));
            updating = false;
        });
        Bukkit.getScheduler().runTaskLaterAsynchronously(getModule().getKitPvP(), this::startStatRefresh, 20 * KitPvP.getInstance().getConfig().getInt("stat-refresh-time"));
    }

    private Double getKd(Integer kills, Integer deaths) {
        if (kills == null || deaths == null)
            return null;
        double kd = UtilMath.getKDR(kills, deaths);
        return (Math.round(kd * 10.0) / 10.0);
    }

    @Override
    public void execute(Member member, String aliasUsed, String... args) {
        member.message("§cStats are currently disabled, they will return at a later time.");
        /*
        if (!checkArgs(member, aliasUsed, args))
            return;
        if (updating) {
            member.message("TopStats are §aupdating§7.");
            return;
        }
        if (killsPageManager.getPageElementsSize() < 1 || deathsPageManager.getPageElementsSize() < 1 || streakPageManager .getPageElementsSize() < 1 || kdrPageManager .getPageElementsSize() < 1) {
            member.message("Something went wrong");
            return;
        }
        int page = 1;
        if (args.length > 1) {
            if (!parseInt(member, args[1]))
                return;
            page = Integer.parseInt(args[1]);
        }

        if (args[0].equalsIgnoreCase("kills") || args[0].equalsIgnoreCase("kill")) {
            killsPageManager.sendToPlayer(member.getPlayer(), page);
        } else if (args[0].equalsIgnoreCase("killstreak") || args[0].equalsIgnoreCase("ks"))  {
            streakPageManager.sendToPlayer(member.getPlayer(), page);
        } else if (args[0].equalsIgnoreCase("deaths") || args[0].equalsIgnoreCase("death")) {
            deathsPageManager.sendToPlayer(member.getPlayer(), page);
        } else if (args[0].equalsIgnoreCase("kd") || args[0].equalsIgnoreCase("kdr")) {
            kdrPageManager.sendToPlayer(member.getPlayer(), page);
        } else
            couldNotFind(member, "stats", args[0]);
            */
    }

    private List<BaseComponent[]> getPageElements(BTNode<Integer, UUID>[] array) {
        List<BaseComponent[]> elements = new ArrayList<>();
        int counter = 1;
        int loopSize = array.length < 10 ? array.length : 10;
        for (int i = 0; i < loopSize; i++) {
            BTNode<Integer, UUID> current = array[i];
            String name = Bukkit.getPlayer(current.getValue()) != null ? Bukkit.getPlayer(current.getValue()).getName() : Bukkit.getOfflinePlayer(current.getValue()).getName();
            elements.add(TextComponent.fromLegacyText("§a" + counter++ + "§7. " + name + " - " + current.getKey()));
        }
        return elements;
    }

    private List<BaseComponent[]> getKdrPageElements(BTNode<Double, UUID>[] kdrArray) {
        List<BaseComponent[]> elements = new ArrayList<>();
        int counter = 1;
        int loopSize = kdrArray.length < 10 ? kdrArray.length : 10;
        for (int i = 0; i < loopSize; i++) {
            BTNode<Double, UUID> current = kdrArray[i];
            String name = Bukkit.getPlayer(current.getValue()) != null ? Bukkit.getPlayer(current.getValue()).getName() : Bukkit.getOfflinePlayer(current.getValue()).getName();
            elements.add(TextComponent.fromLegacyText("§a" + counter++ + "§7. " + name + " - " + current.getKey()));
        }
        return elements;
    }

}
