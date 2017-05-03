package me.bukkit.kitpvp.coreclasses.utils;

import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class UtilBlock {

	public static List<Block> getBlocks(Block block1, Block block2) {
		List<Block> blocks = new ArrayList<>();
		int minX = block1.getX() > block2.getX() ? block2.getX() : block1.getX();
		int maxX = block1.getX() < block2.getX() ? block2.getX() : block1.getX();
		int minY = block1.getY() > block2.getY() ? block2.getY() : block1.getY();
		int maxY = block1.getY() < block2.getY() ? block2.getY() : block1.getY();
		int minZ = block1.getZ() > block2.getZ() ? block2.getZ() : block1.getZ();
		int maxZ = block1.getZ() < block2.getZ() ? block2.getZ() : block1.getZ();
		for (int x = minX; x <= maxX; x++)
			for (int z = minZ; z <= maxZ; z++)
				for (int y = minY; y <= maxY; y++) {
					Block block = block1.getWorld().getBlockAt(x, y, z);
					blocks.add(block);
				}
		return blocks;
	}

}