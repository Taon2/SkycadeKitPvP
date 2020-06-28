package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.skycade.kitpvp.Messages.PLAYER_REPAIRED;
import static net.skycade.kitpvp.Messages.REPAIRED;

public class KitBlacksmith extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack armorkits;

    private int anvilDropCooldown = 15;
    private int armorkitCooldown = 4;
    private int armorkitRegenSpeed = 15;
    private int armorkitStartAmount = 2;
    private int armorkitMaxAmount = 4;

    public KitBlacksmith(KitManager kitManager) {
        super(kitManager, "Blacksmith", KitType.BLACKSMITH, 0, getLore());

        helmet = new ItemBuilder(
                Material.CHAINMAIL_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 4).build();
        chestplate = new ItemBuilder(
                Material.CHAINMAIL_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 4).build();
        leggings = new ItemBuilder(
                Material.CHAINMAIL_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 4).build();
        boots = new ItemBuilder(
                Material.IRON_BOOTS).build();
        weapon = new ItemBuilder(
                Material.IRON_PICKAXE)
                .addEnchantment(Enchantment.DURABILITY, 6)
                .addEnchantment(Enchantment.DAMAGE_ALL, 4)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "%click% every " + anvilDropCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "drops 5 anvils in front of you.").build();
        armorkits = new ItemBuilder(
                Material.ANVIL, armorkitStartAmount)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "%click% a player every " + armorkitCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "repairs their armor.")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Regain 1 armor kit every " + armorkitRegenSpeed + " seconds.")
                .setName("Armor Kit").build();

        ItemStack icon = new ItemStack(Material.ANVIL);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(armorkits);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        startItemRunnable(p, armorkitRegenSpeed, getArmorkits(1), armorkitMaxAmount, KitType.BLACKSMITH);
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.IRON_PICKAXE)
            return;
        if (!addCooldown(p, "Anvil Drop", anvilDropCooldown, true))
            return;

        for (int anvilNum = 2; anvilNum <= 6; anvilNum++) {
            org.bukkit.util.Vector vec = p.getLocation().toVector();
            org.bukkit.util.Vector dir = p.getLocation().getDirection();
            vec = vec.add(dir.multiply(anvilNum));
            Location anvilLocation = vec.toLocation(p.getWorld());
            anvilLocation.add(0, 3, 0);

            if (anvilLocation.getBlock().getType() == Material.AIR) {
                // spawn the anvils
                Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!p.isOnline())
                            return;

                        if (anvilLocation.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)
                            anvilLocation.getBlock().setType(Material.ANVIL);
                    }
                }, anvilNum * 5);

                // try to ensure it goes back to something that isnt an anvil
                Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (anvilLocation.getBlock().getType() == Material.ANVIL)
                            anvilLocation.getBlock().setType(Material.AIR);
                    }
                }, 5 * 20);
            }
        }
    }

    @Override
    public void onInteract(Player p, Player target, ItemStack item) {
        if (item.getType() != Material.ANVIL)
            return;
        if (!addCooldown(p, "Repair Armor", armorkitCooldown, true))
            return;

        for (ItemStack armor : target.getInventory().getArmorContents()) {
            if (armor != null) {
                armor.setDurability((short) (armor.getDurability() - UtilMath.getRandom(3, 7)));
            }
        }

        p.playSound(p.getLocation(), Sound.ANVIL_USE, 1f, 1f);
        target.playSound(target.getLocation(), Sound.ANVIL_USE, 1f, 1f);

        if (p.getInventory().getItemInHand().getAmount() - 1 >= 1)
            p.getInventory().setItemInHand(getArmorkits(p.getInventory().getItemInHand().getAmount() - 1));
        else
            p.getInventory().remove(p.getItemInHand());

        PLAYER_REPAIRED.msg(p, "%player%", target.getName());
        REPAIRED.msg(target, "%player%", p.getName());
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock && event.getEntity().isOnGround()) {
            Block block = event.getBlock();
            Material initialMaterial = block.getType();

            Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
                block.setType(initialMaterial);
            }, 40);
        }
    }

    private ItemStack getArmorkits(int amount) {
        ItemStack armorkitRegen = new ItemStack(armorkits);
        armorkitRegen.setAmount(amount);

        return armorkitRegen;
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Prestige to level 25!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.GREEN + "" + ChatColor.BOLD + "Support Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Forged in fire.",
                "",
                ChatColor.GRAY + "%click% drops anvils in front of you.",
                ChatColor.GRAY + "%click% a player with an armor",
                ChatColor.GRAY + "kit repairs their armor slightly."
        );
    }
}
