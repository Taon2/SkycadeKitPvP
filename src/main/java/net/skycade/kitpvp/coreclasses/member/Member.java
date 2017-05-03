package net.skycade.kitpvp.coreclasses.member;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class Member {

    private Document document;
    private Map<String, Object> changes = new HashMap<>();
    private boolean logChanges = true;

    public Member(Document document) {
        this.document = document;
    }

    public Member(UUID uuid, String name) {
        this.document = new Document();
        put("uuid", uuid.toString());
        put("name", name);
        put("previous_names", Collections.singletonList(name));
        put("permissions", new ArrayList<>());
        put("kitpvp", new Document());
        MemberManager.getInstance().getCollection().insertOne(document);
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Map<String, Object> getChanges() {
        return changes;
    }

    public void setLogChanges(boolean logChanges) {
        this.logChanges = logChanges;
    }

    public UUID getUUID() {
        return UUID.fromString(getString("uuid"));
    }

    public String getName() {
        return getString("name");
    }

    public List<String> getPreviousNames() {
        return (List<String>) document.get("previous_names");
    }

    public void setName(String name) {
        if (getPreviousNames().contains(name))
            return;
        List<String> previousNames = getPreviousNames();
        previousNames.add(name);
        put("name", name);
        put("previous_names", previousNames);
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
        for (String s : (List<String>) document.get("permissions")) {
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
        List<String> permissions = (List<String>) document.get("permissions");
        permissions.add(perm.toString());
        put("permissions", permissions);
    }

    public void update() {
        MemberManager.getInstance().update(this);
    }

    public void put(String key, Object value) {
        put(key, value, false);
    }

    public void put(String key, Object value, boolean update) {
        document.put(key, value);
        if (logChanges)
            changes.put(key, value);
        if (update)
            update();
    }

    public Object get(String key) {
        return document.get(key);
    }

    public String getString(String key) {
        return document.getString(key);
    }

    public int getInt(String key) {
        Object object = document.get(key);
        if (object != null && object instanceof Integer)
            return (Integer) object;
        return -1;
    }

    public long getLong(String key) {
        Object object = document.get(key);
        if (object != null && object instanceof Long)
            return (Long) object;
        return -1;
    }

    public boolean getBoolean(String key) {
        return document.containsKey(key) && document.getBoolean(key, false);
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

}
