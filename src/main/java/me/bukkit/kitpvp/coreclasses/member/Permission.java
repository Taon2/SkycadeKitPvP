package me.bukkit.kitpvp.coreclasses.member;

import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum Permission {

    NONE(DisplayRank.NONE),

    RANK_ONE,
    RANK_TWO(RANK_ONE),
    RANK_THREE(RANK_TWO),
    RANK_FOUR(RANK_THREE),
    RANK_FIVE(RANK_FOUR),
    RANK_SIX(RANK_FIVE),

    // Staff
    JR_MOD(DisplayRank.JR_MOD),
    MOD(DisplayRank.MOD, JR_MOD),
    SR_MOD(DisplayRank.SR_MOD, MOD),
    ADMIN(DisplayRank.ADMIN, SR_MOD),
    STAFF_MANAGER(DisplayRank.STAFF_MANAGER, ADMIN),
    JR_DEV(DisplayRank.JR_DEV, ADMIN),
    DEV(DisplayRank.DEV, ADMIN, STAFF_MANAGER),
    OWNER(DisplayRank.OWNER, ADMIN);

    private final ChatColor chatColour;
    private final Permission[] inheritance;
    private final Set<String> permissions = new HashSet<>();
    private DisplayRank displayRank;

    Permission(Permission... inheritance) {
        this(ChatColor.GOLD, inheritance);
    }

    Permission(DisplayRank displayRank, Permission... inheritance) {
        this(displayRank.getChatColour(), inheritance);
        this.displayRank = displayRank;
    }

    Permission(ChatColor chatColour, Permission... inheritance) {
        this.chatColour = chatColour;
        this.inheritance = inheritance;
    }

    public Permission[] getInheritance() {
        Set<Permission> inheritance = new HashSet<>();
        inheritance.add(this);
        for (Permission permission : this.inheritance)
            inheritance.addAll(Arrays.asList(permission.getInheritance()));
        return inheritance.toArray(new Permission[inheritance.size()]);
    }

    public Set<String> getPermissions(){
        return permissions;
    }

    public ChatColor getChatColour() {
        return chatColour;
    }

    public String getColour() {
        return chatColour.toString();
    }

    public String toDisplayString() {
        return toDisplayString(true);
    }

    public String toDisplayString(boolean bold) {
        if (getDisplayRank() != null)
            return getDisplayRank().getColour() + (bold ? "§l" : "") + getDisplayRank().getDisplayName().toUpperCase();
        return getChatColour() + (bold ? "§l" : "") + name().toUpperCase().replaceAll("_", " ");
    }

    public DisplayRank getDisplayRank() {
        return displayRank;
    }

    public static Permission staff() {
        return JR_MOD;
    }

    public void setDisplayRank(DisplayRank displayRank) {
        this.displayRank = displayRank;
    }

    public static boolean exists(String name) {
        return fromString(name) != null;
    }

    public int getId() {
        return ordinal();
    }

    public static Permission fromString(String name) {
        for (Permission permission : values())
            if (permission.toString().equalsIgnoreCase(name))
                return permission;
        return null;
    }

}
