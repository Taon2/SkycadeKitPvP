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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class KitCaveMan extends Kit {

    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    private int blockCounter = 0;
    private int blockCooldown = 5;

    public KitCaveMan(KitManager kitManager) {
        super(kitManager, "Caveman", KitType.CAVEMAN, 9000, getLore());

        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 13)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 13)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3).build();
        boots = new ItemBuilder(
                Material.IRON_BOOTS)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .build();
        weapon = new ItemBuilder(
                Material.WOOD_SPADE)
                .addEnchantment(Enchantment.DURABILITY, 7)
                .addEnchantment(Enchantment.DAMAGE_ALL, 5)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "%click% every " + blockCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "throws a dirt block.").build();

        constantEffects.put(PotionEffectType.DAMAGE_RESISTANCE, 0);

        ItemStack icon = new ItemStack(Material.DIRT);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        constantEffects.forEach((effect, amplifier) -> {
            p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
        });
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.WOOD_SPADE)
            return;
        if (!addCooldown(p, getName(), blockCooldown, true))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        Block currentBlock = p.getLocation().subtract(0, 1, 0).getBlock();
        if (currentBlock.getType() == Material.AIR)
            currentBlock = p.getLocation().subtract(0, 2, 0).getBlock();
        if (currentBlock.getType() == Material.AIR)
            return;

        final FallingBlock b = p.getWorld().spawnFallingBlock(p.getEyeLocation(), Material.DIRT, currentBlock.getData());
        b.setVelocity(p.getLocation().getDirection().multiply(1D));

        new BukkitRunnable() {
            public void run() {
                Block block = b.getLocation().subtract(0, 1, 0).getBlock();
                if (b.isOnGround() || block.getLocation().distance(p.getLocation()) < 0.1 || blockCounter > 20 || b.isOnGround()) {
                    if (b.getLocation().getBlock().getType() == Material.DIRT)
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

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Unga Bunga!",
                "",
                ChatColor.GRAY + "Toss dirt at your enemies."
        );
    }
}
