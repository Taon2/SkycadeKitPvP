package net.skycade.kitpvp.kit.kits.disabled;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitTribesman extends Kit {
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    private final List<UUID> tribesCd = new ArrayList<>();

    public KitTribesman(KitManager kitManager) {
        super(kitManager, "Tribesman", KitType.TRIBESMAN, 37000, false, getLore());

        helmet = new ItemBuilder(
                Material.IRON_HELMET)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Gains potion effects when taking heavy damage.").build();
        chestplate = new ItemBuilder(
                Material.GOLD_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Gains potion effects when taking heavy damage.").build();
        leggings = new ItemBuilder(
                Material.GOLD_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Gains potion effects when taking heavy damage.").build();
        boots = new ItemBuilder(
                Material.IRON_BOOTS)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Gains potion effects when taking heavy damage.").build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.KNOCKBACK, 1).build();

        constantEffects.put(PotionEffectType.JUMP, 0);
        constantEffects.put(PotionEffectType.DAMAGE_RESISTANCE, 0);
        constantEffects.put(PotionEffectType.REGENERATION, 0);

        ItemStack icon = new ItemStack(Material.WHEAT);
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
    public void onDamageGetHit(EntityDamageByEntityEvent event, Player damager, Player damagee) {
        if (tribesCd.contains(damagee.getUniqueId()))
            return;

        if (event.getFinalDamage() >= 4) {
            tribesEffect(damagee, 6 + 2 * 3);
            tribesCd.add(damagee.getUniqueId());
            Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> tribesCd.remove(damagee.getUniqueId()), 220 - (3 * 20));
        }
    }

    private void tribesEffect(Player p, int seconds) {
        for (PotionEffect effect : p.getActivePotionEffects())
            p.removePotionEffect(effect.getType());
        p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, seconds * 20, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, seconds * 20, 0));
        p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, seconds * 20, 0));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        KitPvPStats stats = KitPvP.getInstance().getStats(event.getPlayer());
        if (stats.getActiveKit() != KitType.TRIBESMAN) return;

        tribesCd.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Unobtainable.");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "A herbalist.",
                "",
                ChatColor.GRAY + "Gains potion effects when",
                ChatColor.GRAY + "taking a lot of damage."
        );
    }
}
