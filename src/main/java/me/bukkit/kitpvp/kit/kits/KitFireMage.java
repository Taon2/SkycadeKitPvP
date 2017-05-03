package me.bukkit.kitpvp.kit.kits;

import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.coreclasses.utils.ParticleEffect;
import me.bukkit.kitpvp.coreclasses.utils.UtilPlayer;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
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
    }

    @Override
    public void applyKit(Player p, int level) {
        p.getInventory().addItem(new ItemBuilder(Material.STICK).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level).build());
        p.getInventory().setArmorContents(getArmour(Material.LEATHER_HELMET, 5, level, Color.RED));
        p.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).addEnchantment(Enchantment.DURABILITY, 10).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level <= 2 ? 2 : 3).setColour(Color.YELLOW).build());
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.STICK || fireCooldown.contains(p.getUniqueId()))
            return;
        fireCooldown.add(p.getUniqueId());
        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> fireCooldown.remove(p.getUniqueId()), 4);
        p.getWorld().playSound(p.getLocation(), Sound.FIRE, 1, 1);

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
