package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static java.lang.Integer.parseInt;
import static net.skycade.kitpvp.Messages.BLEED_ACTIVATED;
import static net.skycade.kitpvp.Messages.BLEED_DEACTIVATED;

public class KitHuntsman extends Kit implements Listener {

    private final List<UUID> huntsmanActiveBleed = new ArrayList<>();
    private final List<UUID> bleeding = new ArrayList<>();

    public KitHuntsman(KitManager kitManager) {
        super(kitManager, "Huntsman", KitType.HUNTSMAN, 40000, "Hunt them down!");

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "SKULL_ITEM");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 40000);

        defaultsMap.put("inventory.sword.material", "STONE_SWORD");
        defaultsMap.put("inventory.sword.enchantments.durability", 5);
        defaultsMap.put("inventory.sword.enchantments.knockback", 1);
        defaultsMap.put("inventory.sword.enchantments.damage-all", 2);

        defaultsMap.put("armor.material", "IRON");
        defaultsMap.put("armor.enchantments.durability", 0);
        defaultsMap.put("armor.enchantments.protection", 0);

        defaultsMap.put("potions.pot1", "JUMP:0");

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
    public void applyKit(Player p, int level) {
        p.getInventory().addItem(new ItemBuilder(
                Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
                .addEnchantment(Enchantment.KNOCKBACK, getConfig().getInt("inventory.sword.enchantments.knockback"))
                .addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

        p.getInventory().setArmorContents(getArmour(
                Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
                getConfig().getInt("armor.enchantments.durability"),
                getConfig().getInt("armor.enchantments.protection")));

        String[] pot1 = getConfig().getString("potions.pot1").split(":");
        p.addPotionEffect(new PotionEffect(
                PotionEffectType.getByName(pot1[0]),
                Integer.MAX_VALUE,
                parseInt(pot1[1])));
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.IRON_SWORD && item.getType() != Material.STONE_SWORD)
            return;
        if (!addCooldown(p, getName(), 20, true))
            return;
        BLEED_ACTIVATED.msg(p);
        huntsmanActiveBleed.add(p.getUniqueId());
        Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> {
            huntsmanActiveBleed.remove(p.getUniqueId());
            BLEED_DEACTIVATED.msg(p);
        }, 7 * 20);
    }

    @Override
    public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
        if (!huntsmanActiveBleed.contains(damager.getUniqueId()))
            return;
        if (bleeding.contains(damagee.getUniqueId()))
            return;
        startBleed(damager, (Player) e.getEntity(), 4);
        bleeding.add(damagee.getUniqueId());
    }

    @SuppressWarnings("deprecation")
    private void startBleed(Player huntsman, Player p, int seconds) {
        ParticleEffect.REDSTONE.display(0.3F, 0.3F, 0.3F, 0, 5, p.getEyeLocation(), 40);
        if (seconds > 0)
            Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> {
                if (getKitManager().getKitPvP().getSpawnRegion().contains(p))
                    return;
                p.setLastDamageCause(new EntityDamageByEntityEvent(huntsman, p, DamageCause.ENTITY_ATTACK, 4));
                p.damage(4);
                startBleed(huntsman, p, seconds - 1);
            }, 20);
        else
            bleeding.remove(p.getUniqueId());
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        huntsmanActiveBleed.remove(e.getPlayer().getUniqueId());
        bleeding.remove(e.getPlayer().getUniqueId());
    }

    @Override
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7You can use your sword to give", "§7players a bleed effect");
    }

}
