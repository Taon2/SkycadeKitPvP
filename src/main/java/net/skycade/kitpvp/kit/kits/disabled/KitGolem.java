package net.skycade.kitpvp.kit.kits.disabled;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

import static net.skycade.kitpvp.Messages.CANT_USE_HERE;

//This kit is broken and got disabled.
public class KitGolem extends Kit {

    public KitGolem(KitManager kitManager) {
        super(kitManager, "Golem", KitType.GOLEM, 37000, false, "Smash the ground to cause players and blocks to fly!");

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "IRON_BLOCK");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 37000);

        defaultsMap.put("inventory.sword.material", "WOOD_SWORD");
        defaultsMap.put("inventory.sword.enchantments.durability", 5);
        defaultsMap.put("inventory.sword.enchantments.damage-all", 4);

        defaultsMap.put("armor.material", "IRON");
        defaultsMap.put("armor.enchantments.durability", 0);
        defaultsMap.put("armor.enchantments.protection", 0);

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
                Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
                .addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

        p.getInventory().setArmorContents(getArmour(
                Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
                getConfig().getInt("armor.enchantments.durability"),
                getConfig().getInt("armor.enchantments.protection")));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.WOOD_SWORD)
            return;
        if (p.getLocation().subtract(0, 1, 0).getBlock().getType() == Material.AIR)
            return;
        if (p.getLocation().add(0, 4, 0).getBlock().getType() != Material.AIR)
            return;
        if (tntIsNearby(p) || fallingBlockIsNearby(p)) {
            CANT_USE_HERE.msg(p);
            return;
        }
        if (!addCooldown(p, getName(), 30 - 5, true))
            return;

        List<Block> blocks = golemBlocks(p);
        blocks = blocks.stream()
                .filter(block -> Arrays.asList(Material.DIRT, Material.GRASS, Material.GRAVEL, Material.STONE, Material.COBBLESTONE, Material.SAND, Material.BEDROCK, Material.HAY_BLOCK).contains(block.getType()))
                .collect(Collectors.toList());

        blocks.remove(p.getLocation().subtract(0, 1, 0).getBlock());
        if (blocks.size() < 2) {
            CANT_USE_HERE.msg(p);
            removeCooldowns(p, getName());
            return;
        }

        List<Entity> entities = (List<Entity>) blocks.get(blocks.size() / 2).getLocation().getWorld().getNearbyEntities(blocks.get(blocks.size() / 2).getLocation(), 5D, 5D, 5D);
        entities = entities.stream().filter(en -> en instanceof Player).collect(Collectors.toList());

        entities.forEach(entity -> {
            if (entity != p) {
                Vector v = entity.getLocation().toVector().subtract(p.getLocation().toVector());
                entity.setVelocity(v);
                entity.teleport(entity.getLocation().add(0, 2, 0));
                ((Player) entity).damage(15, p);
            }
        });

        blocks.forEach(block -> {
            FallingBlock fallblock = p.getWorld().spawnFallingBlock(block.getLocation().subtract(0, 1, 0), block.getType(), block.getData());
            double vel = 0.4;
            double distance = block.getLocation().distance(p.getLocation());

            while (distance > 1) {
                vel += 0.1;
                distance--;
            }

            fallblock.setVelocity(new Vector(0, vel, 0));
            block.getWorld().getBlockAt(block.getLocation()).setType(Material.AIR);
        });
        Location playerLoc = p.getLocation();
        p.teleport(playerLoc.add(0, 1.5, 0));
        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> p.teleport(playerLoc.add(0, 1.5, 0)), 3);
        shootParticlesFromLoc(p, ParticleEffect.FIREWORKS_SPARK, 200, 1);
    }

    private boolean tntIsNearby(Player p) {
        for (Entity en : p.getNearbyEntities(4, 4, 4))
            if (en instanceof TNTPrimed)
                return true;
        return false;
    }

    private boolean fallingBlockIsNearby(Player p) {
        for (Entity en : p.getNearbyEntities(4, 4, 4))
            if (en instanceof FallingBlock)
                return true;
        return false;
    }

    private List<Block> golemBlocks(Player p) {
        Location loc = p.getLocation().add(p.getLocation().getDirection());
        BlockFace[] blockFace = {BlockFace.NORTH, BlockFace.EAST, BlockFace.EAST_NORTH_EAST, BlockFace.EAST_SOUTH_EAST, BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.NORTH_NORTH_EAST, BlockFace.NORTH_NORTH_WEST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.WEST_NORTH_WEST, BlockFace.WEST_SOUTH_WEST};
        List<Block> blocks = new ArrayList<>();

        for (BlockFace face : blockFace)
            blocks.add(loc.getBlock().getRelative(face));
        return blocks;
    }

    @Override
    public List<String> getAbilityDesc() {
        return Collections.singletonList("§7Use your sword to smash the ground");
    }

}
