package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
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

import java.util.*;

import static net.skycade.kitpvp.Messages.CAT;
import static net.skycade.kitpvp.Messages.FROZEN_ALREADY;

public class KitMystic extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private int catCooldown = 4;

    public KitMystic(KitManager kitManager) {
        super(kitManager, "Mystic", KitType.MYSTIC, 31000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.PURPLE).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.PURPLE).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.PURPLE).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.PURPLE).build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.DAMAGE_ALL, 1)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Shift + Right clicking every " + catCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "throws cats that grant potion effects.").build();

        ItemStack icon = new ItemStack(Material.STICK);
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
        cat.setCustomName(p.getName());

        for (Entity ent : loc.getChunk().getEntities())
            if (ent.getType() == EntityType.OCELOT)
                if (!((Ocelot) ent).isAdult())
                    ent.remove();

        cat.setVelocity(loc.getDirection().multiply(1D));
        p.getWorld().playSound(loc, Sound.CAT_MEOW, 1F, 1F);

        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> {
            Set<Player> targetPlayers = UtilPlayer.getNearbyPlayers(p, cat.getLocation(), 4);

            targetPlayers.forEach(target -> {
                mysticEffects(target, p, 7, 7, 22, 22, 22, 12, 14);
            });

            cat.getLocation().getWorld().createExplosion(cat.getLocation(), 0);
            cat.remove();
        }, 15);
    }

    private void mysticEffects(Player target, Player p, int speedPer, int regPer, int slowPer, int weakPer, int poisPer, int blindPer, int freezePer) {
        int percentage = UtilMath.getRandom(0, 100);
        if (percentage <= speedPer) {
            onCatHit(target, p, ChatColor.WHITE + "SPEED UP!", PotionEffectType.SPEED, 160, 1);
        } else if (percentage <= speedPer + regPer) {
            onCatHit(target, p, ChatColor.RED + "REGENERATION!", PotionEffectType.REGENERATION, 100, 1);
        } else if (percentage <= speedPer + regPer + slowPer) {
            onCatHit(target, p, ChatColor.GRAY + "SLOWNESS!", PotionEffectType.SLOW, 160, 0);
        } else if (percentage <= speedPer + regPer + slowPer + weakPer) {
            onCatHit(target, p, ChatColor.RED + "WEAKNESS!", PotionEffectType.WEAKNESS, 200, 0);
        } else if (percentage <= speedPer + regPer + slowPer + weakPer + poisPer) {
            onCatHit(target, p, ChatColor.DARK_GREEN + "POISON", PotionEffectType.POISON, 140, 0);
        } else if (percentage <= speedPer + regPer + slowPer + weakPer + poisPer + blindPer) {
            onCatHit(target, p, ChatColor.BLACK + "BLINDNESS", PotionEffectType.BLINDNESS, 140, 0);
        } else {
            if (getFrozenImmunity().contains(target.getUniqueId())) {
                FROZEN_ALREADY.msg(p, "%player%", target.getName());
                return;
            }

            freezePlayer(target, 5);
            CAT.msg(target, "%effect%", ChatColor.AQUA + "FROZEN!");
            CAT.msg(p, "%effect%", ChatColor.AQUA + "FREEZE!");
        }
    }

    private void onCatHit(Player target, Player p, String playerMsg, PotionEffectType effect, int duration, int amplifier) {
        CAT.msg(target, "%effect%", playerMsg);
        CAT.msg(p, "%effect%", playerMsg);

        target.addPotionEffect(new PotionEffect(effect, duration, amplifier));
    }

    @EventHandler
    public void onCatHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player))
            return;
        if (e.getEntity() instanceof Player)
            return;
        if (e.getEntity().getCustomName() != null) {
            if (e.getEntity().getCustomName().contains("Mystic cat")) {
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
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Meow!",
                "",
                ChatColor.GRAY + "Throws cats that explode and",
                ChatColor.GRAY + "give enemies potion effects."
        );
    }
}
