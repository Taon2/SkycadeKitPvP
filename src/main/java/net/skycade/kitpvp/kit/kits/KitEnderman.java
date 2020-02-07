package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static net.skycade.kitpvp.Messages.WOOSH;

public class KitEnderman extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private int teleportCooldown = 12;

    public KitEnderman(KitManager kitManager) {
        super(kitManager, "Enderman", KitType.ENDERMAN, 35000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 13)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Touching water damages you.")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Immune to arrows.")
                .setColour(Color.PURPLE).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 13)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Touching water damages you.")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Immune to arrows.")
                .setColour(Color.PURPLE).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 13)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Touching water damages you.")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Immune to arrows.")
                .setColour(Color.PURPLE).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 13)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Touching water damages you.")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Immune to arrows.")
                .setColour(Color.PURPLE).build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.DAMAGE_ALL, 1)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Shift + Right clicking your enemy every " + teleportCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "teleports you behind them.").build();

        ItemStack icon = new ItemStack(Material.ENDER_PEARL);
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

    //Shooter is archer, damagee is player with enderman kit
    public void onArrowHit(Player shooter, Player damagee, EntityDamageByEntityEvent e) {
        Location loc = damagee.getLocation();

        for (int i = 0; i < 10; i++) {
            Location newLoc = new Location(damagee.getWorld(), loc.getX() + UtilMath.getRandom(-10, 10), loc.getY(), loc.getZ() + +UtilMath.getRandom(-10, 10));
            if (newLoc.getBlock().getType() == Material.AIR || newLoc.add(0, 1, 0).getBlock().getType() == Material.AIR) {
                damagee.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 1);
                damagee.teleport(newLoc);
                WOOSH.msg(damagee);
                break;
            }
        }
        e.setDamage(0);
    }

    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.IRON_SWORD)
            return;

        Set<Player> targetPlayers = UtilPlayer.getNearbyPlayers(p, p.getLocation(), 10);

        if (targetPlayers.isEmpty())
            return;

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

        if (target == null)
            return;

        if (!addCooldown(p, getName(), teleportCooldown, true) || frozenPlayers.containsKey(p.getUniqueId()))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        if (!teleportBehindPlayer(p, target.getLocation())) {
            removeCooldowns(p, getName());
        } else {
            p.getWorld().playEffect(playerLoc, Effect.ENDER_SIGNAL, 1);
            WOOSH.msg(p);
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
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Hates the water.",
                "",
                ChatColor.GRAY + "Shift + Right clicking your enemy",
                ChatColor.GRAY + "teleports you behind them.",
                ChatColor.GRAY + "Touching water damages you.",
                ChatColor.GRAY + "Immune to projectiles."
        );
    }
}
