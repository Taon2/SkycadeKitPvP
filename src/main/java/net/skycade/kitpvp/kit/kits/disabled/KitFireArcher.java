package net.skycade.kitpvp.kit.kits.disabled;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

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
    private final List<UUID> flameCooldown = new ArrayList<>();

    public KitFireArcher(KitManager kitManager) {
        super(kitManager, "FireArcher", KitType.FIREARCHER, 24000, false, getLore());

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
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Fire 1 arrow every " + arrowCooldown + " second.")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Some arrows are set aflame.").build();
        arrows = new ItemBuilder(
                Material.ARROW, arrowStartAmount).build();

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
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.BOW)
            return;
        if (flameCooldown.contains(p.getUniqueId()))
            return;
        int flameSeconds = 20;
        flameCooldown.add(p.getUniqueId());
        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> flameCooldown.remove(p.getUniqueId()), 60 - flameSeconds);

        item.addEnchantment(Enchantment.ARROW_FIRE, 1);
        p.getWorld().playSound(p.getLocation(), Sound.FIRE_IGNITE, 1.0F, 1.0F);

        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> p.getInventory().forEach((itemStack) -> {
            if (itemStack != null && itemStack.getType() == Material.BOW) {
                if (itemStack.containsEnchantment(Enchantment.ARROW_FIRE)) {
                    FIRE_REMOVED.msg(p);
                    itemStack.removeEnchantment(Enchantment.ARROW_FIRE);
                }
            }
        }), 20 * flameSeconds);
    }


    public void onArrowLaunch(Player shooter, ProjectileLaunchEvent event) {
        if (!addCooldown(shooter, "Bow", arrowCooldown, true)) {
            event.setCancelled(true);
        }
    }

    public void onArrowHit(Player shooter, Player damagee, EntityDamageByEntityEvent event) {
        if (UtilMath.getRandom(0, 100) <= 3)
            damagee.setFireTicks(100);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        bowCooldown.remove(event.getPlayer().getUniqueId());
        flameCooldown.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Unobtainable.");
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


