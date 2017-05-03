package me.bukkit.kitpvp.kit.kits;

import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.coreclasses.utils.ParticleEffect;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

//This kit is broken and got disabled.
public class KitGolem extends Kit {

	public KitGolem(KitManager kitManager) {
		super(kitManager, "Golem", KitType.GOLEM, 37000, false, "Smash the ground to cause players and blocks to fly!");
		setIcon(Material.IRON_BLOCK);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.WOOD_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level + 3).build());

		p.getInventory().setArmorContents(getArmour(Material.IRON_HELMET, 0, 0));
		if (level == 3)
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
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
		    p.sendMessage(ChatColor.RED + "You can't use your ability here");
		    return;
		}
		int level = getLevel(p);
		if (!addCooldown(p, getName(), 30 - (level * 5), true))
			return;

		List<Block> blocks = golemBlocks(p);
		blocks = blocks.stream()
				.filter(block -> Arrays.asList(Material.DIRT, Material.GRASS, Material.GRAVEL, Material.STONE, Material.COBBLESTONE, Material.SAND, Material.BEDROCK, Material.HAY_BLOCK).contains(block.getType()))
				.collect(Collectors.toList());

		blocks.remove(p.getLocation().subtract(0, 1, 0).getBlock());
		if (blocks.size() < 2) {
			p.sendMessage(ChatColor.RED + "You can't use your ability here");
			removeCooldowns(p);
			return;
		}

		List<Entity> entities = (List<Entity>) blocks.get(blocks.size() / 2).getLocation().getWorld().getNearbyEntities(blocks.get(blocks.size() / 2).getLocation(), 5D, 5D, 5D);
		entities = entities.stream().filter(en -> en instanceof Player).collect(Collectors.toList());

		entities.forEach(entity -> {
			if (entity != p) {
				Vector v = entity.getLocation().toVector().subtract(p.getLocation().toVector());
				entity.setVelocity(v);
				entity.teleport(entity.getLocation().add(0, 2, 0));
				if (entity != p)
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
		Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> p.teleport(playerLoc.add(0, 1.5, 0)), 3);
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
