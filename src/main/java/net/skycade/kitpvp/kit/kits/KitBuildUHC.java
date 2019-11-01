package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitBuildUHC extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack fishingrod;
    private ItemStack blocks;
    private ItemStack bow;
    private ItemStack arrows;

    private int goldenHeadCooldown = 25;
    private int arrowCooldown = 1;
    private int arrowRegenSpeed = 3;
    private int arrowStartAmount = 16;
    private int arrowMaxAmount = 32;

    private int blockRemoveSpeed = 2;
    private int blockRegenSpeed = 3;
    private int blockStartAmount = 10;
    private int blockMaxAmount = 20;

    private Map<UUID, List<Block>> placed = new HashMap<>();

    public KitBuildUHC(KitManager kitManager) {
        super(kitManager, "BuildUHC", KitType.BUILDUHC, 29000, getLore());

        helmet = new ItemBuilder(
                Material.IRON_HELMET)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Receive 50% more damage.").build();
        chestplate = new ItemBuilder(
                Material.IRON_CHESTPLATE)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Receive 50% more damage.").build();
        leggings = new ItemBuilder(
                Material.IRON_LEGGINGS)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Receive 50% more damage.").build();
        boots = new ItemBuilder(
                Material.IRON_BOOTS)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Receive 50% more damage.").build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.DAMAGE_ALL, 1)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Right clicking every " + goldenHeadCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "grants a golden head effect.").build();
        fishingrod = new ItemBuilder(
                Material.FISHING_ROD)
                .addEnchantment(Enchantment.DURABILITY, 5).build();
        blocks = new ItemBuilder(
                Material.WOOD, blockStartAmount)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Placed blocks are removed after " + blockRemoveSpeed + " seconds.")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Regain 1 block every " + blockRegenSpeed + " seconds.").build();
        bow = new ItemBuilder(
                Material.BOW)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.ARROW_DAMAGE, 2)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Fire 1 arrow every " + arrowCooldown + " seconds.").build();
        arrows = new ItemBuilder(
                Material.ARROW, arrowStartAmount)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Regain 1 arrow every " + arrowRegenSpeed + " seconds.").build();

        ItemStack icon = new ItemStack(Material.WOOD);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(fishingrod);
        p.getInventory().addItem(bow);
        p.getInventory().addItem(blocks);
        p.getInventory().setItem(27, arrows);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        startItemRunnable(p, arrowRegenSpeed, getArrows(1), arrowMaxAmount, KitType.BUILDUHC);
        startItemRunnable(p, blockRegenSpeed, getBlocks(1), blockMaxAmount, KitType.BUILDUHC);
    }

    private ItemStack getArrows(int amount) {
        ItemStack arrowRegen = new ItemStack(arrows);
        arrowRegen.setAmount(amount);

        return arrowRegen;
    }

    private ItemStack getBlocks(int amount) {
        ItemStack blockRegen = new ItemStack(blocks);
        blockRegen.setAmount(amount);

        return blockRegen;
    }

    public void onArrowLaunch(Player shooter, ProjectileLaunchEvent e) {
        if (!addCooldown(shooter, "Bow", arrowCooldown, true)) {
            e.setCancelled(true);
        }
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.IRON_SWORD)
            return;
        if (!addCooldown(p, "Golden Head", goldenHeadCooldown, true)) return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 0));
        p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 240, 0));
    }

    @Override
    public void onBlockPlace(Player p, Block block) {
        if (block.getType() != Material.WOOD)
            return;

        List<Block> blocks;
        if (placed.containsKey(p.getUniqueId()))
            blocks = placed.get(p.getUniqueId());
        else
            blocks = new ArrayList<>();

        blocks.add(block);

        placed.put(p.getUniqueId(), blocks);

        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
            block.getLocation().getBlock().setType(Material.AIR);
        }, blockRemoveSpeed * 20);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (placed.containsKey(event.getPlayer().getUniqueId())) {
            placed.get(event.getPlayer().getUniqueId()).forEach(block -> {
                block.getLocation().getBlock().setType(Material.AIR);
            });
        }
    }

    @Override
    public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
        e.setDamage(e.getDamage() * 1.5);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /eventshop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "9999 Elo!",
                "",
                ChatColor.GRAY + "Can place blocks to escape enemies.",
                ChatColor.GRAY + "Right clicking gives a golden head effect.",
                ChatColor.GRAY + "Heals with potions instead of soups."
        );
    }
}
