package net.skycade.kitpvp.kit.kits.disabled;

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

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private List<UUID> fireCooldown = new ArrayList<>();

    public KitFireMage(KitManager kitManager) {
        super(kitManager, "FireMage", KitType.FIREMAGE, 34000, false, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .setColour(Color.YELLOW).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .setColour(Color.ORANGE).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .setColour(Color.ORANGE).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .setColour(Color.ORANGE).build();
        weapon = new ItemBuilder(
                Material.STICK)
                .addEnchantment(Enchantment.DAMAGE_ALL, 1).build();

        ItemStack icon = new ItemStack(Material.FIREWORK_CHARGE);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);
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
                    target.damage(2+ 3, p);
                    target.setFireTicks(20);
                }

                if (t > 0.4 + 0.1)
                    this.cancel();
            }
        }.runTaskTimer(getKitManager().getKitPvP(), 0, 1);

    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Unobtainable.");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Pyroblast!",
                "",
                ChatColor.GRAY + "Shoots fireballs."
        );
    }
}
