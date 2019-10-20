package net.skycade.kitpvp.kit.kits;

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
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KitKangaroo extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private double leapYVelocity = 0.07;

    public KitKangaroo(KitManager kitManager) {
        super(kitManager, "Kangaroo", KitType.KANGAROO, 35000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .setColour(Color.SILVER).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .setColour(Color.SILVER).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .setColour(Color.SILVER).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .setColour(Color.SILVER).build();
        weapon = new ItemBuilder(
                Material.GOLD_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.DAMAGE_ALL, 2).build();

        ItemStack icon = new ItemStack(Material.LEATHER_BOOTS);
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
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.GOLD_SWORD)
            return;
        if (!addCooldown(p, getName(), 6, true))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        p.setVelocity(new Vector(p.getLocation().getDirection().getX(), leapYVelocity, p.getLocation().getDirection().getZ()).multiply(4));
        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> p.setVelocity(new Vector(p.getLocation().getDirection().getX(), 0.15, p.getLocation().getDirection().getZ()).multiply(3)), 3);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "From down under.",
                "",
                ChatColor.GRAY + "Right clicking gives you a mega jump."
        );
    }
}
