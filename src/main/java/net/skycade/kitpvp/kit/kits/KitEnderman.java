package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class KitEnderman extends Kit {

    public KitEnderman(KitManager kitManager) {
        super(kitManager, "Enderman", KitType.ENDERMAN, 35000, "Scared of water");

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "ENDER_CHEST");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 35000);

        defaultsMap.put("inventory.sword.material", "IRON_SWORD");
        defaultsMap.put("inventory.sword.enchantments.durability", 5);
        defaultsMap.put("inventory.sword.enchantments.damage-all", 1);

        defaultsMap.put("armor.material", "LEATHER");
        defaultsMap.put("armor.enchantments.durability", 12);
        defaultsMap.put("armor.enchantments.protection", 3);

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
                Material.getMaterial(getConfig().getString("inventory.sword.material")))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
                .addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

        p.getInventory().setArmorContents(getArmour(
                Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
                getConfig().getInt("armor.enchantments.durability"),
                getConfig().getInt("armor.enchantments.protection"),
                Color.PURPLE));
    }

    //Shooter is archer, damagee is player with enderman kit
    public void onArrowHit(Player shooter, Player damagee, EntityDamageByEntityEvent e) {
        Location loc = damagee.getLocation();

        for (int i = 0; i < 10; i++) {
            Location newLoc = new Location(damagee.getWorld(), loc.getX() + UtilMath.getRandom(-10, 10), loc.getY(), loc.getZ() + + UtilMath.getRandom(-10, 10));
            if (newLoc.getBlock().getType() != Material.AIR || newLoc.add(0, 1, 0).getBlock().getType() != Material.AIR)
                continue;
            else {
                damagee.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 1);
                damagee.teleport(newLoc);
                damagee.sendMessage("§5Woosh!");
                break;
            }
        }
        e.setDamage(0);
    }

    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.IRON_SWORD)
            return;
        int level = getLevel(p);
        if (onCooldown(p, getName()) || !addCooldown(p, getName(), 16, true))
            return;

        Set<Player> targetPlayers = UtilPlayer.getNearbyPlayers(p.getLocation(), 10);
        targetPlayers.remove(p);
        if (targetPlayers.isEmpty()) {
            removeCooldowns(p);
            return;
        }

        Player target = null;
        double distance = 100;
        final Location playerLoc = p.getLocation();

        for (Player pl : targetPlayers) {
            double dis = pl.getLocation().distance(playerLoc);
            if (dis < distance) {
               target = pl;
               distance = dis;
            }
        }

        if (target == null) {
            removeCooldowns(p);
            return;
        }

        if (!teleportBehindPlayer(p, target.getLocation())) {
            removeCooldowns(p);
            return;
        } else {
            p.getWorld().playEffect(playerLoc, Effect.ENDER_SIGNAL, 1);
            p.sendMessage("§5Woosh!");
        }
    }

    @Override
    public void onMove(Player p) {
        shootParticlesFromLoc(p, ParticleEffect.SPELL_MOB, 10, 0.1F);

        if (p.getLocation().getBlock().getType() == Material.WATER || p.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
            p.damage(1);
        }
    }

    @Override
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7Right click with your sword", "§7to teleport behind the closest player", "§7Touching water will damage you");
    }

}
