package net.skycade.kitpvp.kit.kits.disabled;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class KitBomber extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack tnt;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    private int tntCooldown = 8;
    private int tntStartAmount = 10;
    private int tntMaxAmount = 10;
    private int tntRegenSpeed = 15;

    public KitBomber(KitManager kitManager) {
        super(kitManager, "Bomber", KitType.BOMBER, 0, false, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 13)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 5)
                .setColour(Color.WHITE).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 13)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 5)
                .setColour(Color.RED).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 13)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 5)
                .setColour(Color.RED).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 13)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 5)
                .setColour(Color.RED).build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5).build();
        tnt = new ItemBuilder(
                Material.TNT, tntStartAmount)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "%click% every " + tntCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "throws 1 tnt.")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Regain 1 tnt every " + tntRegenSpeed + " seconds.").build();

        constantEffects.put(PotionEffectType.SPEED, 1);

        ItemStack icon = new ItemStack(Material.TNT);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(tnt);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        constantEffects.forEach((effect, amplifier) -> {
            p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
        });

        startItemRunnable(p, tntRegenSpeed, getTnt(1), tntMaxAmount, KitType.BOMBER);
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.TNT)
            return;
        if (!addCooldown(p, getName(), tntCooldown, true)) return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        Location loc = p.getEyeLocation();
        TNTPrimed tnt = (TNTPrimed) loc.getWorld().spawnEntity(loc.add(loc.getDirection()), EntityType.PRIMED_TNT);
        tnt.setVelocity(loc.getDirection().multiply(1D));
        tnt.setCustomName(p.getName());
        tnt.setFuseTicks(30);

        p.getWorld().playEffect(p.getLocation(), Effect.CLICK1, 1);
        if (p.getInventory().getItemInHand().getAmount() - 1 >= 1)
            p.getInventory().setItemInHand(getTnt(p.getInventory().getItemInHand().getAmount() - 1));
        else
            p.getInventory().remove(p.getItemInHand());

        // Adding Speed 3 after the Bomb has been ejected
        // After 4 seconds (and an extra tick), grants Speed 2 back to the player.
        // - Negative
        p.removePotionEffect(PotionEffectType.SPEED);
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 4, 2));

        new BukkitRunnable() {

            @Override
            public void run() {
                constantEffects.forEach((effect, amplifier) -> {
                    p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
                });
            }
        }.runTaskLater(KitPvP.getInstance(), 20 * 4 + 1);
    }

    @EventHandler
    public void onExplosion(ExplosionPrimeEvent event) {
        if (!(event.getEntity() instanceof TNTPrimed)) return;

        if (isBombThrowerOnline((TNTPrimed) event.getEntity())) {
            TNTPrimed tnt = (TNTPrimed) event.getEntity();

            Player shooter = Bukkit.getPlayer(tnt.getCustomName());
            Kit kit = KitPvP.getInstance().getStats(shooter).getActiveKit().getKit();

            if (kit.getKitType() == KitType.BOMBER) {
                // 4 is the default radius
                // - Negative
                event.setRadius(4 + 2);
            }
        }
    }

    @Override
    public boolean onDeath(Player died, Player killer) {
        died.getLocation().getWorld().createExplosion(died.getLocation().getBlockX(), died.getLocation().getBlockY(), died.getLocation().getBlockZ(), 3F, false, false);
        return true;
    }

    private boolean isBombThrowerOnline(TNTPrimed tnt) {
        if (tnt.getCustomName() == null) return false;

        String playerNameString = tnt.getCustomName();

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getName().equals(playerNameString)) {
                return true;
            }
        }
        return false;
    }

    private ItemStack getTnt(int amount) {
        ItemStack tntRegen = new ItemStack(tnt);
        tntRegen.setAmount(amount);

        return tntRegen;
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Bombs away!",
                "",
                ChatColor.GRAY + "Toss bombs at your enemies."
        );
    }
}
