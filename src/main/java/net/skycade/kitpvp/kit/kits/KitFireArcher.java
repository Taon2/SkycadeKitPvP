package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static net.skycade.kitpvp.Messages.FIRE_REMOVED;

public class KitFireArcher extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack bow;
    private ItemStack arrows;

    private int arrowCooldown = 1;
    private int arrowStartAmount = 1;

    private final List<UUID> bowCooldown = new ArrayList<>();

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    public KitFireArcher(KitManager kitManager) {
        super(kitManager, "FireArcher", KitType.FIREARCHER, 24000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .setColour(Color.ORANGE).build();
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
                Material.STONE_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.DAMAGE_ALL, 1).build();
        bow = new ItemBuilder(
                Material.BOW)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.ARROW_INFINITE, 1)
                .addEnchantment(Enchantment.ARROW_DAMAGE, 1)
                .addEnchantment(Enchantment.ARROW_FIRE, 1)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Fire 1 arrow every " + arrowCooldown + " second.")
                .addLore(" ")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "On a successful hit, players around")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "will be set on fire!")
                .build();
        arrows = new ItemBuilder(
                Material.ARROW, arrowStartAmount).build();

        constantEffects.put(PotionEffectType.FIRE_RESISTANCE, 1);
        constantEffects.put(PotionEffectType.SPEED, 0);

        ItemStack icon = new ItemStack(Material.FIREBALL);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(bow);
        p.getInventory().setItem(27, arrows);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        constantEffects.forEach((effect, amplifier) -> {
            p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
        });
    }

    public void onArrowLaunch(Player shooter, ProjectileLaunchEvent event) {
        if (!addCooldown(shooter, "Bow", arrowCooldown, true)) {
            event.setCancelled(true);
        }
    }

    public void onArrowHit(Player shooter, Player damagee, EntityDamageByEntityEvent event) {
        damagee.setFireTicks(20 * 5);
        if (UtilMath.getRandom(0, 100) <= 3) {
            Entity arrow = event.getDamager();
            shootParticlesFromLoc(damagee, ParticleEffect.FLAME, 500, 0.3F);
            for (Entity entity : arrow.getNearbyEntities(2D, 2D, 2D)){
                if (entity instanceof Player){
                    Player affected = (Player) entity;
                    affected.setFireTicks(20 * 10);
                    affected.damage(6, shooter);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        KitPvPStats stats = KitPvP.getInstance().getStats(event.getPlayer());
        if (stats.getActiveKit() != KitType.FIREARCHER) return;

        bowCooldown.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /eventshop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Napalm!",
                "",
                ChatColor.GRAY + "Shoots flaming arrows."
        );
    }
}


