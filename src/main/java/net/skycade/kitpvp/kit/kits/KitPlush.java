package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class KitPlush extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private int catCooldown = 5;

    private double jumpPower = 1.0;

    public KitPlush(KitManager kitManager) {
        super(kitManager, "Plush", KitType.PLUSH, 24000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .setColour(Color.fromRGB(200, 255, 255)).build();
        chestplate = new ItemBuilder(
                Material.IRON_CHESTPLATE).build();
        leggings = new ItemBuilder(
                Material.IRON_LEGGINGS).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .setColour(Color.fromRGB(200, 255, 255)).build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.DAMAGE_ALL, 1)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Right clicking every " + catCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "throws cats that grant potion effects.").build();

        ItemStack icon = new ItemStack(Material.RAW_FISH);
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
        if (item.getType() != Material.IRON_SWORD)
            return;
        if (!addCooldown(p, getName(), catCooldown, true))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        Location loc = p.getEyeLocation();
        LivingEntity cat = (LivingEntity) p.getWorld().spawnEntity(loc.add(loc.getDirection()), EntityType.OCELOT);
        cat.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 99));

        for (Entity ent : loc.getChunk().getEntities())
            if (ent.getType() == EntityType.OCELOT)
                if (!((Ocelot) ent).isAdult())
                    ent.remove();

        cat.setVelocity(loc.getDirection().multiply(0.5D));
        cat.setCustomName(p.getName());
        p.getWorld().playSound(loc, Sound.CAT_MEOW, 1F, 1F);

        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> {
            Set<Player> targetPlayers = UtilPlayer.getNearbyPlayers(p, cat.getLocation(), 3.5);
            targetPlayers.add(p);
            targetPlayers.forEach(target -> {
                target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 3));
                target.setVelocity(new Vector(0, jumpPower, 0));
            });

            cat.getLocation().getWorld().createExplosion(cat.getLocation(), 0);
            cat.remove();
        }, 10);
    }

    @EventHandler
    public void onCatHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player))
            return;
        if (e.getEntity() instanceof Player)
            return;
        if (e.getEntity().getCustomName() != null) {
            if (e.getEntity().getCustomName().contains("Plush cat")) {
                e.setCancelled(true);
            }
        }
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "It's so fluffy I'm gonna die!",
                "",
                ChatColor.GRAY + "Throws cats that launch",
                ChatColor.GRAY + "players into oblivion."
        );
    }
}
