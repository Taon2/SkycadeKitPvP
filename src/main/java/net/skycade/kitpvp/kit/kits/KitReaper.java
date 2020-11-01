package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.KitPvP;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class KitReaper extends Kit {
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack abilityItem;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();
    private int abilityCooldown = 45;

    private int witherDuration = 8;
    private int speed2Duration = 8;
    private int regen1Duration = 8;

    public KitReaper(KitManager kitManager) {
        super(kitManager, "Reaper", KitType.REAPER, 30000, getLore());

        helmet = new ItemStack(Material.SKULL_ITEM, 1, (short) 1);
        ItemMeta helmetMeta = helmet.getItemMeta();
        helmetMeta.setDisplayName(ChatColor.GRAY + "Reaper's Skull");
        helmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
        helmet.setItemMeta(helmetMeta);

        chestplate = new ItemBuilder(
                Material.IRON_CHESTPLATE).addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .build();

        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS).addEnchantment(Enchantment.DURABILITY, 15)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .setColour(Color.BLACK)
                .build();

        boots = new ItemBuilder(
                Material.IRON_BOOTS).addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .build();

        weapon = new ItemBuilder(
                Material.DIAMOND_HOE).addEnchantment(Enchantment.DAMAGE_ALL, 7)
                .build();

        abilityItem = new ItemStack(Material.SKULL_ITEM, 1, (short) 1);
        ItemMeta abilityMeta = abilityItem.getItemMeta();
        abilityMeta.setDisplayName(ChatColor.DARK_GRAY + "Reaper's Wrath");
        List<String> abilityLore = new ArrayList<>();
        abilityLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Right-Click a player to infect them with");
        abilityLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Wither for 8 seconds and grant yourself");
        abilityLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Speed 2 and Regeneration for 8 seconds.");

        abilityMeta.setLore(abilityLore);
        abilityItem.setItemMeta(abilityMeta);

        constantEffects.put(PotionEffectType.SPEED, 0);

        ItemStack icon = new ItemStack(Material.SKULL_ITEM, 1, (short) 1);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(abilityItem);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        constantEffects.forEach((effect, amplifier) -> {
            p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
        });
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Die, die, die!!",
                "",
                ChatColor.GRAY + "%click% a player to infect them with",
                ChatColor.GRAY + "Wither for 8 seconds and grant yourself",
                ChatColor.GRAY + "Speed 2 and Regeneration for 8 seconds."
        );
    }

    @Override
    public void onInteract(Player p, Player target, ItemStack item) {
        if (item.getType() != Material.SKULL_ITEM)
            return;

        if (!item.getItemMeta().hasDisplayName()) // Checks if the ability item doesnt have a display name
            return;
        if (!item.getItemMeta().getDisplayName().equals(ChatColor.DARK_GRAY + "Reaper's Wrath")) // Checks if the ability item's name is not "Reaper's Wrath"
            return;

        if (!addCooldown(p, "Reaper's Wrath", abilityCooldown, true))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        p.removePotionEffect(PotionEffectType.SPEED);
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * speed2Duration, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * regen1Duration, 0));

        target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * witherDuration, 0));

        new BukkitRunnable() {

            @Override
            public void run() {
                constantEffects.forEach((effect, amplifier) -> {
                    p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
                });
            }
        }.runTaskLater(KitPvP.getInstance(), 20 * 8 + 1);
    }
}
