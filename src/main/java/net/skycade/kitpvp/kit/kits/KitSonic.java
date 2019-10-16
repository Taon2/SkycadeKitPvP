package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitSonic extends Kit {

    private final HashMap<UUID, Integer> sprinting = new HashMap<>();

    public KitSonic(KitManager kitManager) {
        super(kitManager, "Sonic", KitType.SONIC, 28000, "You gotta go fast!");

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "BREAD");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 28000);

        defaultsMap.put("inventory.sword.material", "IRON_SWORD");
        defaultsMap.put("inventory.sword.enchantments.durability", 5);
        defaultsMap.put("inventory.sword.enchantments.damage-all", 1);

        defaultsMap.put("armor.material", "LEATHER");
        defaultsMap.put("armor.enchantments.durability", 12);
        defaultsMap.put("armor.enchantments.protection", 2);

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
                .addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

        p.getInventory().setArmorContents(getArmour(
                Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
                getConfig().getInt("armor.enchantments.durability"),
                getConfig().getInt("armor.enchantments.protection"),
                Color.BLUE));

        p.getInventory().setBoots(new ItemBuilder(p.getInventory().getArmorContents()[0]).setColour(Color.RED).build());
    }

    @Override
    public void onMove(Player p) {
        if (p.isSprinting()) {
            if (!sprinting.containsKey(p.getUniqueId()))
                sprinting.put(p.getUniqueId(), 1);
            else {
                int value = sprinting.get(p.getUniqueId());
                value++;
                /* if (level == 1)
					soniceSprint(p, value, Arrays.asList(2, 5, 9, 15, 22));
				else if (level == 2) 
					soniceSprint(p, value, Arrays.asList(1, 4, 6, 11, 18));
				else */
                sonicSprint(p, value, Arrays.asList(1, 5, 10, 15));
                sprinting.put(p.getUniqueId(), value);
            }
        }
    }

    public void disableSprint(Player p){
        if (p.hasPotionEffect(PotionEffectType.SPEED))
            p.removePotionEffect(PotionEffectType.SPEED);
        sprinting.remove(p.getUniqueId());
    }

    private void sonicSprint(Player p, int value, List<Integer> values) {
        if (values.contains(value)) {
            if (p.hasPotionEffect(PotionEffectType.SPEED))
                p.removePotionEffect(PotionEffectType.SPEED);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, values.indexOf(value)));
            if (values.indexOf(value) == values.size() - 1) {
                p.getWorld().playEffect(p.getLocation(), Effect.POTION_BREAK, 1);
                p.getWorld().playSound(p.getLocation(), Sound.WOLF_SHAKE, 1, 1);
            }
        }
    }

    @Override
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7Get speed when you're sprinting", "§7the effect increases the longer you sprint");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        sprinting.remove(uuid);
    }

}
