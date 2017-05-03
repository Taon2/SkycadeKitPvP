package net.skycade.kitpvp.managers;

import net.skycade.kitpvp.coreclasses.utils.UtilPacket;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageManager {

    private final String title;
    private final String command;
    private List<BaseComponent[]> pageElements;
    private final int amountPerPage;
    private int amountOfPages;
    private final int sideLength;

    //Has line for page number.
    private Map<Integer, List<BaseComponent[]>> pageMap;

    public PageManager(String title, String command, List<BaseComponent[]> pageElements, int amountPerPage, int sideLength) {
        this.title = title;
        this.command = command;
        this.pageElements = pageElements;
        this.amountPerPage = amountPerPage;
        this.sideLength = sideLength;
        fillPageMap();
    }

    public void sendToPlayer(Player p, int currentPage) {
        if (currentPage > pageMap.size()) {
            currentPage = pageMap.size();
        } else if (currentPage <= 0) {
            currentPage = 1;
        }
        p.spigot().sendMessage(createTitle(p, currentPage));
        for (BaseComponent[] line : pageMap.get(currentPage))
            p.spigot().sendMessage(line);
    }

    private void fillPageMap() {
        pageMap = new HashMap<>();

        int currentPage = 0;
        int currentLine = 0;
        List<BaseComponent[]> lines = new ArrayList<>();

        for (BaseComponent[] line : pageElements) {
            lines.add(line);
            currentLine++;
            if (currentLine % amountPerPage == 0) {
                currentPage++;
                pageMap.put(currentPage, lines);
                lines = new ArrayList<>();
            }
        }

        if (!pageMap.containsKey(1))
            pageMap.put(1, lines);

        amountOfPages = pageMap.size();
    }

    public BaseComponent[] createTitle(Player p, int currentPage) {
        String bar = "";
        for (int i = 0; i < sideLength; i++)
            bar += "_";

        return UtilPacket.concatinateChatComponents(
                TextComponent.fromLegacyText("§7" + bar + "[§a " + title + " "),
                getLeftArrow(p, "§b", currentPage),
                TextComponent.fromLegacyText("§a" + currentPage + "/" + amountOfPages),
                getRightArrow(p, "§b", currentPage, amountOfPages),
                TextComponent.fromLegacyText("§7]" + bar)
        );
    }

    private BaseComponent[] getLeftArrow(Player p, String leftColor, int currentPage) {
        if (currentPage <= 1)
            leftColor = "§7";
        return UtilPacket.createChatComponent(p,
                "§7[" + leftColor + "<§7]",
                "Left page",
                currentPage - 1 > 0 ? (x) -> x.chat(command +  (currentPage - 1)) : null
        );
    }

    private BaseComponent[] getRightArrow(Player p, String rightColor, int currentPage, int amountOfPages) {
        if (currentPage == amountOfPages)
            rightColor = "§7";
        return UtilPacket.createChatComponent(p,
                "§7[" + rightColor + ">§7]",
                "Right page",
                currentPage < amountOfPages ? (x) -> x.chat(command +  (currentPage + 1)) : null
        );
    }

    public void setPageElements(List<BaseComponent[]> pageElements) {
        this.pageElements = pageElements;
        fillPageMap();
    }

    public int getPageElementsSize() {
        return pageElements.size();
    }

}
