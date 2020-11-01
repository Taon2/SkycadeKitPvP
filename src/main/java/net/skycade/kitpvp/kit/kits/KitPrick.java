package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.Messages;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class KitPrick extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack cactus;

    private int cactusCooldown = 11;

    private ArrayList<UUID> prickedPlayers = new ArrayList<>();

    public KitPrick(KitManager kitManager) {
        super(kitManager, "Prick", KitType.PRICK, 22000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 16)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addEnchantment(Enchantment.THORNS, 1)
                .setColour(Color.GREEN).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 16)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addEnchantment(Enchantment.THORNS, 1)
                .setColour(Color.GREEN).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 16)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addEnchantment(Enchantment.THORNS, 1)
                .setColour(Color.GREEN).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 16)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addEnchantment(Enchantment.THORNS, 1)
                .setColour(Color.GREEN).build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.KNOCKBACK, 1).build();
        cactus = new ItemBuilder(
                Material.CACTUS)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "%click% on a player every " + cactusCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "gives the target poison and damages their armor.").build();

        ItemStack icon = new ItemStack(Material.CACTUS);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(cactus);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);
    }

    @Override
    public void onInteract(Player p, Player target, ItemStack item) {
        if (item.getType() != Material.CACTUS)
            return;

        // If target is in the ArrayList, they cannot be pricked
        // - Negative
        if (prickedPlayers.contains(target.getUniqueId())) {
            Messages.PRICKED_RECENTLY.msg(p, "%player%", target.getName());
            return;
        }

        if (!addCooldown(p, "Prick", cactusCooldown, true))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        ItemStack[] armor = target.getEquipment().getArmorContents();
        target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 130, 1));

        // Adds them to the Pricked Players arraylist
        prickedPlayers.add(target.getUniqueId());

        // Removes them from the Pricked Players arraylist after 130 ticks
        new BukkitRunnable() {

            @Override
            public void run() {
                prickedPlayers.remove(target.getUniqueId());
            }
        }.runTaskLater(KitPvP.getInstance(), 130);

        for (ItemStack anArmor : armor)
            if (anArmor != null)
                anArmor.setDurability((short) (anArmor.getDurability() + 20));
        target.getInventory().setArmorContents(armor);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Just wants a hug.",
                "",
                ChatColor.GRAY + "Your cactus poisons enemies,",
                ChatColor.GRAY + "causing heavy durability damage."
        );
    }
}
