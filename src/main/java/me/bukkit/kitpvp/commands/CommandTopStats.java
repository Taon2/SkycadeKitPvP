package me.bukkit.kitpvp.commands;

import me.bukkit.kitpvp.Settings;
import me.bukkit.kitpvp.coreclasses.commands.Command;
import me.bukkit.kitpvp.coreclasses.datastructures.binarytree.BTNode;
import me.bukkit.kitpvp.coreclasses.datastructures.binarytree.BinaryTree;
import me.bukkit.kitpvp.coreclasses.member.Member;
import me.bukkit.kitpvp.coreclasses.member.Permission;
import me.bukkit.kitpvp.coreclasses.utils.UtilMath;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.managers.PageManager;
import me.bukkit.kitpvp.stat.KitPvPDB;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Iterator;
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
		super(module, "View the top 10 kills/killstreak/deaths.", Permission.NONE, "topstats", "statstop");
		setUsage("<kills/deaths/killstreak/kdr>");
		startStatRefresh();
	}

    private void startStatRefresh() {
	    updating = true;
        Bukkit.getScheduler().runTaskAsynchronously(getModule().getKitPvP(), () -> {
            int size = (int) KitPvPDB.getInstance().getMemberCollection().count();
            BTNode<Integer, UUID>[] killsStartArray = new BTNode[size];
            BTNode<Integer, UUID>[] deathsStartArray = new BTNode[size];
            BTNode<Integer, UUID>[] streakStartArray = new BTNode[size];
            BTNode<Double, UUID>[] kdStartArray = new BTNode[size];

            Iterator<Document> iterator = KitPvPDB.getInstance().getMemberCollection().find().iterator();

            for (int i = 0; i < size; i++) {
                if (!iterator.hasNext())
                    break;
                Document memberDoc = iterator.next();
                UUID uuid = UUID.fromString(memberDoc.getString("uuid"));

                Document doc = (Document) memberDoc.get("kitpvp");

                Integer kills = doc.getInteger("kills");
                Integer streak = doc.getInteger("highest_streak");
                Integer deaths = doc.getInteger("deaths");
                Double kd = getKd(kills, deaths);

                killsStartArray[i] = kills == null ? new BTNode<>(0, uuid) : new BTNode<>(kills, uuid);
                deathsStartArray[i] = streak == null ? new BTNode<>(0, uuid) : new BTNode<>(streak, uuid);
                streakStartArray[i] = deaths == null ? new BTNode<>(0, uuid) : new BTNode<>(deaths, uuid);
                kdStartArray[i] = kd == null ? new BTNode<>(0.0, uuid) : new BTNode<>(kd, uuid);
            }
            killTree = new BinaryTree<>(false,  killsStartArray);
            killsPageManager.setPageElements(getPageElements(killTree.getInOrderArray()));
            deathTree = new BinaryTree<>(false,  deathsStartArray);
            deathsPageManager.setPageElements(getPageElements(deathTree.getInOrderArray()));
            streakTree = new BinaryTree<>(false,  streakStartArray);
            streakPageManager.setPageElements(getPageElements(streakTree.getInOrderArray()));
            kdrTree = new BinaryTree<>(false,  kdStartArray);
            kdrPageManager.setPageElements(getKdrPageElements(kdrTree.getInOrderArray()));
            updating = false;
        });
	    Bukkit.getScheduler().runTaskLater(getModule().getKitPvP(), this::startStatRefresh, 20 * Settings.STAT_REFRESH_TIME);
    }

    private Double getKd(Integer kills, Integer deaths) {
	    if (kills == null || deaths == null)
	        return null;
        double kd = UtilMath.getKDR(kills, deaths);
        return (Math.round(kd * 10.0) / 10.0);
    }

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
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
