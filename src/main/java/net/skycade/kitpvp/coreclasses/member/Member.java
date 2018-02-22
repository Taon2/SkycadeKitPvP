package net.skycade.kitpvp.coreclasses.member;

import net.skycade.kitpvp.KitPvP;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class Member {

    private UUID uuid;
    private String name;
    private List<String> previousNames = new ArrayList<>();

    public Member(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        previousNames.add(name);
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public List<String> getPreviousNames() {
        return previousNames;
    }

    public void setName(String name) {
        if (getPreviousNames().contains(name))
            return;
        this.name = name;
        previousNames.add(name);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getUUID());
    }

    public void message(String message) {
        Player p = getPlayer();
        if (p != null) {
            p.sendMessage("§7" + message);
        }
    }

    public void update() {
        MemberManager.getInstance().update(this);
    }

    @Override
    public int hashCode() {
        return getUUID().hashCode();
    }
    @Override
    public String toString() {
        return getUUID().toString();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Member && ((Member) object).getUUID().equals(getUUID());
    }

    public boolean isConsole() {
        return this instanceof ConsoleCommandSender;
    }

    public Integer getKills() {
        return KitPvP.getInstance().getStats(this).getKills();
    }

    public Integer getHighestStreak() {
        return KitPvP.getInstance().getStats(this).getHighestStreak();
    }

    public Integer getDeaths() {
        return KitPvP.getInstance().getStats(this).getDeaths();
    }

    public void addPreviousName(String name) {
        previousNames.add(name);
    }
}
