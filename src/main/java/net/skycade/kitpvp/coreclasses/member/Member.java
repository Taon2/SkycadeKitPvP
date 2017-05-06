package net.skycade.kitpvp.coreclasses.member;

import net.skycade.kitpvp.stat.KitPvPDB;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class Member {

    private Map<String, Object> changes = new HashMap<>();

    private UUID uuid;
    private String name;
    private List<String> previousNames = new ArrayList<>();

    private List<String> permissions = new ArrayList<>();

    private Integer kills = 0;
    private Integer highestStreak = 0;

    private Map<String, Object> properties = new HashMap<>();

    private Integer deaths = 0;

    public Member(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        previousNames.add(name);
        KitPvPDB.getInstance().setMemberData(uuid, name, previousNames, permissions, kills, highestStreak, deaths, properties);
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

    public boolean hasPermission(Permission permission) {
        return getPermissions(true).contains(permission);
    }

    public List<Permission> getPermissions(boolean inheritance) {
        Set<Permission> permissions = new HashSet<>();
        for (String s : this.permissions) {
            Permission permission = Permission.fromString(s);
            if (permission == null)
                continue;
            permissions.add(permission);
            if (inheritance)
                permissions.addAll(Arrays.asList(permission.getInheritance()));
        }
        return new ArrayList<>(permissions);
    }

    public void addPermission(Permission perm) {
        if (hasPermission(perm))
            return;
        permissions.add(perm.toString());
    }

    public void update() {
        MemberManager.getInstance().update(this);
    }

    public DisplayRank getDisplayRank() {
        List<Permission> permissions = getPermissions(false);
        DisplayRank displayRank = null;
        if (permissions.contains(Permission.RANK_SIX))
            displayRank = Permission.RANK_SIX.getDisplayRank();
        else if (permissions.contains(Permission.RANK_FIVE))
            displayRank = Permission.RANK_FIVE.getDisplayRank();
        else if (permissions.contains(Permission.RANK_FOUR))
            displayRank = Permission.RANK_FOUR.getDisplayRank();
        else if (permissions.contains(Permission.RANK_THREE))
            displayRank = Permission.RANK_THREE.getDisplayRank();
        else if (permissions.contains(Permission.RANK_TWO))
            displayRank = Permission.RANK_TWO.getDisplayRank();
        else if (permissions.contains(Permission.RANK_ONE))
            displayRank = Permission.RANK_ONE.getDisplayRank();
        if (displayRank != null)
            return displayRank;
        return DisplayRank.NONE;
    }

    public boolean isStaff() {
        return getPermissions(true).contains(Permission.staff());
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
        return kills;
    }

    public Integer getHighestStreak() {
        return highestStreak;
    }

    public Integer getDeaths() {
        return deaths;
    }

    public List<String> getRawPermissions() {
        return permissions;
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

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public void setKills(Integer kills) {
        this.kills = kills;
    }

    public void setHighestStreak(Integer highestStreak) {
        this.highestStreak = highestStreak;
    }

    public void setDeaths(Integer deaths) {
        this.deaths = deaths;
    }
}
