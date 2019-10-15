package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static java.lang.Integer.parseInt;
import static net.skycade.kitpvp.Messages.CURRENT_COMBO;

public class KitStrafe extends Kit {

    private final Map<UUID, Integer> comboMap = new HashMap<>();

    public KitStrafe(KitManager kitManager) {
        super(kitManager, "Strafe", KitType.STRAFE, 41000, "Do you like to strafe?");

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "DIAMOND_BOOTS");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 41000);

        defaultsMap.put("inventory.sword.material", "STONE_SWORD");
        defaultsMap.put("inventory.sword.enchantments.durability", 5);
        defaultsMap.put("inventory.sword.enchantments.damage-all", 0);

        defaultsMap.put("armor.boots.material", "DIAMOND");
        defaultsMap.put("armor.boots.enchantments.durability", 5);
        defaultsMap.put("armor.boots.enchantments.protection", 1);

        defaultsMap.put("potions.pot1", "SPEED:3");
        defaultsMap.put("potions.pot2", "FAST_DIGGING:2");
        defaultsMap.put("potions.pot3", "INCREASE_DAMAGE:0");

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
    public void applyKit(Player p, int level) {
        p.getInventory().addItem(new ItemBuilder(
                Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
                .addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

        p.getInventory().setBoots(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.boots.material").toUpperCase() + "_BOOTS"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.boots.enchantments.durability"))
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.boots.enchantments.protection")).build());

        String[] pot1 = getConfig().getString("potions.pot1").split(":");
        p.addPotionEffect(new PotionEffect(
                PotionEffectType.getByName(pot1[0]),
                Integer.MAX_VALUE,
                parseInt(pot1[1])));

        String[] pot2 = getConfig().getString("potions.pot2").split(":");
        p.addPotionEffect(new PotionEffect(
                PotionEffectType.getByName(pot2[0]),
                Integer.MAX_VALUE,
                parseInt(pot2[1])));

        String[] pot3 = getConfig().getString("potions.pot3").split(":");
        p.addPotionEffect(new PotionEffect(
                PotionEffectType.getByName(pot3[0]),
                Integer.MAX_VALUE,
                parseInt(pot3[1])));
    }

    @Override
    public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
        if (!comboMap.containsKey(damager.getUniqueId())) {
            comboMap.put(damager.getUniqueId(), 1);
            return;
        }
        int combo = comboMap.get(damager.getUniqueId()) + 1;
        damager.setLevel(combo);
        if (combo > 0 && (combo % 3 == 0))
            CURRENT_COMBO.msg(damager, "%combo%", Integer.toString(combo));

        double dmgInc = 1.0;
        while (combo >= 3) {
            dmgInc += 0.1;
            combo -= 3;
        }
        e.setDamage(e.getDamage() * (Math.min(dmgInc, 1.5)));
    }

    @Override
    public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
        comboMap.remove(damagee.getUniqueId());
    }

    @Override
    public void onMove(Player p) {
        particleTracerEffect(p, Color.RED, 30);
    }

    @Override
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7Comboing someone will make your", "§7hits more deadly");
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        comboMap.remove(e.getPlayer().getUniqueId());
    }

}
