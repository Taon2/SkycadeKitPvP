package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class KitTreeEnt extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private int blockCounter = 0;
    private int blockCooldown = 8;

    public KitTreeEnt(KitManager kitManager) {
        super(kitManager, "Tree Ent", KitType.TREEENT, 28000, getLore());

        helmet = new ItemBuilder(
                Material.LEAVES)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addEnchantment(Enchantment.THORNS, 1).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .build();
        weapon = new ItemBuilder(
                Material.STICK)
                .addEnchantment(Enchantment.KNOCKBACK, 1)
                .addEnchantment(Enchantment.DAMAGE_ALL, 5)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Shift + Right clicking every " + blockCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "throws five leaf blocks in a shotgun pattern.").build();

        ItemStack icon = new ItemStack(Material.LEAVES);
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
        if (item.getType() != Material.STICK)
            return;
        if (!addCooldown(p, "Leaf Storm", blockCooldown, true))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        Block currentBlock = p.getLocation().subtract(0, 1, 0).getBlock();
        if (currentBlock.getType() == Material.AIR)
            currentBlock = p.getLocation().subtract(0, 2, 0).getBlock();
        if (currentBlock.getType() == Material.AIR)
            return;

        Location loc = p.getLocation();
        loc.add(0, 1.5, 0);

        for (int i = 0; i < 5; i++) {
            final FallingBlock b = p.getWorld().spawnFallingBlock(p.getEyeLocation(), Material.LEAVES, currentBlock.getData());
            b.setVelocity(loc.getDirection().multiply(1.75).add(new Vector(ThreadLocalRandom.current().nextDouble(), 0, ThreadLocalRandom.current().nextDouble())
                    .subtract(new Vector(ThreadLocalRandom.current().nextDouble(), 0, ThreadLocalRandom.current().nextDouble()))));

            new BukkitRunnable() {
                public void run() {
                    Block block = b.getLocation().subtract(0, 1, 0).getBlock();
                    if (b.isOnGround() || block.getLocation().distance(p.getLocation()) < 0.1 || blockCounter > 20 || b.isOnGround()) {
                        if (b.getLocation().getBlock().getType() == Material.LEAVES)
                            b.getLocation().getBlock().setType(Material.AIR);
                        b.remove();
                        blockCounter = 0;
                        this.cancel();
                        return;
                    }
                    blockCounter++;
                    UtilPlayer.getNearbyPlayers(p, b.getLocation(), 2).forEach(player -> {
                        if (player.getGameMode() == GameMode.SURVIVAL)
                            player.damage(11, p);
                    });
                }
            }.runTaskTimer(getKitManager().getKitPvP(), 0, 2);
        }
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "I am Groot!",
                "",
                ChatColor.GRAY + "Toss leaves at your enemies",
                ChatColor.GRAY + "in a shotgun pattern."
        );
    }
}
