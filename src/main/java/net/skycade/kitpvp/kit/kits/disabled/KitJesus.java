package net.skycade.kitpvp.kit.kits.disabled;

import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.skycade.kitpvp.Messages.HEALED;

public class KitJesus extends Kit {

    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack book;

    private int cleanseCooldown = 25;

    public KitJesus(KitManager kitManager) {
        super(kitManager, "Jesus", KitType.JESUS, 20000, false, getLore());

        chestplate = new ItemBuilder(
                Material.DIAMOND_CHESTPLATE)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Gains speed in water.").build();
        leggings = new ItemBuilder(
                Material.DIAMOND_LEGGINGS)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Gains speed in water.").build();
        boots = new ItemBuilder(
                Material.DIAMOND_BOOTS)
                .addEnchantment(Enchantment.DEPTH_STRIDER, 1)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Gains speed in water.").build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "%click% every " + cleanseCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "grants your nearby gang members regeneration.").build();
        book = new ItemStack(
                Material.ENCHANTED_BOOK);

        ItemStack icon = new ItemStack(Material.ENCHANTED_BOOK);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(book);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);
    }

    @Override
    public void onMove(Player p) {
        if (p.getLocation().getBlock().getType() == Material.WATER || p.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
            if (!p.hasPotionEffect(PotionEffectType.SPEED))
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * (3 * 3), 1));
        }
        particleTracerEffect(p, Color.BLUE, 10);
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.ENCHANTED_BOOK)
            return;
        if (!addCooldown(p, "Cleanse Sins", cleanseCooldown, true))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        Gang gang = GangsPlusApi.getPlayersGang(p);

        if (gang == null) {
            p.addPotionEffects(Collections.singletonList(new PotionEffect(PotionEffectType.REGENERATION, 60, 1)));
            HEALED.msg(p);
        } else {
            gang.getOnlineMembers().forEach(member -> {
                member.addPotionEffects(Collections.singletonList(new PotionEffect(PotionEffectType.REGENERATION, 60, 1)));
                HEALED.msg(member);
            });
        }
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.GREEN + "" + ChatColor.BOLD + "Support Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Respawns in 3 days. Just kidding.",
                "",
                ChatColor.GRAY + "Fast in water and",
                ChatColor.GRAY + "heals your friends."
        );
    }
}
