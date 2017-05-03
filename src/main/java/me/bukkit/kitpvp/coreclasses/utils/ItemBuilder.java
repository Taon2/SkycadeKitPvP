package me.bukkit.kitpvp.coreclasses.utils;

import me.bukkit.kitpvp.coreclasses.datastructures.Pair;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;

@SuppressWarnings("deprecation")
public class ItemBuilder {

	private final Material material;
	private byte data;
	private int amount;

	private String name;
	private List<String> lore;
	private short durability = -1;
	private boolean glow, unbreakable, hideEnchants, hideAttributes = true;
	private Map<Enchantment, Integer> enchantments;
	private Color colour;

	public ItemBuilder(Material material) {
		this(material, (byte) 0, 1);
	}

	public ItemBuilder(Pair<Material, Byte> pair) {
		this(pair.getLeft(), pair.getRight());
	}

	public ItemBuilder(Material material, byte data) {
		this(material, data, 1);
	}

	public ItemBuilder(Material material, int amount) {
		this(material, (byte) 0, amount);
	}

	public ItemBuilder(Material material, byte data, int amount) {
		this.material = material;
		this.data = data;
		this.amount = amount;
		this.durability = -1;
	}

	public ItemBuilder(ItemStack itemStack) {
		this.material = itemStack.getType();
		this.data = itemStack.getData().getData();
		this.amount = itemStack.getAmount();
		// this.durability = (short) (material.getMaxDurability() -
		// itemStack.getDurability() - (material.getMaxDurability() == 0 ? 1 :
		// 0));
		if (itemStack.hasItemMeta()) {
			if (itemStack.getItemMeta().hasDisplayName())
				name = itemStack.getItemMeta().getDisplayName();
			if (itemStack.getItemMeta().hasLore())
				lore = itemStack.getItemMeta().getLore();
			if (material.toString().startsWith(Material.LEATHER_HELMET.toString().split("_")[0])) {
				LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
				this.colour = leatherArmorMeta.getColor();
			}
		}
	}

	public ItemBuilder setName(String name) {
		this.name = name;
		return this;
	}

	// add blank line
	public ItemBuilder addLore() {
		if (lore == null)
			lore = new ArrayList<>();
		lore.add("");
		return this;
	}

	public ItemBuilder addLore(String... lore) {
		return addLore(Arrays.asList(lore));
	}

	public ItemBuilder addLore(List<String> lore) {
		if (lore == null)
			return this;
		if (this.lore == null)
			this.lore = new ArrayList<>();
		for (String s : lore)
			if (s != null)
				this.lore.add("§f" + s);
		return this;
	}

	public ItemBuilder setGlow(boolean glow) {
		this.glow = glow;
		if (glow && material == Material.GOLDEN_APPLE) {
			data = 1;
			this.glow = false;
		}
		return this;
	}

	public ItemBuilder setColour(Color colour) {
		this.colour = colour;
		return this;
	}

	public ItemBuilder setDurabilityPercentage(double durabilityPercentage) {
		this.durability = (short) (durabilityPercentage * (double) material.getMaxDurability());
		return this;
	}

	public ItemBuilder setDurability(short durability) {
		this.durability = durability;
		return this;
	}

	public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
		if (enchantment == null)
			return this;
		if (enchantments == null)
			enchantments = new HashMap<>();
		if (enchantments.containsKey(enchantment) || level <= 0) {
			enchantments.remove(enchantment);
			return this;
		}
		enchantments.put(enchantment, level);
		return this;
	}

	public ItemStack build() {
		ItemStack itemStack = new ItemStack(material, amount, (short) 0, data);
		ItemMeta meta = itemStack.getItemMeta();
		if (name != null)
			meta.setDisplayName(name);
		if (lore != null)
			meta.setLore(lore);
		if (durability != -1)
			itemStack.setDurability((short) (itemStack.getType().getMaxDurability() - durability));
		if (unbreakable) {
			meta.spigot().setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		}
		if (hideAttributes)
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		if (hideEnchants)
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		itemStack.setItemMeta(meta);
		if (colour != null && material.name().startsWith("LEATHER_")) {
			LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
			leatherArmorMeta.setColor(colour);
			itemStack.setItemMeta(leatherArmorMeta);
		}
		if (glow)
			UtilItem.addGlow(itemStack);
		if (enchantments != null)
			for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet())
				itemStack.addUnsafeEnchantment(enchantment.getKey(), enchantment.getValue());
		return itemStack;
	}

}