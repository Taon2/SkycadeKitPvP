package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class KitUnicorn extends Kit {

    private final Color[] rainbowColors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.AQUA, Color.BLUE, Color.PURPLE, Color.FUCHSIA};
    private final List<UUID> rodUse = new ArrayList<>();

    public KitUnicorn(KitManager kitManager) {
        super(kitManager, "Unicorn", KitType.UNICORN, 40000, "Be a mythical creature");

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "HAY_BLOCK");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 40000);

        defaultsMap.put("armor.helmet.material", "LEATHER");
        defaultsMap.put("armor.helmet.enchantments.protection", 3);
        defaultsMap.put("armor.helmet.enchantments.durability", 14);

        defaultsMap.put("armor.chestplate.material", "LEATHER");
        defaultsMap.put("armor.chestplate.enchantments.protection", 3);
        defaultsMap.put("armor.chestplate.enchantments.durability", 12);

        defaultsMap.put("armor.leggings.material", "LEATHER");
        defaultsMap.put("armor.leggings.enchantments.protection", 2);
        defaultsMap.put("armor.leggings.enchantments.durability", 12);

        defaultsMap.put("armor.boots.material", "LEATHER");
        defaultsMap.put("armor.boots.enchantments.protection", 4);
        defaultsMap.put("armor.boots.enchantments.durability", 12);

        defaultsMap.put("inventory.stick.enchantments.damage-all", 5);

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
        p.getInventory().setHelmet(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.helmet.material").toUpperCase() + "_HELMET"))
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.helmet.enchantments.protection"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.helmet.enchantments.durability"))
                .setColour(Color.PURPLE).build());

        p.getInventory().setChestplate(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.chestplate.material").toUpperCase() + "_CHESTPLATE"))
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.chestplate.enchantments.protection"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.chestplate.enchantments.durability"))
                .setColour(Color.WHITE).build());

        p.getInventory().setLeggings(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.leggings.material").toUpperCase() + "_LEGGINGS"))
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.leggings.enchantments.protection"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.leggings.enchantments.durability"))
                .setColour(Color.WHITE).build());

        p.getInventory().setBoots(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.boots.material").toUpperCase() + "_BOOTS"))
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.boots.enchantments.protection"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.boots.enchantments.durability"))
                .setColour(Color.WHITE).build());

        p.getInventory().addItem(new ItemBuilder(
                Material.STICK)
                .addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.stick.enchantments.damage-all")).build());
    }

    @Override
    public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
        if (UtilMath.getRandom(0, 100) <= 3) {
            damager.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 160, 3));
            damager.sendMessage("§fSwing speed up!");
        }
    }

    @Override
    public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
        if (UtilMath.getRandom(0, 100) <= 3) {
            damagee.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 160, 2));
            damagee.sendMessage("§fDefence up!");
            shootParticlesFromLoc(damagee, ParticleEffect.WATER_WAKE, 500, 0.3F);
        }
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        int level = getLevel(p);
        if (item.getType() == Material.STICK) {
            if (rodUse.contains(p.getUniqueId()))
                return;
            rodUse.add(p.getUniqueId());
            Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> rodUse.remove(p.getUniqueId()), 70);

            new BukkitRunnable() {
                Location loc = p.getEyeLocation().subtract(0, 0.2, 0);
                Vector dir = p.getLocation().getDirection().normalize();
                double t = 0.0;

                public void run() {
                    t += 0.07F;
                    double x = dir.getX() * t;
                    double y = dir.getY() * t;
                    double z = dir.getZ() * t;
                    loc.add(x, y, z);

                    for (int i = 0; i < 3; i++)
                        for (Color col : rainbowColors)
                            ParticleEffect.SPELL_MOB.display(new ParticleEffect.OrdinaryColor(col), loc, 30);
                    if (UtilMath.getRandom(0, 3) == 2)
                        ParticleEffect.LAVA.display(0, 0, 0, 0, 3, loc, 30);

                    for (Player target : UtilPlayer.getNearbyPlayers(loc, 1).stream().filter(player -> !player.equals(p) && player.getGameMode() == GameMode.SURVIVAL).collect(Collectors.toList())) {
                        target.damage(14, p);
                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
                    }
                    if (t > 1.7)
                        this.cancel();
                }
            }.runTaskTimer(getKitManager().getPlugin(), 0, 1);
        }
    }

    @Override
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7You got a chance to gain", "§7damage resistance when getting hit", "§7and a chance to get haste when hitting someone", "§7use your wand to shoot a rainbow");
    }

}
