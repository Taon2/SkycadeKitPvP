package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.events.CaptureTheFlagEvent;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.stat.KitPvPStats;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.skycade.kitpvp.Messages.YOURE_FROZEN;

public class KitFrosty extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack snowball;

    private int snowballCooldown = 5;
    private int snowballStartAmount = 6;
    private int snowballMaxAmount = 8;
    private int snowballRegenSpeed = 20;

    private List<Snowball> snowballList = new ArrayList<>();

    public KitFrosty(KitManager kitManager) {
        super(kitManager, "Frosty", KitType.FROSTY, 20000, getLore());

        helmet = new ItemBuilder(
                Material.JACK_O_LANTERN)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Takes extra damage from lava and fire.").build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 9)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Takes extra damage from lava and fire.")
                .setColour(Color.fromRGB(200, 255, 255)).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 9)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Takes extra damage from lava and fire.")
                .setColour(Color.fromRGB(200, 255, 255)).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 9)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Takes extra damage from lava and fire.")
                .setColour(Color.fromRGB(200, 255, 255)).build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5).build();
        snowball = new ItemBuilder(
                Material.SNOW_BALL, snowballStartAmount)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Throwing a snowball every " + snowballCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "freezes the target in place.")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Regain 1 snowball every " + snowballRegenSpeed + " seconds.").build();

        ItemStack icon = new ItemStack(Material.SNOW_BALL);
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

        startItemRunnable(p, snowballRegenSpeed, getSnowball(1), snowballMaxAmount, KitType.FROSTY);
    }

    public void onSnowballUse(Player shooter, ProjectileLaunchEvent e) {
        e.getEntity().setCustomName(shooter.getName());
        e.getEntity().setCustomNameVisible(false);
        snowballList.add((Snowball) e.getEntity());

        e.getEntity().setVelocity(e.getEntity().getVelocity().multiply(2));
    }

    public void onSnowballHit(Player shooter, Player damagee) {
        if (CaptureTheFlagEvent.getInstance().getBegin() != null && CaptureTheFlagEvent.getInstance().isTeamRed(shooter) == CaptureTheFlagEvent.getInstance().isTeamRed(damagee)) {
            return;
        }

        if (!addCooldown(shooter, "Frosty", snowballCooldown, true)) {
            return;
        }

        KitPvPStats stats = KitPvP.getInstance().getStats(shooter);

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(shooter, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        if (!frozenPlayers.containsKey(damagee.getUniqueId())) {
            YOURE_FROZEN.msg(damagee, "%player%", shooter.getName(), "%kit%", stats.getActiveKit().getKit().getName());
            freezePlayer(damagee, 5);
        }
    }

    @Override
    public void onMove(Player p) {
        if (p.getLocation().getBlock().getType() == Material.STATIONARY_LAVA || p.getLocation().getBlock().getType() == Material.LAVA || p.getLocation().getBlock().getType() == Material.FIRE) {
            p.damage(1);
        }
    }

    private ItemStack getSnowball(int amount) {
        ItemStack snowballRegen = new ItemStack(snowball);
        snowballRegen.setAmount(amount);

        return snowballRegen;
    }

    @Override
    public void reimburseItem(Player p, ItemStack item) {
        int count = -1;
        for (ItemStack itemStack : p.getInventory()) {
            if (itemStack != null && item != null && item.getType() == itemStack.getType() && item.getDurability() == itemStack.getDurability()) {
                count += itemStack.getAmount();
            }
        }

        if (item != null && item.getType() == getSnowball(item.getAmount()).getType() && count < snowballMaxAmount) {
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

    public void removeSummon(int seconds, Player p) {
        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> {
            for (Snowball snowball : snowballList)
                if (snowball.getCustomName().contains(p.getName())) {
                    snowball.remove();
                }
        }, seconds * 20);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "I'm melting!",
                "",
                ChatColor.GRAY + "Snowballs freeze players in place.",
                ChatColor.GRAY + "Takes extra damage from lava and fire."
        );
    }
}
