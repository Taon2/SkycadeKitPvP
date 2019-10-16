package net.skycade.kitpvp.kit.kits.disabled;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static java.lang.Integer.parseInt;

public class KitNinja extends Kit {

    private final List<UUID> ninjaCooldown = new ArrayList<>();

    public KitNinja(KitManager kitManager) {
        super(kitManager, "Ninja", KitType.NINJA, 32000, false, "Dash to deal the damage.");
        setIcon(new ItemBuilder(Material.LEATHER_BOOTS).setColour(Color.BLACK).build());

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "LEATHER_BOOTS");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 32000);

        defaultsMap.put("inventory.sword.material", "STONE_SWORD");
        defaultsMap.put("inventory.sword.enchantments.durability", 5);
        defaultsMap.put("inventory.sword.enchantments.damage-all", 3);

        defaultsMap.put("armor.material", "LEATHER");
        defaultsMap.put("armor.enchantments.protection", 4);

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
    public void applyKit(Player p) {
        p.getInventory().addItem(new ItemBuilder(
                Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
                .addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

        p.getInventory().setBoots(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_BOOTS"))
                .setColour(Color.BLACK)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.enchantments.protection")).build());

        String[] pot1 = getConfig().getString("potions.pot1").split(":");
        p.addPotionEffect(new PotionEffect(
                PotionEffectType.getByName(pot1[0]),
                Integer.MAX_VALUE,
                parseInt(pot1[1])));
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.STONE_SWORD)
            return;
        if (ninjaCooldown.contains(p.getUniqueId()))
            return;
        ninjaCooldown.add(p.getUniqueId());
        Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> ninjaCooldown.remove(p.getUniqueId()), 60);
        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0));
        tpDash(p, 6);
    }

    private void tpDash(Player p, int range) {
        final Location playerLoc = p.getLocation();
        double nX;
        double nZ;
        float nang = playerLoc.getYaw() + 90;
        if (nang < 0)
            nang += 360;
        nX = Math.cos(Math.toRadians(nang)) * range;
        nZ = Math.sin(Math.toRadians(nang)) * range;

        Location newLoc = new Location(playerLoc.getWorld(), playerLoc.getX() + nX, playerLoc.getY(), playerLoc.getZ() + nZ, playerLoc.getYaw(), playerLoc.getPitch());
        if (!isValidBlock(newLoc.getBlock().getType()) || (newLoc.getBlock().getType() != Material.AIR && newLoc.add(0, 1, 0).getBlock().getType() != Material.AIR && newLoc.add(0, 2, 0).getBlock().getType() != Material.AIR)) {
            if (range <= 2) {
                return;
            } else
                tpDash(p, range - 1);
        }
        p.teleport(newLoc);
        p.getWorld().playEffect(p.getLocation(), Effect.ENDER_SIGNAL, 1);
    }

    @Override
    public void onMove(Player p) {
        particleTracerEffect(p, Color.PURPLE, 30);
    }

    @Override
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7Use your sword to dash around", "§7dashing will give you a short strength buff");
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        ninjaCooldown.remove(e.getPlayer().getUniqueId());
    }

}
