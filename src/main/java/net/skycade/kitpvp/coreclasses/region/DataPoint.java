package net.skycade.kitpvp.coreclasses.region;

import org.bukkit.World;
import org.bukkit.block.Block;

public class DataPoint {

	private int x, y, z;

	public DataPoint(Block block) {
		this(block.getX(), block.getY(), block.getZ());
	}

	public DataPoint(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public Block getBlock(World world) {
		return world.getBlockAt(x, y, z);
	}

	@Override
	public String toString() {
		return x + "," + y + "," + z;
	}

	public String toStringFormatted() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

	public static DataPoint fromString(String string) {
		if (string == null)
			return null;
		String[] array = string.split(",");
		if (array.length != 3)
			return null;
		try {
			return new DataPoint(Integer.parseInt(array[0]), Integer.parseInt(array[1]), Integer.parseInt(array[2]));
		} catch (NumberFormatException exception) {
			return null;
		}
	}

}