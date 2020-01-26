package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KitTeleporter extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack pearls;

    private int pearlRegenSpeed = 20;
    private int pearlStartAmount = 5;
    private int pearlMaxAmount = 8;

    private List<EnderPearl> pearlList = new ArrayList<>();

    public KitTeleporter(KitManager kitManager) {
        super(kitManager, "Teleporter", KitType.TELEPORTER, 32000, getLore());

        helmet = new ItemBuilder(
                Material.IRON_HELMET).build();
        chestplate = new ItemBuilder(
                Material.CHAINMAIL_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 8)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        leggings = new ItemBuilder(
                Material.CHAINMAIL_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 8)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        boots = new ItemBuilder(
                Material.IRON_BOOTS).build();
        weapon = new ItemBuilder(
                Material.DIAMOND_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5).build();
        pearls = new ItemBuilder(
                Material.ENDER_PEARL, pearlStartAmount)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Regain 1 ender pearl every " + pearlRegenSpeed + " seconds.").build();

        ItemStack icon = new ItemStack(Material.ENDER_PORTAL_FRAME);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(pearls);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        startItemRunnable(p, pearlRegenSpeed, getPearl(1), pearlMaxAmount, KitType.TELEPORTER);
    }

    public void onPearlLaunch(Player shooter, ProjectileLaunchEvent event) {
        event.getEntity().setCustomName(shooter.getName());
        event.getEntity().setCustomNameVisible(false);

        pearlList.add((EnderPearl) event.getEntity());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (player.getItemInHand().getType() == Material.ENDER_PEARL) {
                if (!addCooldown(event.getPlayer(), getName(), 10, true) || frozenPlayers.containsKey(event.getPlayer().getUniqueId())) {
                    event.setCancelled(true);
                    return;
                }

                //For missions
                KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(player, this.getKitType());
                Bukkit.getServer().getPluginManager().callEvent(abilityEvent);
            }
        }
    }

    private ItemStack getPearl(int amount) {
        ItemStack pearlRegen = new ItemStack(pearls);
        pearlRegen.setAmount(amount);

        return pearlRegen;
    }

    @Override
    public void reimburseItem(Player p, ItemStack item) {
        int count = -1;
        for (ItemStack itemStack : p.getInventory()) {
            if (itemStack != null && item != null && item.getType() == itemStack.getType() && item.getDurability() == itemStack.getDurability()) {
                count += itemStack.getAmount();
            }
        }

        if (item != null && item.getType() == getPearl(item.getAmount()).getType() && count < pearlMaxAmount) {
            Inventory inv = p.getInventory();
            int amount = 0;
            ItemStack newItem = getPearl(1);

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
                if (amount < pearlMaxAmount)
                    inv.setItem(finalSlot, new ItemStack(invItem.getType(), invItem.getAmount() + 1));
            } else
                p.getInventory().addItem(newItem);
        }
    }

    public void removeSummon(int seconds, Player p) {
        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> {
            for (EnderPearl pearl : pearlList)
                if (pearl.getCustomName().contains(p.getName())) {
                    pearl.remove();
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
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Poof!",
                "",
                ChatColor.GRAY + "Has ender pearls to teleport around."
        );
    }
}
