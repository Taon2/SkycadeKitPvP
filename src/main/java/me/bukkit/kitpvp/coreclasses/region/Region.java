package me.bukkit.kitpvp.coreclasses.region;

import me.bukkit.kitpvp.coreclasses.utils.Callback;
import me.bukkit.kitpvp.coreclasses.utils.UtilBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Region {

	private String name;
	private DataPoint point1, point2;
	private Callback<Player> entry, exit;

	public Region(String name, DataPoint point1, DataPoint point2) {
		this.name = name;
		int minX = point1.getX() < point2.getX() ? point1.getX() : point2.getX();
		int maxX = point1.getX() > point2.getX() ? point1.getX() : point2.getX();
		int minY = point1.getY() < point2.getY() ? point1.getY() : point2.getY();
		int maxY = point1.getY() > point2.getY() ? point1.getY() : point2.getY();
		int minZ = point1.getZ() < point2.getZ() ? point1.getZ() : point2.getZ();
		int maxZ = point1.getZ() > point2.getZ() ? point1.getZ() : point2.getZ();
		this.point1 = new DataPoint(minX, minY, minZ);
		this.point2 = new DataPoint(maxX, maxY, maxZ);
	}

	public List<Block> getBlocks(World world) {
		return UtilBlock.getBlocks(point1.getBlock(world), point2.getBlock(world));
	}

	@Override
	public String toString() {
		return name + ":" + point1.toString() + ":" + point2.toString();
	}

	public String toStringFormatted() {
		return name + " " + point1.toStringFormatted() + ", " + point2.toStringFormatted();
	}

	public DataPoint getMidpoint() {
		return new DataPoint((point1.getX() + point2.getX()) / 2, (point1.getY() + point2.getY()) / 2, (point1.getZ() + point2.getZ()) / 2);
	}

	public String getName() {
		return name;
	}

	public DataPoint getPoint1() {
		return point1;
	}

	public DataPoint getPoint2() {
		return point2;
	}

	public List<Player> getPlayers(World world) {
		List<Player> players = new ArrayList<>();
		int minX = point1.getX(), minY = point1.getY(), minZ = point1.getZ(), maxX = point2.getX(), maxY = point2.getY(), maxZ = point2.getZ();
		for (Player pl : world.getPlayers()) {
			Block block = pl.getLocation().getBlock();
			int x = block.getX(), y = block.getY(), z = block.getZ();
			if (x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ)
				players.add(pl);
		}
		return players;
	}

	public List<Entity> getEntities(World world) {
		List<Entity> entities = new ArrayList<>();
		int minX = point1.getX(), minY = point1.getY(), minZ = point1.getZ(), maxX = point2.getX(), maxY = point2.getY(), maxZ = point2.getZ();
		for (Entity entity : world.getEntities()) {
			Block block = entity.getLocation().getBlock();
			int x = block.getX(), y = block.getY(), z = block.getZ();
			if (x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ)
				entities.add(entity);
		}
		return entities;
	}

	public boolean contains(Entity entity) {
		return contains(entity.getLocation());
	}

	public boolean contains(Location loc) {
		int minX = point1.getX(), minY = point1.getY(), minZ = point1.getZ(), maxX = point2.getX(), maxY = point2.getY(), maxZ = point2.getZ();
		double x = loc.getX(), y = loc.getY(), z = loc.getZ();
		return x >= minX && x <= maxX + 1 && y >= minY && y <= maxY + 1 && z >= minZ && z <= maxZ + 1;
	}

	public Callback<Player> getEntry() {
		return entry;
	}

	public void setEntry(Callback<Player> entry) {
		this.entry = entry;
	}

	public Callback<Player> getExit() {
		return exit;
	}

	public void setExit(Callback<Player> exit) {
		this.exit = exit;
	}

	public void fill(World world, Material material) {
		fill(world, material, (byte) 0);
	}

	@SuppressWarnings("deprecation")
	public void fill(World world, Material material, byte data) {
		getBlocks(world).forEach(b -> b.setTypeIdAndData(material.getId(), data, false));
	}

}