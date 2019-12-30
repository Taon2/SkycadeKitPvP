package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.skycade.kitpvp.Messages.YOURE_FROZEN;

public class KitDualBlader extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack weapon2;

    public KitDualBlader(KitManager kitManager) {
        super(kitManager, "DualBlader", KitType.DUALBLADER, 34000, getLore());

        helmet = new ItemBuilder(
                Material.IRON_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 1).build();
        chestplate = new ItemBuilder(
                Material.IRON_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 1).build();
        leggings = new ItemBuilder(
                Material.IRON_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 1).build();
        boots = new ItemBuilder(
                Material.IRON_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 1).build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .setName(ChatColor.RED + "Sword of Fire")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Sets your enemies aflame.").build();
        weapon2 = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .setName(ChatColor.AQUA + "Sword of Frost")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Freezes your enemies in place.").build();

        ItemStack icon = new ItemStack(Material.PACKED_ICE);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(weapon2);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);
    }

    @Override
    public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
        if (!Arrays.asList(Material.DIAMOND_SWORD, Material.IRON_SWORD).contains(damager.getItemInHand().getType()))
            return;

        if (damager.getItemInHand().getItemMeta().getDisplayName().contains("Fire")) {
            fireFreezeCalc(damager, damagee, 25, 7, 0);
        } else if (damager.getItemInHand().getItemMeta().getDisplayName().contains("Frost")) {
            fireFreezeCalc(damager, damagee, 0, 0, 10);
        }
    }

    private void fireFreezeCalc(Player damager, Player damagee, int firechance, int firedur, int freezechance) {
        KitPvPStats stats = KitPvP.getInstance().getStats(damager);
        int random = UtilMath.getRandom(0, 100);

        if (random <= firechance)
            damagee.setFireTicks(firedur * 20);
        else if (random <= firechance + freezechance && !frozenPlayers.containsKey(damagee.getUniqueId())) {
            YOURE_FROZEN.msg(damagee, "%player%", damager.getName(), "%kit%", stats.getActiveKit().getKit().getName());
            freezePlayer(damagee, 5);
        }
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Master of the elements.",
                "",
                ChatColor.GRAY + "Freeze and burn your enemies."
        );
    }
}