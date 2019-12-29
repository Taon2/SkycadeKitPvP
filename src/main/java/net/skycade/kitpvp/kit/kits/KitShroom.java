package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static net.skycade.kitpvp.Messages.YOURE_POISONED;

public class KitShroom extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack snowball;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    private int snowballCooldown = 7;
    private int snowballStartAmount = 6;
    private int snowballMaxAmount = 8;
    private int snowballRegenSpeed = 20;

    private List<Snowball> snowballList = new ArrayList<>();

    public KitShroom(KitManager kitManager) {
        super(kitManager, "Shroom", KitType.SHROOM, 32000, getLore());

        helmet = new ItemBuilder(
                Material.HUGE_MUSHROOM_2)
                .addEnchantment(Enchantment.THORNS, 1)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Moving makes people around you nauseous.").build();
        chestplate = new ItemBuilder(
                Material.IRON_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 9)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Moving makes people around you nauseous.").build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 9)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Moving makes people around you nauseous.")
                .setColour(Color.WHITE).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 9)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Moving makes people around you nauseous.")
                .setColour(Color.WHITE).build();
        weapon = new ItemBuilder(
                Material.STONE_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.DAMAGE_ALL, 1).build();
        snowball = new ItemBuilder(
                Material.SNOW_BALL, snowballStartAmount)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Throwing a snowball every " + snowballCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "gives your target poison.")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Regain 1 snowball every " + snowballRegenSpeed + " seconds.").build();

        constantEffects.put(PotionEffectType.JUMP, 1);

        ItemStack icon = new ItemStack(Material.HUGE_MUSHROOM_2);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(snowball);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        constantEffects.forEach((effect, amplifier) -> {
            p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
        });

        startItemRunnable(p, snowballRegenSpeed, getSnowball(1), snowballMaxAmount, KitType.SHROOM);
    }

    public void onSnowballUse(Player shooter, ProjectileLaunchEvent e) {
        e.getEntity().setCustomName(shooter.getName());
        e.getEntity().setCustomNameVisible(false);
        snowballList.add((Snowball) e.getEntity());

        e.getEntity().setVelocity(e.getEntity().getVelocity().multiply(2));
    }

    public void onSnowballHit(Player shooter, Player damagee) {
        if (!addCooldown(shooter, "Spore", snowballCooldown, true)) {
            return;
        }

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(shooter, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        YOURE_POISONED.msg(damagee, "%player%", shooter.getName());
        damagee.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 80, 4));
    }

    @Override
    public void onMove(Player p) {
        particleMoveEffect(p, ParticleEffect.TOWN_AURA, 2, 30);

        if (UtilPlayer.isMoving(p) && !getKitManager().getKitPvP().getSpawnRegion().contains(p)) {
            UtilPlayer.getNearbyPlayers(p, p.getLocation(), 2).forEach(target -> {
                target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 0));
            });
        }
    }

    private ItemStack getSnowball(int amount) {
        ItemStack snowballRegen = new ItemStack(snowball);
        snowballRegen.setAmount(amount);

        return snowballRegen;
    }

    @Override
    public void reimburseItem(Player p, ItemStack item) {
        if (item != null && item.getType() == getSnowball(item.getAmount()).getType()) {
            Inventory inv = p.getInventory();
            int amount = 0;
            ItemStack newItem = getSnowball(1);

            Integer finalSlot = null;
            for (Integer i = 0; i < inv.getSize(); i++)
                if (inv.getItem(i) != null)
                    if (inv.getItem(i).getType() == newItem.getType()) {
                        amount += inv.getItem(i).getAmount();
                        if (amount <= inv.getMaxStackSize())
                            finalSlot = i;
                    }
            if (finalSlot != null && amount > 0) {
                ItemStack invItem = inv.getItem(finalSlot);
                if (amount < snowballMaxAmount)
                    inv.setItem(finalSlot, new ItemStack(invItem.getType(), invItem.getAmount() + 1));
            } else
                p.getInventory().addItem(newItem);
        }
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "A fungus is among us!",
                "",
                ChatColor.GRAY + "Players near you feel nauseous when you move.",
                ChatColor.GRAY + "Throws snowballs to poison enemies."
        );
    }
}
