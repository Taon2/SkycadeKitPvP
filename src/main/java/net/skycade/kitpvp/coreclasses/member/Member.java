package net.skycade.kitpvp.coreclasses.member;

import net.skycade.kitpvp.KitPvP;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class Member {

    private Map<String, Object> changes = new HashMap<>();

    private UUID uuid;
    private String name;
    private List<String> previousNames = new ArrayList<>();

    private Map<String, Object> properties = new HashMap<>();

    public Member(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        previousNames.add(name);
    }

    public Map<String, Object> getChanges() {
        return changes;
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

    public boolean isOnline() {
        return getPlayer() != null;
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

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public void setPreviousNames(List<String> previousNames) {
        this.previousNames = previousNames;
    }

    public void setKills(Integer kills) {
        KitPvP.getInstance().getStats(this).setKills(kills);
    }

    public void setHighestStreak(Integer highestStreak) {
        KitPvP.getInstance().getStats(this).setHighestStreak(highestStreak);
    }

    public void setDeaths(Integer deaths) {
        KitPvP.getInstance().getStats(this).setDeaths(deaths);
    }

    public void putProperty(String key, Object value) {
        properties.put(key, value);
    }
}
