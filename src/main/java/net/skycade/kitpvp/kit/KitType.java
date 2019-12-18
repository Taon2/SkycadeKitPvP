package net.skycade.kitpvp.kit;

import net.skycade.kitpvp.KitPvP;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public enum KitType {

    ARCHER("archer"),
    ASSASSIN("assassin"),
    BARBARIAN("barbarian"),
    BLADEMASTER("blademaster"),
    BOMBER("bomber"),
    CAVEMAN("caveman"),
    CERBERUS("cerberus"),
    CHANCE("chance"),
    CHRONOS("chronos"),
    COBRA("cobra"),
    DEFAULT("default"),
    DUALBLADER("dualblader"),
    DUBSTEP("dubstep"),
    ELITE("elite"),
    FIREARCHER("firearcher"),
    FIREMAGE("firemage"),
    FISHERMAN("fisherman"),
    FROSTY("frosty"),
    GANK("gank"),
    GHOST("ghost"),
    GOLEM("golem"),
    ENDERMAN("enderman"),
    HADES("hades"),
    HUNTSMAN("huntsman"),
    HYDRA("hydra"),
    HYPER("hyper"),
    JESUS("jesus"),
    JUMPER("jumper"),
    KANGAROO("kangaroo"),
    KING("king"),
    KITMASTER("kitmaster"),
    KNIGHT("knight"),
    LOVER("lover"),
    MEDIC("medic"),
    MYSTIC("mystic"),
    NINJA("ninja"),
    PLUSH("plush"),
    POTIONMASTER("potionmaster"),
    PRICK("prick"),
    SHACO("shaco"),
    SHARINGAN("sharingan"),
    SNIPER("sniper"),
    SONIC("sonic"),
    SOULMASTER("soulmaster"),
    STRAFE("strafe"),
    SUPPORT("support"),
    TANK("tank"),
    TELEPORTER("teleporter"),
    TRIBESMAN("tribesman"),
    UNICORN("unicorn"),
    VAMPIRE("vampire"),
    WARRIOR("warrior"),
    WITHER("wither"),
    WIZARD("wizard"),
    WOLFPACK("wolfpack"),
    ZEUS("zeus");

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
        return KitType.getTypeFromString(name.substring(2, name.length()));
    }

    public static KitType byAlias(String alias) {
        return aliases.getOrDefault(alias, null);
    }

    public String getAlias() {
        return alias;
    }
}