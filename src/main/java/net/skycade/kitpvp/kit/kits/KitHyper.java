package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KitHyper extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack sugar;

    private int sugarCooldown = 25;

    public KitHyper(KitManager kitManager) {
        super(kitManager, "Hyper", KitType.HYPER, 24000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .setColour(Color.BLACK).build();
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
                .addEnchantment(Enchantment.DURABILITY, 5).build();
        sugar = new ItemBuilder(Material.SUGAR)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Shift + Right clicking every " + sugarCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "grants you speed and regeneration.").build();

        ItemStack icon = new ItemStack(Material.SUGAR);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(sugar);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.SUGAR)
            return;
        if (!addCooldown(p, "Sugar Rush", sugarCooldown, true))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        p.addPotionEffects(Arrays.asList(new PotionEffect(PotionEffectType.SPEED, 350, 2), new PotionEffect(PotionEffectType.REGENERATION, 350, 1)));
        p.getWorld().playEffect(p.getLocation(), Effect.ENDER_SIGNAL, 0);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "I am speed!",
                "",
                ChatColor.GRAY + "Sugar gives you speed",
                ChatColor.GRAY + "and regeneration."
        );
    }
}
