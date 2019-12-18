package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitVampire extends Kit {

    public KitVampire(KitManager kitManager) {
        super(kitManager, "Vampire", KitType.VAMPIRE, 25000, "Loves the taste of blood");

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "REDSTONE");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 25000);

        defaultsMap.put("inventory.sword.material", "IRON_SWORD");
        defaultsMap.put("inventory.sword.enchantments.durability", 5);
        defaultsMap.put("inventory.sword.enchantments.damage-all", 0);

        defaultsMap.put("armor.material", "LEATHER");
        defaultsMap.put("armor.enchantments.durability", 10);
        defaultsMap.put("armor.enchantments.protection", 3);

        defaultsMap.put("armor.helmet.material", "LEATHER");
        defaultsMap.put("armor.helmet.enchantments.durability", 12);
        defaultsMap.put("armor.helmet.enchantments.protection", 1);

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

        p.getInventory().setArmorContents(getArmour(
                Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
                getConfig().getInt("armor.enchantments.durability"),
                getConfig().getInt("armor.enchantments.protection"),
                Color.RED));

        p.getInventory().setHelmet(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.helmet.material").toUpperCase() + "_HELMET"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.helmet.enchantments.durability"))
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.helmet.enchantments.protection"))
                .setColour(Color.BLACK).build());
    }

    @Override
    public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
        int random = UtilMath.getRandom(0, 100);
        int level = getLevel(damager);
        int chance = 7;

        if (random < chance) {
            double healAmount = 2.5;
            if (damager.getHealth() + healAmount < damager.getMaxHealth())
                damager.setHealth(damager.getHealth() + healAmount);
            else if (damager.getHealth() == damager.getMaxHealth())
                damager.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 60, 0));
            else
                damager.setHealth(damager.getMaxHealth());
            damager.sendMessage("§cHealed!");

        } else if (random < chance * 2) {
            damagee.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80 + 20 * 3 - 1, 0));
            damagee.sendMessage("§cYou are bit by §f" + damager.getName() + "§c.");
        }
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.IRON_SWORD)
            return;
        int level = getLevel(p);
        if (!addCooldown(p, getName(), 30, true))
            return;
        Set<Player> targetPlayers = UtilPlayer.getNearbyPlayers(p.getLocation(), 4);
        if (targetPlayers.size() <= 1) {
            removeCooldowns(p, getName());
            return;
        }

        double healAmount = 0.0;
        for (Player target : targetPlayers) {
            if (target != p) {
                startBleed(p, target, 7);
                healAmount += 4;
            }
        }

        if (healAmount > 0) {
            if (p.getHealth() + healAmount > p.getMaxHealth()) {
                healAmount -= (p.getMaxHealth() - p.getHealth());
                if (healAmount > 0)
                    p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, (int) (20 * Math.floor(healAmount)), 0));
            } else
                p.setHealth(p.getHealth() + healAmount);
            p.sendMessage("§7You got §7Healed!");
        }
    }

    @Override
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7Use your sword ability to give", "§7players around you a bleed effect", "§7you will also get healed if there are many", "§7players around you");
    }

    @SuppressWarnings("deprecation")
    private void startBleed(Player vampire, Player p, int seconds) {
        ParticleEffect.REDSTONE.display(0.3F, 0.3F, 0.3F, 0, 5, p.getEyeLocation(), 40);

        if (seconds > 0)
            Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> {
                if (getKitManager().getKitPvP().getSpawnRegion().contains(p))
                    return;
                p.setLastDamageCause(new EntityDamageByEntityEvent(vampire, p, DamageCause.ENTITY_ATTACK, 6));
                p.damage(4);
                startBleed(vampire, p, seconds - 1);
            }, 15);
    }

}
