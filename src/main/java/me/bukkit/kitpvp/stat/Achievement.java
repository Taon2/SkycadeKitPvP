package me.bukkit.kitpvp.stat;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public enum Achievement {
	
	KILLS(Material.DIAMOND_SWORD, Arrays.asList(10, 50, 250, 1250, 2500, 5000, 10000)),
	KILLSTREAK(Material.REDSTONE, Arrays.asList(2, 10, 25, 50, 75, 100, 150)),
	DUEL(Material.SKULL_ITEM, Arrays.asList(2, 5, 10, 20, 50, 75, 100)),
	ASSISTS(Material.WOOD_SWORD, Arrays.asList(15, 75, 500, 2500, 5000, 10000, 25000));
	
	private final Material material;
	private final List<Integer> values;
	
	Achievement(Material material, List<Integer> values) {
		this.material = material;
		this.values = values; 
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public ItemStack getIcon() {
		return new ItemStack(material);
	}
	
	public List<Integer> getValues() {
		return values;
	}

}
