package net.skycade.kitpvp.kit.kits.disabled;

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

import static net.skycade.kitpvp.Messages.DEFENCE_UP;

public class KitUnicorn extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private int rainbowCooldown = 20;

    private final Color[] rainbowColors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.AQUA, Color.BLUE, Color.PURPLE, Color.FUCHSIA};
    private final List<UUID> rodUse = new ArrayList<>();

    public KitUnicorn(KitManager kitManager) {
        super(kitManager, "Unicorn", KitType.UNICORN, 40000, false, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 14)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Taking damage grants a resistance buff.")
                .setColour(Color.PURPLE).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Taking damage grants a resistance buff.")
                .setColour(Color.WHITE).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Taking damage grants a resistance buff.")
                .setColour(Color.WHITE).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Taking damage grants a resistance buff.")
                .setColour(Color.WHITE).build();
        weapon = new ItemBuilder(
                Material.STICK)
                .addEnchantment(Enchantment.DAMAGE_ALL, 5)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "%click% every " + rainbowCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "shoots a rainbow from your wand.").build();

        ItemStack icon = new ItemStack(Material.HAY_BLOCK);
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
    public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
        if (UtilMath.getRandom(0, 100) <= 3) {
            damagee.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 160, 2));
            DEFENCE_UP.msg(damager);
            shootParticlesFromLoc(damagee, ParticleEffect.WATER_WAKE, 500, 0.3F);
        }
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.STICK)
            return;
        if (!addCooldown(p, "Rainbow", rainbowCooldown, true))
            return;

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
                        ParticleEffect.SPELL_MOB.display(new ParticleEffect.OrdinaryColor(col), loc, 20);
                if (UtilMath.getRandom(0, 3) == 2)
                    ParticleEffect.LAVA.display(0, 0, 0, 0, 3, loc, 20);

                for (Player target : UtilPlayer.getNearbyPlayers(p, loc, 1).stream().filter(player -> !player.equals(p) && player.getGameMode() == GameMode.SURVIVAL).collect(Collectors.toList())) {
                    target.damage(14, p);
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
                }
                if (t > 1.7)
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
                ChatColor.GRAY + "" + ChatColor.ITALIC + "A mythical creature.",
                "",
                ChatColor.GRAY + "Shoots rainbows from a wand.",
                ChatColor.GRAY + "Has a chance to gain resistance."
        );
    }
}
