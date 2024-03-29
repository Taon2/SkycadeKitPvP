package net.skycade.kitpvp.kit.kits.disabled;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import static net.skycade.kitpvp.Messages.BLEED_ACTIVATED;
import static net.skycade.kitpvp.Messages.BLEED_DEACTIVATED;

public class KitHuntsman extends Kit implements Listener {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    private int bleedCooldown = 20;

    private final List<UUID> huntsmanActiveBleed = new ArrayList<>();
    private final List<UUID> bleeding = new ArrayList<>();

    public KitHuntsman(KitManager kitManager) {
        super(kitManager, "Huntsman", KitType.HUNTSMAN, 40000, false, getLore());

        helmet = new ItemBuilder(
                Material.IRON_HELMET).build();
        chestplate = new ItemBuilder(
                Material.IRON_CHESTPLATE).build();
        leggings = new ItemBuilder(
                Material.IRON_LEGGINGS).build();
        boots = new ItemBuilder(
                Material.IRON_BOOTS).build();
        weapon = new ItemBuilder(
                Material.STONE_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.DAMAGE_ALL, 2)
                .addEnchantment(Enchantment.KNOCKBACK, 1)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "%click% every " + bleedCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "lets your attacks make players bleed.").build();

        constantEffects.put(PotionEffectType.JUMP, 0);

        ItemStack icon = new ItemStack(Material.SKULL_ITEM);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        constantEffects.forEach((effect, amplifier) -> {
            p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
        });
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.IRON_SWORD && item.getType() != Material.STONE_SWORD)
            return;
        if (!addCooldown(p, "Blood Frenzy", bleedCooldown, true))
            return;
        BLEED_ACTIVATED.msg(p);
        huntsmanActiveBleed.add(p.getUniqueId());
        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> {
            huntsmanActiveBleed.remove(p.getUniqueId());
            BLEED_DEACTIVATED.msg(p);
        }, 7 * 20);
    }

    @Override
    public void onDamageDealHit(EntityDamageByEntityEvent event, Player damager, Player damagee) {
        if (!huntsmanActiveBleed.contains(damager.getUniqueId()))
            return;
        if (bleeding.contains(damagee.getUniqueId()))
            return;
        startBleed(damager, (Player) event.getEntity(), 4);
        bleeding.add(damagee.getUniqueId());
    }

    @SuppressWarnings("deprecation")
    private void startBleed(Player huntsman, Player p, int seconds) {
        ParticleEffect.REDSTONE.display(0.3F, 0.3F, 0.3F, 0, 5, p.getEyeLocation(), 40);
        if (seconds > 0)
            Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> {
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
    public void onPlayerQuit(PlayerQuitEvent event) {
        KitPvPStats stats = KitPvP.getInstance().getStats(event.getPlayer());
        if (stats.getActiveKit() != KitType.HUNTSMAN) return;

        huntsmanActiveBleed.remove(event.getPlayer().getUniqueId());
        bleeding.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Unobtainable.");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "A bloody mess.",
                "",
                ChatColor.GRAY + "Makes players bleed."
        );
    }
}
