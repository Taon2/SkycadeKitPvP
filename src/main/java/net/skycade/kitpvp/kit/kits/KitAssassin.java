package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class KitAssassin extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    private double backstabMultiplier = 1.2;

    private final Map<UUID, Integer> comboMap = new HashMap<>();

    public KitAssassin(KitManager kitManager) {
        super(kitManager, "Assassin", KitType.ASSASSIN, 42000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 13)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .setColour(Color.BLACK).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 13)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .setColour(Color.BLACK).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 13)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .setColour(Color.BLACK).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 20)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .setColour(Color.RED).build();
        weapon = new ItemBuilder(
                Material.DIAMOND_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Hitting players from behind deals more damage.").build();

        constantEffects.put(PotionEffectType.SPEED, 0);

        ItemStack icon = new ItemStack(Material.COAL);
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
    public void onDamageDealHit(EntityDamageByEntityEvent event, Player damager, Player damagee) {
        if (isBackStab(damager, damagee))
            event.setDamage(event.getDamage() * backstabMultiplier);
        if (!comboMap.containsKey(damager.getUniqueId())) {
            comboMap.put(damager.getUniqueId(), 1);
            damager.setLevel(1);
            return;
        }
        int combo = comboMap.get(damager.getUniqueId()) + 1;
        comboMap.put(damager.getUniqueId(), combo);
        damager.setLevel(combo);
        if (combo >= 7) {
            damager.setLevel(0);
            comboMap.remove(damager.getUniqueId());
            teleportBehindPlayer(damager, damagee.getLocation());
        }
        if (damager.getItemInHand().getType() == Material.DIAMOND_SWORD)
            hitParticles(damager);
    }

    private void hitParticles(Player p) {
        double t = 0.0;
        Location loc = p.getLocation().add(0, 0.2, 0);
        Vector dir = p.getLocation().getDirection().normalize();
        while (t < 0.7) {
            t += 0.05F;
            double x = dir.getX() * t;
            double y = dir.getY() * t;
            double z = dir.getZ() * t;
            loc.add(x, y, z);

            for (int i = 0; i < 3; i++)
                ParticleEffect.SPELL_MOB.display(new ParticleEffect.OrdinaryColor(Color.RED), loc, 20);
            for (Entity entity : loc.getChunk().getEntities()) {
                if (entity.getLocation().distance(loc) < 0.5 && entity != p) {
                    t = 1;
                }
            }
        }
    }

    private boolean isBackStab(Player damager, Player damagee) {
        final double diffX = damager.getLocation().getDirection().getX() - damagee.getLocation().getDirection().getX();
        final double diffZ = damager.getLocation().getDirection().getZ() - damagee.getLocation().getDirection().getZ();
        if (diffX > 0 && diffX < 1 || diffX < 0 && diffX > -1) {
            return diffZ > 0 && diffZ < 1 || diffZ < 0 && diffZ > -1;
        }
        return false;
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Poof!",
                "",
                ChatColor.GRAY + "Hits from behind deal more damage."
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        KitPvPStats stats = KitPvP.getInstance().getStats(event.getPlayer());
        if (stats.getActiveKit() != KitType.ASSASSIN) return;

        comboMap.remove(event.getPlayer().getUniqueId());
    }
}
