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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;

import static net.skycade.kitpvp.Messages.COPIED_KIT;
import static net.skycade.kitpvp.Messages.COPIED_YOUR_KIT;

public class KitMaster extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack stick;

    private int swapCooldown = 60;
    private int swapLength = 20;

    public KitMaster(KitManager kitManager) {
        super(kitManager, "KitMaster", KitType.KITMASTER, 0, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5)
                .setColour(Color.fromBGR(0, 215, 255)).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.fromBGR(0, 215, 255)).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.fromBGR(0, 215, 255)).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5)
                .setColour(Color.fromBGR(0, 215, 255)).build();
        weapon = new ItemBuilder(
                Material.DIAMOND_SPADE)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.DAMAGE_ALL, 5).build();
        stick = new ItemBuilder(
                Material.STICK)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "%click% a player every " + swapCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "lets you copy their kit for 20 seconds.").build();

        ItemStack icon = new ItemStack(Material.DIAMOND_SPADE);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(stick);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);
    }

    @Override
    public void onInteract(Player p, Player target, ItemStack item) {
        if (item.getType() != Material.STICK)
            return;
        if (!addCooldown(p, getName(), swapCooldown, true))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        Kit targetKit = getKitManager().getKitPvP().getStats(target).getActiveKit().getKit();
        if (targetKit == null || targetKit.getKitType() == KitType.KITMASTER || !targetKit.isEnabled())
            return;

        HashMap<Integer, ItemStack> invItems = new HashMap<>();
        PlayerInventory inv = p.getInventory();
        for (int i = 0; i < inv.getSize(); i++)
            if (inv.getItem(i) != null)
                invItems.put(i, inv.getItem(i));

        ItemStack[] armor = p.getInventory().getArmorContents();
        final int soupAmount = getSoupAmount(inv);
        clearInventory(p);

        targetKit.beginApplyKit(p);
        getKitManager().getKitPvP().getStats(p).setActiveKit(targetKit.getKitType());
        targetKit.giveSoup(p, soupAmount);
        COPIED_KIT.msg(p, "%kit%", targetKit.getName());
        COPIED_YOUR_KIT.msg(target, "%player%", p.getName());
        kitMasterRunnable(p, armor, invItems);
    }

    private void clearInventory(Player p) {
        p.getOpenInventory().close();
        p.getInventory().clear();
        p.getInventory().setArmorContents(new ItemStack[p.getInventory().getArmorContents().length]);
    }

    private void kitMasterRunnable(Player p, ItemStack[] playerArmor, HashMap<Integer, ItemStack> invItems) {
       Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), new BukkitRunnable() {
           @Override
           public void run() {
               if (!KitPvP.getInstance().getSpawnRegion().contains(p) && getKitManager().getKitPvP().getStats(p).getActiveKit() != KitType.KITMASTER) {
                   getKitManager().getKitPvP().getStats(p).getActiveKit().getKit().cancelRunnables(p);
                   getKitManager().getKits().get(KitType.KITMASTER).beginApplyKit(p);
                   getKitManager().getKitPvP().getStats(p).setActiveKit(KitType.KITMASTER);
                   clearInventory(p);

                   p.getInventory().setArmorContents(playerArmor);
                   for (Entry<Integer, ItemStack> entry : invItems.entrySet())
                       p.getInventory().setItem(entry.getKey(), entry.getValue());
                   for (PotionEffect effect : p.getActivePotionEffects())
                       p.removePotionEffect(effect.getType());
               }
           }
        }, swapLength * 20);
    }

    private int getSoupAmount(Inventory inv) {
        int amount = 0;
        for (int i = 0; i < inv.getSize(); i++)
            if (inv.getItem(i) != null)
                if (inv.getItem(i).getType() == Material.MUSHROOM_SOUP)
                    amount++;
        return amount;
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Prestige to level 100!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Jack of all trades.",
                "",
                ChatColor.GRAY + "Use the stick to copy",
                ChatColor.GRAY + "a player's kit."
        );
    }
}
