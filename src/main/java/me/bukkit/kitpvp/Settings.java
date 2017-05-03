package me.bukkit.kitpvp;

        import me.bukkit.kitpvp.kit.KitType;

public class Settings {

    // Amount of coins you get on your first join
    public static final int START_COINS = 3500;

    // Kits you have unlocked from the start (other than default)
    public static final KitType[] START_KITS = new KitType[]{KitType.ARCHER, KitType.CHANCE};

    // Amount of keys you get on first join
    public static final int START_KEYS = 1;

    // How long it takes before the kits in show rotate in seconds
    public static final int ROTATION_SECONDS = 3600;

    // The amount of kits in the rotation
    public static final int KITS_ROTATION_AMOUNT = 18;

    // Higher is more xp required to level up your kit
    public static final int REQUIRED_XP_MULTIPLIER = 1;

    // Display hit damage using armor stands
    public static final boolean DISPLAY_HIT_DAMAGE = true;

    // How often the highest ks player on the scoreboard gets updated
    public static final int KS_UPDATE_TIME = 30;

    // Amount of credits you get for a kill
    public static final int KILL_CREDITS = 15;

    // How long it takes for the chest to refresh (in seconds)
    public static final int CHEST_COOLDOWN = 30;

    // How long it takes before you can use a refresh sign again (in seconds)
    public static final int SIGN_REFRESH_COOLDOWN = 120;

    // How long it takes to update TopStats in seconds
    public static final int STAT_REFRESH_TIME = 1800;

}
