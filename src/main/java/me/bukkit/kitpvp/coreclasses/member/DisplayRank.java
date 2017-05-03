package me.bukkit.kitpvp.coreclasses.member;

import org.bukkit.ChatColor;

public enum DisplayRank {
    NONE("None", ChatColor.GRAY),

    // KitPvP
    ESQUIRE("Esquire", ChatColor.DARK_GREEN),
    KNIGHT("Knight", ChatColor.GREEN),
    KIT_WARRIOR("Warrior", ChatColor.DARK_AQUA),
    APPRENTICE("Apprentice", ChatColor.BLUE),
    SAMURAI("Samurai", ChatColor.AQUA),
    GLADIATOR("Gladiator", ChatColor.LIGHT_PURPLE),

	// Staff
	JR_MOD("Jr Mod", ChatColor.YELLOW),
	MAP_ADMIN("Map Admin", ChatColor.RED),
	MOD("Mod", ChatColor.GOLD),
	SR_MOD("Sr Mod", ChatColor.GOLD),
	ADMIN("Admin", ChatColor.RED),
	STAFF_MANAGER("SM", ChatColor.RED),
	JR_DEV("Jr Dev", ChatColor.RED),
	DEV("Dev", ChatColor.RED),
	OWNER("Owner", ChatColor.DARK_RED);

	public static void main(String[] args) {
		for (DisplayRank displayRank : DisplayRank.values())
			System.out.println(displayRank + " - " + (100 - displayRank.ordinal()));
	}

	private final String displayName;
	private final ChatColor chatColour;

	DisplayRank(String displayName, ChatColor chatColour) {
		this.displayName = displayName;
		this.chatColour = chatColour;
	}

	public String getDisplayName() {
		return displayName;
	}

	public ChatColor getChatColour() {
		return chatColour;
	}

	public String getColour() {
		return chatColour.toString();
	}

	public int getId() {
		return ordinal();
	}

	public static DisplayRank fromId(int id) {
		if (id > values().length - 1)
			return null;
		return values()[id];
	}

	public String getPrefix() {
		return getPrefix(false);
	}

	public String getPrefix(boolean includeDefaultTag) {
		return getChatColour() + (getId() == 0 && !includeDefaultTag ? "" : getDisplayName().toUpperCase() + ChatColor.RESET.toString() + getChatColour() + " ");
	}

	public Permission getPermission() {
		for (Permission permission : Permission.values())
			if (permission.getDisplayRank() == this)
				return permission;
		return null;
	}

	public boolean isDefault() {
		return getId() == 0;
	}

	public boolean isHighest() {
		return getId() == values().length - 1;
	}

	public String getSlackPrefix() {
		return ordinal() == 0 ? "" : "*" + displayName.toUpperCase() + "* ";
	}

	public static boolean exists(String name) {
		return fromString(name) != null;
	}

	public static DisplayRank fromString(String name) {
		if (name == null)
			return null;
		for (DisplayRank rank : values())
			if (rank.name().equalsIgnoreCase(name) || rank.getDisplayName().equalsIgnoreCase(name))
				return rank;
		return null;
	}

}