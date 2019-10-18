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

public class KitMystic extends Kit {

    public KitMystic(KitManager kitManager) {
        super(kitManager, "Mystic", KitType.MYSTIC, 31000, "Cats can be good creatures");

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "STICK");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 31000);

        defaultsMap.put("inventory.sword.material", "IRON_SWORD");
        defaultsMap.put("inventory.sword.enchantments.durability", 5);
        defaultsMap.put("inventory.sword.enchantments.damage-all", 1);

        defaultsMap.put("armor.material", "LEATHER");
        defaultsMap.put("armor.enchantments.durability", 12);
        defaultsMap.put("armor.enchantments.protection", 4);

        setConfigDefaults(defaultsMap);

        if (getConfig().getString("kit.icon.material") != null) {
            if (getConfig().getString("kit.icon.material").contains("LEATHER")) {
                setIcon(new ItemBuilder(Material.getMaterial(getConfig().getString("kit.icon.material").toUpperCase()))
                        .setColour(getColor(getConfig().getString("kit.icon.color"))).build());
            } else {
                setIcon(new ItemStack(Material.getMaterial(getConfig().getString("kit.icon.material").toUpperCase())));
            }
        } else {
            setIcon(new ItemStack(Material.DIRT));
        }
        setPrice(getConfig().getInt("kit.price"));
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(new ItemBuilder(
                Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
                .addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

        p.getInventory().setArmorContents(getArmour(
                Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
                getConfig().getInt("armor.enchantments.durability"),
                getConfig().getInt("armor.enchantments.protection"),
                Color.PURPLE));
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.IRON_SWORD)
            return;
        if (!addCooldown(p, getName(), 3, true))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        Location loc = p.getEyeLocation();
        LivingEntity cat = (LivingEntity) p.getWorld().spawnEntity(loc.add(loc.getDirection()),
                EntityType.OCELOT);
        cat.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 99));
        cat.setCustomName("Mystic cat");

        for (Entity ent : loc.getChunk().getEntities())
            if (ent.getType() == EntityType.OCELOT)
                if (!((Ocelot) ent).isAdult())
                    ent.remove();

        cat.setVelocity(loc.getDirection().multiply(1D));
        p.getWorld().playSound(loc, Sound.CAT_MEOW, 0, 0);

        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> {
            Set<Player> targetPlayers = UtilPlayer.getNearbyPlayers(cat.getLocation(), 4);
            targetPlayers.remove(p);

            cat.getLocation().getWorld().createExplosion(cat.getLocation(), 0);
            cat.remove();
        }, 15);
    }

    private void mysticEffects(Player target, Player p, int speedPer, int regPer, int slowPer, int weakPer, int poisPer, int blindPer, int freezePer) {
        int percentage = UtilMath.getRandom(0, 100);
        if (percentage <= speedPer) {
            onCatHit(target, p, "&fSPEED UP!", PotionEffectType.SPEED, 160, 1);
        } else if (percentage <= speedPer + regPer) {
            onCatHit(target, p, "&cREGENERATION!", PotionEffectType.REGENERATION, 100, 1);
        } else if (percentage <= speedPer + regPer + slowPer) {
            onCatHit(target, p, "&7SLOWNESS!", PotionEffectType.SLOW, 160, 0);
        } else if (percentage <= speedPer + regPer + slowPer + weakPer) {
            onCatHit(target, p, "&cWEAKNESS!", PotionEffectType.WEAKNESS, 200, 0);
        } else if (percentage <= speedPer + regPer + slowPer + weakPer + poisPer) {
            onCatHit(target, p, "&2POISON", PotionEffectType.POISON, 140, 0);
        } else if (percentage <= speedPer + regPer + slowPer + weakPer + poisPer + blindPer) {
            onCatHit(target, p, "&0BLINDNESS", PotionEffectType.BLINDNESS, 140, 0);
        } else  {
            freezePlayer(target, 5);
            CAT.msg(target, "%effect%", "&bFROZEN!");
            CAT.msg(p, "%effect%", "&bFREEZE!");
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
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7Use your sword to throw a cat", "§7the cat can have different effects");
    }

}
