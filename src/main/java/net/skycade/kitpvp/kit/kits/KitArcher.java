package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static java.lang.Integer.parseInt;
import static net.skycade.kitpvp.Messages.*;

public class KitArcher extends Kit {

    private final List<UUID> bowCooldown = new ArrayList<>();

    public KitArcher(KitManager kitManager) {
        super(kitManager, "Archer", KitType.ARCHER, 8000, "Chance-based archer kit");

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "BOW");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 8000);

        defaultsMap.put("inventory.sword.material", "STONE_SWORD");
        defaultsMap.put("inventory.sword.enchantments.durability", 5);
        defaultsMap.put("inventory.sword.enchantments.damage-all", 0);
        defaultsMap.put("inventory.bow.enchantments.durability", 5);
        defaultsMap.put("inventory.bow.enchantments.arrow-damage", 1);
        defaultsMap.put("inventory.armour.type", "LEATHER");
        defaultsMap.put("inventory.armour.durability", 5);
        defaultsMap.put("inventory.armour.protection", 2);
        defaultsMap.put("inventory.arrow.amount", 16);
        defaultsMap.put("inventory.arrow.regen-speed", 3);
        defaultsMap.put("inventory.arrow.max-amount", 64);

        defaultsMap.put("potions.pot1", "SPEED:1");

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

        p.getInventory().addItem(new ItemBuilder(
                Material.BOW)
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.bow.enchantments.durability"))
                .addEnchantment(Enchantment.ARROW_DAMAGE, getConfig().getInt("inventory.bow.enchantments.arrow-damage")).build());

        p.getInventory().addItem(new ItemBuilder(
                Material.ARROW, getConfig().getInt("inventory.arrow.amount")).build());

        p.getInventory().setArmorContents(getArmour(Material.getMaterial(
                getConfig().getString("inventory.armour.type") + "_HELMET"),
                getConfig().getInt("inventory.armour.durability"),
                getConfig().getInt("inventory.armour.protection")));

        String[] pot1 = getConfig().getString("potions.pot1").split(":");
        p.addPotionEffect(new PotionEffect(
                PotionEffectType.getByName(pot1[0]),
                Integer.MAX_VALUE,
                parseInt(pot1[1])));

        startItemRunnable(p, getConfig().getInt("inventory.arrow.regen-speed"), new ItemBuilder(
                Material.ARROW).build(), getConfig().getInt("inventory.arrow.max-amount"), KitType.ARCHER);
    }

    public void onArrowLaunch(Player shooter, ProjectileLaunchEvent e) {
        if (bowCooldown.contains(shooter.getUniqueId())) {
            e.setCancelled(true);
            return;
        }
        bowCooldown.add(shooter.getUniqueId());
        Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> bowCooldown.remove(shooter.getUniqueId()), 20);
    }

    public void onArrowHit(Player shooter, Player damagee, EntityDamageByEntityEvent e) {
        archerChanceEffects(shooter, damagee, e, 60, 50, 30, 30, 20);
    }

    private void archerChanceEffects(Player archer, Player target, EntityDamageByEntityEvent e, int regainHealth, int doubleDamage, int slowEffect, int miningEffect, int blindEffect) {
        int randomNumber = UtilMath.getRandom(0, 1000);
        if (randomNumber <= regainHealth) {
            archer.setHealth(archer.getMaxHealth());
            HEALTH_BOOST.msg(archer);
        } else if (randomNumber <= regainHealth + doubleDamage) {
            DOUBLE_DAMAGE.msg(archer);
            e.setDamage(e.getDamage() * 2);
            DOUBLE_DAMAGE_YOU.msg(target, "%player%", archer.getName());
        } else if (randomNumber <= regainHealth + doubleDamage + slowEffect) {
            TARGET_SLOWED.msg(archer);
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 0));
            YOURE_SLOWED.msg(target, "%player%", archer.getName());
        } else if (randomNumber <= regainHealth + doubleDamage + slowEffect + miningEffect + blindEffect) {
            TARGET_BLINDED.msg(archer);
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 6 * 20, 0));
            YOURE_BLINDED.msg(target, "%player%", archer.getName());
        }
    }

    @Override
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7Arrows have a chance to", "§7give effects to your target.");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        bowCooldown.remove(e.getPlayer().getUniqueId());
    }

}