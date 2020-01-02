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
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitWitchdoctor extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack regen;
    private ItemStack weakness;

    private int potionStartAmount = 1;
    private int potionMaxAmount = 1;
    private int potionRegenSpeed = 15;

    private int chantCooldown = 20;

    public KitWitchdoctor(KitManager kitManager) {
        super(kitManager, "Witchdoctor", KitType.WITCHDOCTOR, 20000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 9)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .setColour(Color.OLIVE).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 9)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .setColour(Color.OLIVE).build();
        leggings = new ItemBuilder(
                Material.IRON_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 9)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 9)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .setColour(Color.OLIVE).build();
        weapon = new ItemBuilder(
                Material.STONE_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.DAMAGE_ALL, 2)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Right clicking every " + chantCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "heals nearby allies and weakens nearby enemies.").build();
        regen = new ItemStack(Material.POTION, potionStartAmount, (short) 16385);
                ItemMeta meta = regen.getItemMeta();
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Regain 1 regeneration potion every " + potionRegenSpeed + " seconds.");
                meta.setLore(lore);
                regen.setItemMeta(meta);
        weakness = new ItemStack(Material.POTION, potionStartAmount, (short) 16424);
                ItemMeta meta2 = weakness.getItemMeta();
                List<String> lore2 = new ArrayList<>();
                lore2.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Regain 1 weakness potion every " + potionRegenSpeed + " seconds.");
                meta2.setLore(lore2);
                weakness.setItemMeta(meta2);

        ItemStack icon = new ItemStack(Material.CAULDRON_ITEM);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(regen);
        p.getInventory().addItem(weakness);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        startItemRunnable(p, potionRegenSpeed, getRegen(1), potionMaxAmount, KitType.WITCHDOCTOR);
        startItemRunnable(p, potionRegenSpeed, getWeakness(1), potionMaxAmount, KitType.WITCHDOCTOR);
    }

    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.STONE_SWORD)
            return;
        if (!addCooldown(p, "Chant", chantCooldown, true))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        Set<Player> targetPlayers = UtilPlayer.getNearbyPlayers(p, p.getLocation(), 7);

        Gang gang = GangsPlusApi.getPlayersGang(p);

        if (gang == null)
            return;

        targetPlayers.forEach(target -> {
            if (gang.getOnlineMembers().contains(target))
                target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 1));
            else if (target != p)
                target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 120, 1));
        });
    }

    private ItemStack getRegen(int amount) {
        ItemStack regenRegen = new ItemStack(regen);
        regenRegen.setAmount(amount);

        return regenRegen;
    }

    private ItemStack getWeakness(int amount) {
        ItemStack weaknessRegen = new ItemStack(weakness);
        weaknessRegen.setAmount(amount);

        return weaknessRegen;
    }

    @Override
    public void reimburseItem(Player p, ItemStack item) {
        if (item != null && item.getType() == Material.POTION && item.getDurability() == 16421) {
            ItemStack newItem = new ItemStack(Material.POTION, 1, (short) 16421);

            p.getInventory().addItem(newItem);
            return;
        }

        //Starts at -1 because the item is still considered in the inventory, and thus counted when it shouldn't be
        int count = -1;
        for (ItemStack itemStack : p.getInventory()) {
            if (itemStack != null && item != null && item.getType() == itemStack.getType() && item.getDurability() == itemStack.getDurability()) {
                count += itemStack.getAmount();
            }
        }

        if (item != null && item.getType() == getWeakness(1).getType() && item.getDurability() == getWeakness(1).getDurability() && count < potionMaxAmount) {
            ItemStack newItem = getWeakness(1);

            p.getInventory().addItem(newItem);
        } else if (item != null && item.getType() == getRegen(1).getType() && item.getDurability() == getRegen(1).getDurability()) {
            ItemStack newItem = getRegen(1);

            p.getInventory().addItem(newItem);
        }
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.GREEN + "" + ChatColor.BOLD + "Support Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "I told the witchdoctor I was in love with you!",
                "",
                ChatColor.GRAY + "Right clicking heals allies",
                ChatColor.GRAY + "and weakens enemies.",
                ChatColor.GRAY + "Heals with potions instead of soups."
        );
    }
}
