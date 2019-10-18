package net.skycade.kitpvp.kit.kits.disabled;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
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

public class KitTribesman extends Kit {
    private final List<UUID> tribesCd = new ArrayList<>();

    public KitTribesman(KitManager kitManager) {
        super(kitManager, "Tribesman", KitType.TRIBESMAN, 37000, false, "Tribesman is good with herbs");

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "GOLD_CHESTPLATE");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 37000);

        defaultsMap.put("inventory.sword.material", "IRON_SWORD");
        defaultsMap.put("inventory.sword.enchantments.durability", 5);
        defaultsMap.put("inventory.sword.enchantments.knockback", 1);
        defaultsMap.put("inventory.sword.enchantments.damage-all", 0);

        defaultsMap.put("armor.helmet.material", "IRON");

        defaultsMap.put("armor.chestplate.material", "GOLD");
        defaultsMap.put("armor.chestplate.enchantments.durability", 5);

        defaultsMap.put("armor.leggings.material", "GOLD");
        defaultsMap.put("armor.leggings.enchantments.durability", 1);

        defaultsMap.put("armor.boots.material", "IRON");

        defaultsMap.put("potions.pot1", "DAMAGE_RESISTANCE:0");
        defaultsMap.put("potions.pot2", "JUMP:0");
        defaultsMap.put("potions.pot3", "REGENERATION:0");

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
                .addEnchantment(Enchantment.KNOCKBACK, getConfig().getInt("inventory.sword.enchantments.knockback"))
                .addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

        p.getInventory().setHelmet(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.helmet.material").toUpperCase() + "_HELMET")).build());

        p.getInventory().setChestplate(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.chestplate.material").toUpperCase() + "_CHESTPLATE"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.chestplate.enchantments.durability")).build());

        p.getInventory().setLeggings(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.leggings.material").toUpperCase() + "_LEGGINGS"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.leggings.enchantments.durability")).build());

        p.getInventory().setBoots(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.boots.material").toUpperCase() + "_BOOTS")).build());

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
    public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
        if (tribesCd.contains(damagee.getUniqueId()))
            return;

        if (e.getFinalDamage() >= 4) {
            tribesEffect(damagee, 6 + 2 * 3);
            tribesCd.add(damagee.getUniqueId());
            Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> tribesCd.remove(damagee.getUniqueId()), 220 - (3 * 20));
        }
    }

    private void tribesEffect(Player p, int seconds) {
        for (PotionEffect effect : p.getActivePotionEffects())
            p.removePotionEffect(effect.getType());
        p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, seconds * 20, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, seconds * 20, 0));
        p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, seconds * 20, 0));
    }

    @Override
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7You can get potion effects", "§7when someone deals a lot", "§7of damage to you");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        tribesCd.remove(e.getPlayer().getUniqueId());
    }

}
