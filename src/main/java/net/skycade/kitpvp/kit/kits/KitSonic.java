package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitSonic extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private final HashMap<UUID, Integer> sprinting = new HashMap<>();

    public KitSonic(KitManager kitManager) {
        super(kitManager, "Sonic", KitType.SONIC, 28000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Moving builds up your speed.")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Loses speed when hit.")
                .setColour(Color.BLUE).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Moving builds up your speed.")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Loses speed when hit.")
                .setColour(Color.BLUE).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Moving builds up your speed.")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Loses speed when hit.")
                .setColour(Color.BLUE).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Moving builds up your speed.")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Loses speed when hit.")
                .setColour(Color.RED).build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.DAMAGE_ALL, 1).build();

        ItemStack icon = new ItemStack(Material.WOOL);
        icon.setDurability((short) 11);
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
    public void onMove(Player p) {
        if (p.isSprinting()) {
            if (!sprinting.containsKey(p.getUniqueId()))
                sprinting.put(p.getUniqueId(), 1);
            else {
                int value = sprinting.get(p.getUniqueId());
                value++;
                sonicSprint(p, value, Arrays.asList(1, 5, 10, 15));
                sprinting.put(p.getUniqueId(), value);
            }
        }
    }

    public void disableSprint(Player p){
        if (p.hasPotionEffect(PotionEffectType.SPEED))
            p.removePotionEffect(PotionEffectType.SPEED);
        sprinting.remove(p.getUniqueId());
    }

    private void sonicSprint(Player p, int value, List<Integer> values) {
        if (values.contains(value)) {
            if (p.hasPotionEffect(PotionEffectType.SPEED))
                p.removePotionEffect(PotionEffectType.SPEED);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, values.indexOf(value)));
            if (values.indexOf(value) == values.size() - 1) {
                p.getWorld().playEffect(p.getLocation(), Effect.POTION_BREAK, 1);
                p.getWorld().playSound(p.getLocation(), Sound.WOLF_SHAKE, 1, 1);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        KitPvPStats stats = KitPvP.getInstance().getStats(event.getPlayer());
        if (stats.getActiveKit() != KitType.SONIC) return;

        UUID uuid = event.getPlayer().getUniqueId();
        sprinting.remove(uuid);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Gotta go fast!",
                "",
                ChatColor.GRAY + "Gains speed while out of combat,",
                ChatColor.GRAY + "increasing the longer you sprint.",
                ChatColor.GRAY + "Loses speed when hit."
        );
    }
}
