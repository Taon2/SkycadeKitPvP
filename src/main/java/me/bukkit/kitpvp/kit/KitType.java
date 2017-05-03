package me.bukkit.kitpvp.kit;

import me.bukkit.kitpvp.KitPvP;
import org.bukkit.inventory.ItemStack;

public enum KitType {

	ARCHER,	
	ASSASSIN,
	BARBARIAN,
	BLADEMASTER,
	BOMBER,
	CAVEMAN,
	CERBERUS,
	CHANCE,
	CHRONOS,
	COBRA,
	DEFAULT,
	DUALBLADER,
	DUBSTEP,
	ELITE,
	FIREARCHER,
	FIREMAGE,
	FISHERMAN,
	FROSTY,
	GANK,
	GHOST,
	GOLEM,
	ENDERMAN,
	HADES,
	HUNTSMAN,
	HYDRA,
	HYPER,
	JESUS,
	JUMPER,
	KANGAROO,
	KING,
	KITMASTER,
	KNIGHT,
	LOVER,
	MEDIC,
	MYSTIC,
	NINJA,
	PLUSH,
	POTIONMASTER,
	PRICK,
	SHACO,
	SHARINGAN, 
	SNIPER,
	SONIC,
	SOULMASTER, 
	STRAFE, 
	SUPPORT,
	TANK,
	TELEPORTER,
	TRIBESMAN, 
	UNICORN,
	VAMPIRE,
	WARRIOR,
	WITHER,
	WIZARD, 
	WOLFPACK,
	ZEUS;

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

}