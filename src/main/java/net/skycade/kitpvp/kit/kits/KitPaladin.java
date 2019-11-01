package net.skycade.kitpvp.kit.kits;

import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class KitPaladin extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private int lightCooldown = 25;

    public KitPaladin(KitManager kitManager) {
        super(kitManager, "Paladin", KitType.PALADIN, 42000, getLore());

        helmet = new ItemBuilder(
                Material.IRON_HELMET).build();
        chestplate = new ItemBuilder(
                Material.GOLD_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 7)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build();
        leggings = new ItemBuilder(
                Material.IRON_LEGGINGS).build();
        boots = new ItemBuilder(
                Material.IRON_BOOTS).build();
        weapon = new ItemBuilder(
                Material.GOLD_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.DAMAGE_UNDEAD, 4)
                .addEnchantment(Enchantment.DAMAGE_ALL, 1)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Right clicking every " + lightCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "grants nearby gang members absorbtion.").build();

        ItemStack icon = new ItemStack(Material.GLOWSTONE_DUST);
        setIcon(icon);
    }

    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.GOLD_SWORD)
            return;
        if (!addCooldown(p, getName(), lightCooldown, true))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        Set<Player> targetPlayers = UtilPlayer.getNearbyPlayers(p.getLocation(), 7);

        Gang gang = GangsPlusApi.getPlayersGang(p);

        targetPlayers.forEach(target -> {
            if (gang.getOnlineMembers().contains(target))
                target.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 200, 1));
        });
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
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.GREEN + "" + ChatColor.BOLD + "Support Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "May the light be with you.",
                "",
                ChatColor.GRAY + "Right clicking with your sword",
                ChatColor.GRAY + "grants protection to nearby allies."
        );
    }
}
