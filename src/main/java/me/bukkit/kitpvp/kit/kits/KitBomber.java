package me.bukkit.kitpvp.kit.kits;

import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KitBomber extends Kit {

	public KitBomber(KitManager kitManager) {
		super(kitManager, "Bomber", KitType.BOMBER, 7000, "Bombs away!");
		setIcon(new ItemStack(Material.TNT));
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level == 3 ? 1 : 0).build());
		p.getInventory().addItem(getTnt(p, level == 3 ? 12 : 10, level));
		
		ItemStack[] armor = getArmour(Material.LEATHER_HELMET, 12, level == 1 ? 1 : 2, Color.RED);
		armor[3] = new ItemBuilder(Material.LEATHER_HELMET).addEnchantment(Enchantment.DURABILITY, 10).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level == 1 ? 1 : 2).setColour(Color.WHITE).build();
		for (ItemStack piece : armor)
			piece.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 5);
		p.getInventory().setArmorContents(armor);
		
		p.addPotionEffects(Collections.singletonList(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, level == 1 ? 0 : 1)));
		startItemRunnable(p, 20 - (level * 5), getTnt(p, 1, level), 10, KitType.BOMBER);
	}
	
	private ItemStack getTnt(Player p, int amount, int level) {
		return new ItemBuilder(Material.TNT, amount).setName("§bTNT").addLore("§F" + "Regain 1 tnt every " + (20 - (level * 5)) + " seconds").build();
	}
	
	@Override
	public void onItemUse(Player p, ItemStack item) {
		if (item.getType() != Material.TNT)
			return; 
		Location loc = p.getEyeLocation();
		TNTPrimed tnt = (TNTPrimed) loc.getWorld().spawnEntity(loc.add(loc.getDirection()), EntityType.PRIMED_TNT);
		tnt.setVelocity(loc.getDirection().multiply(1D));
		tnt.setCustomName(p.getName());
		tnt.setFuseTicks(30);

		p.getWorld().playEffect(p.getLocation(), Effect.CLICK1, 1);
		if (p.getInventory().getItemInHand().getAmount() - 1 >= 1)
			p.getInventory().setItemInHand(getTnt(p, p.getInventory().getItemInHand().getAmount() - 1, getLevel(p)));
		else 
			p.getInventory().remove(p.getItemInHand());
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7You can fire tnt", "§7you will regain tnt overtime", "§7you will get tnt faster if you're a higher level");
	}

}
