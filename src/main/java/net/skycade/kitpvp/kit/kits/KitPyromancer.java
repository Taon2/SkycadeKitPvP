package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class KitPyromancer extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack bow;
    private ItemStack arrows;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    private int arrowCooldown = 2;
    private int arrowRegenSpeed = 12;
    private int arrowStartAmount = 8;
    private int arrowMaxAmount = 8;

    private List<Arrow> arrowList = new ArrayList<>();

    private int fireballCooldown = 20;

    public KitPyromancer(KitManager kitManager) {
        super(kitManager, "Pyromancer", KitType.PYROMANCER, 50000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 7)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .setColour(Color.YELLOW).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 7)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .setColour(Color.ORANGE).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 7)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .setColour(Color.ORANGE).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 7)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .setColour(Color.RED).build();
        weapon = new ItemBuilder(
                Material.BLAZE_ROD)
                .addEnchantment(Enchantment.DAMAGE_ALL, 5)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Right clicking every " + fireballCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "launches a fireball.").build();
        bow = new ItemBuilder(
                Material.BOW)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.ARROW_DAMAGE, 2)
                .addEnchantment(Enchantment.ARROW_FIRE, 1)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Fire 1 arrow every " + arrowCooldown + " seconds.")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Grounded arrows create a ring of fire around them.").build();
        arrows = new ItemBuilder(
                Material.ARROW, arrowStartAmount)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Regain 1 arrow every " + arrowRegenSpeed + " seconds.").build();

        constantEffects.put(PotionEffectType.FIRE_RESISTANCE, 0);

        ItemStack icon = new ItemStack(Material.FLINT_AND_STEEL);
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

        startItemRunnable(p, arrowRegenSpeed, getArrows(1), arrowMaxAmount, KitType.PYROMANCER);
    }

    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.BLAZE_ROD)
            return;
        if (!addCooldown(p, "Fireball", fireballCooldown, true))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        p.launchProjectile(Fireball.class);
    }

    public void onArrowLaunch(Player shooter, ProjectileLaunchEvent event) {
        if (!addCooldown(shooter, "Bow", arrowCooldown, true)) {
            event.setCancelled(true);
        }

        event.getEntity().setCustomName(shooter.getName());
        event.getEntity().setCustomNameVisible(false);
        arrowList.add((Arrow) event.getEntity());
    }

    public void onArrowLand(Player shooter, Block block, ProjectileHitEvent event) {
        circle(event.getEntity().getLocation());
    }

    private void circle(Location loc) {
        for (int r = 1; r < 3; r++) {
            int cx = loc.getBlockX();
            int cy = loc.getBlockY();
            int cz = loc.getBlockZ();
            World w = loc.getWorld();
            int rSquared = r * r;
            for (int x = cx - r; x <= cx +r; x++) {
                for (int z = cz - r; z <= cz +r; z++) {
                    if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= rSquared) {
                        Block block = w.getBlockAt(x, cy, z);
                        if (block.getType() == Material.AIR) {
                            block.setType(Material.FIRE);

                            Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), new BukkitRunnable() {
                                @Override
                                public void run() {
                                    block.setType(Material.AIR);
                                }
                            }, r + 1);
                        }
                    }
                }
            }
        }
    }

    private ItemStack getArrows(int amount) {
        ItemStack arrowRegen = new ItemStack(arrows);
        arrowRegen.setAmount(amount);

        return arrowRegen;
    }

    public void removeSummon(int seconds, Player p) {
        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> {
            for (Arrow arrow : arrowList)
                if (arrow.getCustomName().contains(p.getName())) {
                    arrow.remove();
                }
        }, seconds * 20);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.GOLD + "" + ChatColor.BOLD + "Ranged Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Through fire and flames.",
                "",
                ChatColor.GRAY + "Grounded arrows create fire around them.",
                ChatColor.GRAY + "Right click to shoot a fireball."
        );
    }
}