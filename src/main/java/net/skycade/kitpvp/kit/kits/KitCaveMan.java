package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static java.lang.Integer.parseInt;

public class KitCaveMan extends Kit {

    private int blockCounter = 0;
    private final List<UUID> abilityCooldown = new ArrayList<>();

    public KitCaveMan(KitManager kitManager) {
        super(kitManager, "Caveman", KitType.CAVEMAN, 7000, "Crazy caveman guy!");

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "WOOD_SPADE");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 7000);

        defaultsMap.put("inventory.spade.material", "WOOD_SPADE");
        defaultsMap.put("inventory.spade.enchantments.durability", 7);
        defaultsMap.put("inventory.spade.enchantments.damage-all", 5);

        defaultsMap.put("armor.chestplate.material", "LEATHER");
        defaultsMap.put("armor.chestplate.enchantments.durability", 12);
        defaultsMap.put("armor.chestplate.enchantments.protection", 2);

        defaultsMap.put("armor.leggings.material", "LEATHER");
        defaultsMap.put("armor.leggings.enchantments.durability", 12);
        defaultsMap.put("armor.leggings.enchantments.protection", 3);

        defaultsMap.put("armor.boots.material", "IRON");
        defaultsMap.put("armor.boots.enchantments.durability", 0);
        defaultsMap.put("armor.boots.enchantments.protection", 1);

        defaultsMap.put("potions.pot1", "DAMAGE_RESISTANCE:0");

        setConfigDefaults(defaultsMap);

        if (getConfig().getString("kit.icon.material") != null) {
            if (getConfig().getString("kit.icon.material").contains("LEATHER")) {
                setIcon(new ItemBuilder(Material.getMaterial(getConfig().getString("kit.icon.material").toUpperCase()))
                        .setColour(getColor(getConfig().getString("kit.icon.color"))).build());
            } else {
                setIcon(new ItemStack(Material.getMaterial(getConfig().getString("kit.icon.material").toUpperCase())));
            }
        } else {
            setIcon(new ItemStack(Material.DIRT));
        }
        setPrice(getConfig().getInt("kit.price"));
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(new ItemBuilder(
                Material.getMaterial(getConfig().getString("inventory.spade.material").toUpperCase()))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.spade.enchantments.durability"))
                .addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.spade.enchantments.damage-all")).build());

        String[] pot1 = getConfig().getString("potions.pot1").split(":");
        p.addPotionEffect(new PotionEffect(
                PotionEffectType.getByName(pot1[0]),
                Integer.MAX_VALUE,
                parseInt(pot1[1])));

        p.getInventory().setChestplate(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.chestplate.material").toUpperCase() + "_CHESTPLATE"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.chestplate.enchantments.durability"))
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.chestplate.enchantments.protection")).build());

        p.getInventory().setLeggings(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.leggings.material").toUpperCase() + "_LEGGINGS"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.leggings.enchantments.durability"))
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.leggings.enchantments.protection")).build());

        p.getInventory().setBoots(new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.boots.material").toUpperCase() + "_BOOTS"))
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.boots.enchantments.durability"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.boots.enchantments.protection")).build());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.WOOD_SPADE)
            return;
        if (abilityCooldown.contains(p.getUniqueId()))
            return;
        abilityCooldown.add(p.getUniqueId());
        Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> abilityCooldown.remove(p.getUniqueId()), 2 * 20);

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
                UtilPlayer.getNearbyPlayers(b.getLocation(), 2).forEach(player -> {
                    if (player != p)
                        if (Arrays.asList(GameMode.SURVIVAL, GameMode.ADVENTURE).contains(player.getGameMode()))
                            player.damage(8 + 3, p);
                });
            }
        }.runTaskTimer(getKitManager().getPlugin(), 0, 2);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        abilityCooldown.remove(e.getPlayer().getUniqueId());
    }

    @Override
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7Use your shovel to throw a dirt block.");
    }

}
