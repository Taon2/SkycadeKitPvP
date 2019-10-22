package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static net.skycade.kitpvp.Messages.*;

public class KitShaco extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack snowball;

    private int snowballStartAmount = 6;
    private int snowballMaxAmount = 8;
    private int snowballRegenSpeed = 20;
    private double backstabMultiplier = 1.2;

    private final Map<UUID, ItemStack[]> shacoArmor = new HashMap<>();

    public KitShaco(KitManager kitManager) {
        super(kitManager, "Shaco", KitType.SHACO, 42000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .setColour(Color.BLACK).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .setColour(Color.BLACK).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .setColour(Color.BLACK).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .setColour(Color.BLACK).build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.DAMAGE_ALL, 2).build();
        snowball = new ItemBuilder(
                Material.SNOW_BALL, snowballStartAmount)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Regain 1 snowball every " + snowballRegenSpeed + " seconds.").build();

        ItemStack icon = new ItemStack(Material.FERMENTED_SPIDER_EYE);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(snowball);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        startItemRunnable(p, snowballRegenSpeed, getSnowball(1), snowballMaxAmount, KitType.SHACO);
    }

    private ItemStack getSnowball(int amount) {
        ItemStack snowballRegen = new ItemStack(snowball);
        snowballRegen.setAmount(amount);

        return snowballRegen;
    }

    @Override
    public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
        if (!(damager.hasPotionEffect(PotionEffectType.INVISIBILITY)))
            return;
        double diffX = damager.getLocation().getDirection().getX() - damagee.getLocation().getDirection().getX();
        double diffZ = damager.getLocation().getDirection().getZ() - damagee.getLocation().getDirection().getZ();

        if (diffX > 0 && diffX < 1 || diffX < 0 && diffX > -1) {
            if (diffZ > 0 && diffZ < 1 || diffZ < 0 && diffZ > -1) {
                BACKSTABBED.msg(damagee);
                e.setDamage(e.getDamage() * backstabMultiplier);
            }
        }
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.IRON_SWORD)
            return;
        if (!addCooldown(p, "Invisibility", 20, true))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        POOF.msg(p);
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 160, 1));

        shacoArmor.put(p.getUniqueId(), p.getInventory().getArmorContents());
        p.getInventory().setArmorContents(null);
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 160, 0));

        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> {
            if (shacoArmor.containsKey(p.getUniqueId())) {
                p.getInventory().setArmorContents(shacoArmor.get(p.getUniqueId()));
                shacoArmor.remove(p.getUniqueId());
            }
        }, 160);
    }

    public void onSnowballUse(ProjectileLaunchEvent e) {
        e.getEntity().setVelocity(e.getEntity().getVelocity().multiply(2.5D));
    }

    public void onSnowballHit(Player shooter, Player damagee) {
        if (!addCooldown(shooter, "Switch Locations", 5, true)) {
            reimburseItem(shooter, getSnowball(1), snowballMaxAmount, KitType.SHACO);
            return;
        }

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(shooter, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        if (shooter.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            teleportBehindPlayer(shooter, damagee.getLocation());
        } else {
            Location shooterLoc = shooter.getLocation();
            Location damageeLoc = damagee.getLocation();
            damagee.teleport(shooterLoc);
            shooter.teleport(damageeLoc);
            POSITIONS_SWITCHED.msg(shooter);
            POSITIONS_SWITCHED.msg(damagee);
        }
        damagee.damage(8, shooter);
        shacoHit.add(damagee.getUniqueId());
        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> shacoHit.remove(damagee.getUniqueId()), 5 * 20);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        shacoArmor.remove(e.getPlayer().getUniqueId());
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /eventshop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Now you see me, now you don't.",
                "",
                ChatColor.GRAY + "Use snowballs to switch positions.",
                ChatColor.GRAY + "Use your sword to become invisible.",
                ChatColor.GRAY + "Using snowballs while invisible",
                ChatColor.GRAY + "teleports you behind your enemy."
        );
    }
}
