package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.events.CaptureTheFlagEvent;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static net.skycade.kitpvp.Messages.*;

public class KitShaco extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack snowball;

    private int snowballCooldown = 5;
    private int snowballStartAmount = 6;
    private int snowballMaxAmount = 8;
    private int snowballRegenSpeed = 20;

    private List<Snowball> snowballList = new ArrayList<>();

    private double backstabMultiplier = 1.2;

    private int invisibilityCooldown = 20;
    private int invisibilityLength = 8;

    private final Map<UUID, ItemStack[]> shacoArmor = new HashMap<>();
    private Map<UUID, Integer> armorRunnableMap = new HashMap<>();

    public KitShaco(KitManager kitManager) {
        super(kitManager, "Shaco", KitType.SHACO, 0, getLore());

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
                .addEnchantment(Enchantment.DAMAGE_ALL, 1)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "%click% every " + invisibilityCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "makes you invisible for " + invisibilityLength + " seconds.")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Hits from behind deal more damage while invisible .").build();
        snowball = new ItemBuilder(
                Material.SNOW_BALL, snowballStartAmount)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Throwing a snowball every " + snowballCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "makes you switch locations with your target,")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "or teleport you behind your target while invisible.")
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

    @Override
    public void onDamageDealHit(EntityDamageByEntityEvent event, Player damager, Player damagee) {
        if (!(damager.hasPotionEffect(PotionEffectType.INVISIBILITY)))
            return;
        double diffX = damager.getLocation().getDirection().getX() - damagee.getLocation().getDirection().getX();
        double diffZ = damager.getLocation().getDirection().getZ() - damagee.getLocation().getDirection().getZ();

        if (diffX > 0 && diffX < 1 || diffX < 0 && diffX > -1) {
            if (diffZ > 0 && diffZ < 1 || diffZ < 0 && diffZ > -1) {
                BACKSTABBED.msg(damagee);
                event.setDamage(event.getDamage() * backstabMultiplier);
            }
        }
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.IRON_SWORD)
            return;
        if (!addCooldown(p, "Invisibility", invisibilityCooldown, true))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        POOF.msg(p);
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 160, 1));

        shacoArmor.put(p.getUniqueId(), p.getInventory().getArmorContents());
        p.getInventory().setArmorContents(null);
        p.setCustomNameVisible(false);
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, invisibilityLength * 20, 0));

        int runnableId = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(KitPvP.getInstance(), new BukkitRunnable() {
            public void run() {
                p.setCustomNameVisible(true);
                if (shacoArmor.containsKey(p.getUniqueId())) {
                    if (KitPvP.getInstance().getStats(p).getActiveKit() == KitType.SHACO)
                        p.getInventory().setArmorContents(shacoArmor.get(p.getUniqueId()));
                }
                shacoArmor.remove(p.getUniqueId());
            }
        }, 160);

        armorRunnableMap.put(p.getUniqueId(), runnableId);
    }

    @Override
    public void cancelRunnables(Player p) {
        if (armorRunnableMap.containsKey(p.getUniqueId())) {
            Bukkit.getScheduler().cancelTask(armorRunnableMap.get(p.getUniqueId()));
            armorRunnableMap.remove(p.getUniqueId());
        }
    }

    public void onSnowballUse(Player shooter, ProjectileLaunchEvent event) {
        event.getEntity().setCustomName(shooter.getName());
        event.getEntity().setCustomNameVisible(false);
        snowballList.add((Snowball) event.getEntity());

        event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(2.5D));
    }

    public void onSnowballHit(Player shooter, Player damagee) {
        if (CaptureTheFlagEvent.getInstance().getBegin() != null && CaptureTheFlagEvent.getInstance().isTeamRed(shooter) == CaptureTheFlagEvent.getInstance().isTeamRed(damagee)) {
            return;
        }

        if (!addCooldown(shooter, "Switch Locations", snowballCooldown, true) || frozenPlayers.containsKey(shooter.getUniqueId()) || frozenPlayers.containsKey(damagee.getUniqueId())) {
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
    public void onPlayerQuit(PlayerQuitEvent event) {
        KitPvPStats stats = KitPvP.getInstance().getStats(event.getPlayer());
        if (stats.getActiveKit() != KitType.SHACO) return;

        shacoArmor.remove(event.getPlayer().getUniqueId());
    }

    private ItemStack getSnowball(int amount) {
        ItemStack snowballRegen = new ItemStack(snowball);
        snowballRegen.setAmount(amount);

        return snowballRegen;
    }

    @Override
    public void reimburseItem(Player p, ItemStack item) {
        int count = -1;
        for (ItemStack itemStack : p.getInventory()) {
            if (itemStack != null && item != null && item.getType() == itemStack.getType() && item.getDurability() == itemStack.getDurability()) {
                count += itemStack.getAmount();
            }
        }

        if (item != null && item.getType() == getSnowball(item.getAmount()).getType() && count < snowballMaxAmount) {
            Inventory inv = p.getInventory();
            int amount = 0;
            ItemStack newItem = getSnowball(1);

            Integer finalSlot = null;
            for (Integer i = 0; i < inv.getSize(); i++)
                if (inv.getItem(i) != null)
                    if (inv.getItem(i).getType() == newItem.getType()) {
                        amount += inv.getItem(i).getAmount();
                        if (amount <= inv.getMaxStackSize())
                            finalSlot = i;
                    }
            if (finalSlot != null && amount > 0) {
                ItemStack invItem = inv.getItem(finalSlot);
                if (amount < snowballMaxAmount)
                    inv.setItem(finalSlot, new ItemStack(invItem.getType(), invItem.getAmount() + 1));
            } else
                p.getInventory().addItem(newItem);
        }
    }

    public void removeSummon(int seconds, Player p) {
        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> {
            for (Snowball snowball : snowballList)
                if (snowball.getCustomName().contains(p.getName())) {
                    snowball.remove();
                }
        }, seconds * 20);
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
                ChatColor.GRAY + "Right click your sword to become invisible.",
                ChatColor.GRAY + "Using snowballs while invisible",
                ChatColor.GRAY + "teleports you behind your enemy.",
                ChatColor.GRAY + "Hits from behind deal more damage when invisible."
        );
    }
}
