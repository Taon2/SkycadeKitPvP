package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.skycade.kitpvp.Messages.YOURE_FROZEN;

public class KitDualBlader extends Kit {

    public KitDualBlader(KitManager kitManager) {
        super(kitManager, "DualBlader", KitType.DUALBLADER, 34000, "Use the sword of fire and ice");

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "PACKED_ICE");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 34000);

        defaultsMap.put("inventory.sword.material", "IRON_SWORD");
        defaultsMap.put("inventory.sword.enchantments.durability", 5);
        defaultsMap.put("inventory.sword.name", "§cSword of fire");

        defaultsMap.put("inventory.sword2.material", "IRON_SWORD");
        defaultsMap.put("inventory.sword2.enchantments.durability", 5);
        defaultsMap.put("inventory.sword2.name", "§bSword of ice");

        defaultsMap.put("armor.material", "IRON");
        defaultsMap.put("armor.enchantments.durability", 1);
        defaultsMap.put("armor.enchantments.protection", 0);

        setConfigDefaults(defaultsMap);

        if (getConfig().getString("kit.icon.material") != null) {
            if (getConfig().getString("kit.icon.material").contains("LEATHER")) {
                setIcon(new ItemBuilder(Material.getMaterial(getConfig().getString("kit.icon.material").toUpperCase()))
                        .setColour(getColor(getConfig().getString("kit.icon.color"))).build());
            } else {
                setIcon(new ItemStack(Material.getMaterial(getConfig().getString("kit.icon.material").toUpperCase())));
            }
        } else {
            setIcon(new ItemStack(Material.DIRT));
        }
        setPrice(getConfig().getInt("kit.price"));
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(new ItemBuilder(
                Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
                .setName(getConfig().getString("inventory.sword.name")).build());

        p.getInventory().addItem(new ItemBuilder(
                Material.getMaterial(getConfig().getString("inventory.sword2.material").toUpperCase()))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword2.enchantments.durability"))
                .setName(getConfig().getString("inventory.sword2.name")).build());

        p.getInventory().setArmorContents(getArmour(
                Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
                getConfig().getInt("armor.enchantments.durability"),
                getConfig().getInt("armor.enchantments.protection")));
    }

    @Override
    public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
        if (!Arrays.asList(Material.DIAMOND_SWORD, Material.IRON_SWORD).contains(damager.getItemInHand().getType()))
            return;

        if (damager.getItemInHand().getItemMeta().getDisplayName().contains("fire")) {
            fireFreezeCalc(damagee, 25, 7, 0);
        } else if (damager.getItemInHand().getItemMeta().getDisplayName().contains("ice")) {
            fireFreezeCalc(damagee, 0, 0, 10);
        }
    }

    private void fireFreezeCalc(Player damagee, int firechance, int firedur, int freezechance) {
        int random = UtilMath.getRandom(0, 100);

        if (random <= firechance)
            damagee.setFireTicks(firedur * 20);
        else if (random <= firechance + freezechance) {
            YOURE_FROZEN.msg(damagee);
            freezePlayer(damagee, 5);
        }
    }

    @Override
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7Your fire sword has a chance", "§7to light someone on fire", "§7and your ice sword can freeze people.");
    }

}
