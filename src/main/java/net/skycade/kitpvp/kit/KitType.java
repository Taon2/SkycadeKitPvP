package net.skycade.kitpvp.kit;

import net.skycade.kitpvp.KitPvP;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public enum KitType {
    ARCHER("archer"),
    ASSASSIN("assassin"),
    AVATAR("avatar"),
    BARBARIAN("barbarian"),
    BLACKSMITH("blacksmith"),
    BLOCKHUNT("blockhunt"),
    BUILDUHC("builduhc"),
    CAVEMAN("caveman"),
    CERBERUS("cerberus"),
    CHANCE("chance"),
    COBRA("cobra"),
    DUALBLADER("dualblader"),
    DUBSTEP("dubstep"),
    ELITE("elite"),
    FIREARCHER("firearcher"),
    FISHERMAN("fisherman"),
    FROSTY("frosty"),
    GANK("gank"),
    GUARDIAN("guardian"),
    ENDERMAN("enderman"),
    HADES("hades"),
    HYDRA("hydra"),
    HYPER("hyper"),
    JUMPER("jumper"),
    KANGAROO("kangaroo"),
    KING("king"),
    KNIGHT("knight"),
    LICH("lich"),
    LOVER("lover"),
    MYSTIC("mystic"),
    NECROMANCER("necromancer"),
    PAINTBALL("paintball"),
    PALADIN("paladin"),
    PLUSH("plush"),
    POTIONMASTER("potionmaster"),
    PRICK("prick"),
    REAPER("reaper"),
    SHACO("shaco"),
    SHARINGAN("sharingan"),
    SNIPER("sniper"),
    SONIC("sonic"),
    SORCERER("sorcerer"),
    SOULMASTER("soulmaster"),
    TANK("tank"),
    TELEPORTER("teleporter"),
    WITCHDOCTOR("witchdoctor"),
    WITHER("wither"),
    WIZARD("wizard"),
    WOLFPACK("wolfpack"),
    ZEUS("zeus"),
    KITMASTER("kitmaster"),

    //disabled
    BLADEMASTER("blademaster"),
    CHRONOS("chronos"),
    DEFAULT("default"),
    FIREMAGE("firemage"),
    GAMBLER("gambler"),
    GHOST("ghost"),
    GOLEM("golem"),
    HUNTSMAN("huntsman"),
    JESUS("jesus"),
    MEDIC("medic"),
    MULTISHOT("multishot"),
    NINJA("ninja"),
    PYROMANCER("pyromancer"),
    SHROOM("shroom"),
    STRAFE("strafe"),
    SUPPORT("support"),
    TREEENT("treeent"),
    TRIBESMAN("tribesman"),
    UNICORN("unicorn"),
    VAMPIRE("vampire"),
    WARRIOR("warrior"),
    HULK("hulk"),

    BOMBER("bomber");

    private final String alias;

    KitType(String alias) {
        this.alias = alias;
    }

    private static Map<String, KitType> aliases = new HashMap<>();

    static {
        for (KitType kitType : values()) {
            aliases.put(kitType.getAlias(), kitType);
        }
    }

    private Kit kit;

    public Kit getKit() {
        if (kit == null)
            kit = KitPvP.getInstance().getKitManager().getKits().get(this);
        return kit;
    }

    public static int getSize() {
        int amount = 0;
        for (KitType type : values())
            if (type.getKit().isEnabled())
                amount++;
        return amount;
    }

    public static KitType getTypeFromString(String kit) {
        for (KitType type : values())
            if (type.toString().equalsIgnoreCase(kit))
                return type;
        return null;
    }

    // Used for shop and kits
    public static KitType getClickedKit(ItemStack item) {
        if (item == null || item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null)
            return null;
        String name = item.getItemMeta().getDisplayName();
        return KitType.getTypeFromString(name.substring(2));
    }

    public static KitType byAlias(String alias) {
        return aliases.getOrDefault(alias, null);
    }

    public String getAlias() {
        return alias;
    }
}