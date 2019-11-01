package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class KitFisherman extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack fishingRod;

    private int grappleCooldown = 4;

    public KitFisherman(KitManager kitManager) {
        super(kitManager, "Fisherman", KitType.FISHERMAN, 46000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 9)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.fromRGB(200, 255, 255)).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 9)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.fromRGB(200, 255, 255)).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 9)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.fromRGB(200, 255, 255)).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 9)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.fromRGB(200, 255, 255)).build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5).build();
        fishingRod = new ItemBuilder(
                Material.FISHING_ROD)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Using the fishing rod every " + 4 + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "grapples you towards the hook.").build();

        ItemStack icon = new ItemStack(Material.FISHING_ROD);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(fishingRod);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);
    }

    public void onRodUse(Player p, ProjectileLaunchEvent e) {
        if (!addCooldown(p, "Grapple", grappleCooldown, true))
            return;

        Location target = getTarget(p, 30);
        if (target == null)
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        p.teleport(p.getLocation().add(0, 0.5, 0));
        Vector v = getVectorForPoints(p.getLocation(), target);
        e.getEntity().setVelocity(v);

        Bukkit.getScheduler().scheduleSyncDelayedTask(getKitManager().getKitPvP(), () -> p.setVelocity(v), 5);
    }

    private Location getTarget(Player hookshooter, Integer amount) {
        for (Block block : hookshooter.getLineOfSight((Set<Material>) null, amount))
            if (!block.getType().equals(Material.AIR))
                return block.getLocation();
        return null;
    }

    private Vector getVectorForPoints(Location l1, Location l2) {
        double g = -0.08;
        double t = l2.distance(l1);
        double vX = (1.0 + 0.07 * t) * (l2.getX() - l1.getX()) / t;
        double vY = (1.0 + 0.03 * t) * (l2.getY() - l1.getY()) / t - 0.5 * g * t;
        double vZ = (1.0 + 0.07 * t) * (l2.getZ() - l1.getZ()) / t;
        return new Vector(vX, vY, vZ);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "It got away...",
                "",
                ChatColor.GRAY + "Your fishing rod is",
                ChatColor.GRAY + "a grappling hook."
        );
    }
}
