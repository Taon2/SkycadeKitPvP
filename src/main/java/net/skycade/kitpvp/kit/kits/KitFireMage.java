package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class KitFireMage extends Kit {

    private List<UUID> fireCooldown = new ArrayList<>();

    public KitFireMage(KitManager kitManager) {
        super(kitManager, "FireMage", KitType.FIREMAGE, 34000, "Use fire magic!");
        setIcon(new ItemStack(Material.FIREWORK_CHARGE));

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("inventory.stick.enchantments.durability", 5);
        defaultsMap.put("inventory.stick.enchantments.damage-all", 1);

        defaultsMap.put("armor.material", "LEATHER");
        defaultsMap.put("armor.enchantments.durability", 5);
        defaultsMap.put("armor.enchantments.protection", 1);

        defaultsMap.put("armor.helmet.material", "LEATHER");
        defaultsMap.put("armor.helmet.enchantments.durability", 10);
        defaultsMap.put("armor.helmet.enchantments.protection", 2);

        setConfigDefaults(defaultsMap);
    }

    @Override
    public void applyKit(Player p, int level) {
        p.getInventory().addItem(new ItemBuilder(
                Material.STICK)
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.stick.enchantments.durability"))
                .addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.stick.enchantments.damage-all")).build());

        p.getInventory().setArmorContents(getArmour(
                Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
                getConfig().getInt("armor.enchantments.durability"),
                getConfig().getInt("armor.enchantments.protection"),
                Color.RED));

        p.getInventory().setHelmet(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.helmet.material").toUpperCase() + "_HELMET"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.helmet.enchantments.durability"))
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.helmet.enchantments.protection"))
                .setColour(Color.YELLOW).build());
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.STICK || fireCooldown.contains(p.getUniqueId()))
            return;
        fireCooldown.add(p.getUniqueId());
        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> fireCooldown.remove(p.getUniqueId()), 4);
        p.getWorld().playSound(p.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 1, 1);

        new BukkitRunnable() {
            Location loc = p.getEyeLocation().subtract(0, 0.5, 0);
            Vector dir = p.getLocation().getDirection().normalize();

            double t = 0.0;

            public void run() {
                t += 0.03F;
                double x = dir.getX() * t;
                double y = dir.getY() * t;
                double z = dir.getZ() * t;
                loc.add(x, y, z);


                ParticleEffect.FLAME.display(0, 0, 0, 0.03F, 3, loc, 30);

                for (Player target : UtilPlayer.getNearbyPlayers(loc, 1.5).stream().filter(player -> !player.equals(p) && player.getGameMode() == GameMode.SURVIVAL).collect(Collectors.toList())) {
                    target.damage((getLevel(p) * 2) + 3, p);
                    target.setFireTicks(20);
                }

                if (t > 0.4 + (0.1 * getLevel(p)))
                    this.cancel();
            }
        }.runTaskTimer(getKitManager().getPlugin(), 0, 1);

    }

    @Override
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7Hold your mouse while holding", "§7the wand to shoot fire", "§7Your spell will be stronger on a higher level.");
    }

}
